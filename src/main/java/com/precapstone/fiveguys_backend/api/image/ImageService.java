package com.precapstone.fiveguys_backend.api.image;

import ai.fal.client.FalClient;
import ai.fal.client.Output;
import ai.fal.client.SubscribeOptions;
import ai.fal.client.queue.QueueStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.precapstone.fiveguys_backend.api.auth.JwtTokenProvider;
import com.precapstone.fiveguys_backend.api.aws.AwsS3Service;
import com.precapstone.fiveguys_backend.api.aws.ImageLinkExtractor;
import com.precapstone.fiveguys_backend.api.dto.ImageInpaintDTO;
import com.precapstone.fiveguys_backend.api.dto.ImageResponseDTO;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.retrofit.ImggenRetrofitClient;
import com.precapstone.fiveguys_backend.common.retrofit.PhotoroomRetrofitClient;
import com.precapstone.fiveguys_backend.entity.Image;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import retrofit2.Call;
import retrofit2.Response;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;


/**
 * Generate image with flux.1 (dev)
 *
 * @author 6-keem
 * @since 2024-11-07
 *
 */
@Service
@RequiredArgsConstructor
public class ImageService {

    private final AwsS3Service awsS3Service;
    private final ImageRepository imageRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private static final FalClient fal = FalClient.withEnvCredentials();

    @Value("${spring.photoroom.api-key}")
    private String PHOTOROOM_API_KEY;
    @Value("${spring.imggen.api-key}")
    private String IMGGEN_API_KEY;

    private String WATER_COLOR_LORA_LINK;
    private String JAPANESE_STYLE_LORA_LINK;


    /**
     * 이미지 생성
     *
     * @param authorization 인증 헤더
     * @return ResponseEntity<CommonResponse> 이미지 정보
     */
    public CommonResponse generate(String authorization, ImageGenerateDTO imageGenerateDTO){
        String accessToken = JwtTokenProvider.stripTokenPrefix(authorization);
        String userId = jwtTokenProvider.getUserIdFromToken(accessToken);

        var input = Map.of(
            "prompt", imageGenerateDTO);

        var result = fal.subscribe("fal-ai/flux/dev",
            SubscribeOptions.<JsonObject>builder()
                    .input(input)
                    .logs(true)
                    .resultType(JsonObject.class)
                    .onQueueUpdate(update -> {
                        if (update instanceof QueueStatus.InProgress) {
                            System.out.println(((QueueStatus.InProgress) update).getLogs());
                        }
                    })
                    .build());

        try {
            this.saveImageResultIntoDB(result, userId);
            return CommonResponse.builder()
                    .code(200)
                    .data(ImageResponseDTO.builder()
                            .requestId(result.getRequestId())
                            .url(ImageLinkExtractor.extractImageUrl(result.getData()))
                            .build())
                    .build();
        } catch (Exception e) {
            return CommonResponse.builder()
                    .code(400)
                    .build();
        }
    }

    /**
     * 이미지 생성
     *
     * @param authorization 인증 헤더
     * @return ResponseEntity<CommonResponse> 이미지 정보
     */
    public CommonResponse generateWithLora(String authorization, ImageGenerateDTO imageGenerateDTO){
        String accessToken = JwtTokenProvider.stripTokenPrefix(authorization);
        String userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        String style = imageGenerateDTO.lora;
        String loraLink = "";

        switch (style){
            case "water-color" -> loraLink = WATER_COLOR_LORA_LINK;
            case "japanese-style" -> loraLink = JAPANESE_STYLE_LORA_LINK;
            default -> {
                return CommonResponse.builder()
                    .code(400)
                    .build();
            }
        }

        var input = Map.of(
            "prompt", imageGenerateDTO.prompt,
            "image_size", imageGenerateDTO.imageSize,
            "num_inference_steps", imageGenerateDTO.numInterfaceSteps,
            "guidance_scale", imageGenerateDTO.cfg,
            "loras", List.of(
                    Map.of("path", loraLink, "scale", 1.0)
            ),
            "output_format", "jpeg"
        );

        var result = fal.subscribe("fal-ai/flux-lora",
                SubscribeOptions.<JsonObject>builder()
                        .input(input)
                        .logs(true)
                        .resultType(JsonObject.class)
                        .onQueueUpdate(update -> {
                            if (update instanceof QueueStatus.InProgress) {
                                System.out.println(((QueueStatus.InProgress) update).getLogs());
                            }
                        })
                        .build());

        try {
            this.saveImageResultIntoDB(result, userId);
            return CommonResponse.builder()
                    .code(200)
                    .data(ImageResponseDTO.builder()
                            .requestId(result.getRequestId())
                            .url(ImageLinkExtractor.extractImageUrl(result.getData()))
                            .build())
                    .build();
        } catch (Exception e) {
            return CommonResponse.builder()
                    .code(400)
                    .build();
        }
    }


    /**
     * 이미지 수정
     *
     * @param authorization 인증 헤더
     * @param imageInpaintDTO 인페인팅 파라미터 (MultipartFile, requestId, prompt)
     * @return CommonResponse
     */
    public CommonResponse inpaint(String authorization, ImageInpaintDTO imageInpaintDTO, MultipartFile multipartFile){
        String accessToken = JwtTokenProvider.stripTokenPrefix(authorization);
        String userId = jwtTokenProvider.getUserIdFromToken(accessToken);

        String requestId = imageInpaintDTO.getRequestId();
        Image image = imageRepository.findImageByRequestId(requestId).orElseThrow();
        String maskImageUrl = saveMaskImageIntoS3(multipartFile, requestId, userId);

        var input = Map.of(
            "guidance_scale", "8",
            "strength", 0.6,
            "image_url", image.getUrl(),
            "mask_url", maskImageUrl,
            "prompt", imageInpaintDTO.getPrompt(),
            "num_inference_steps" , 50,
            "image_size", Map.of(
                "width", imageInpaintDTO.getWidth(),
                "height", imageInpaintDTO.getHeight()
            )
        );

        try {
            var result = fal.subscribe("fal-ai/flux-lora/inpainting",
                    SubscribeOptions.<JsonObject>builder()
                            .input(input)
                            .logs(true)
                            .resultType(JsonObject.class)
                            .onQueueUpdate(update -> {
                                if (update instanceof QueueStatus.InProgress) {
                                    System.out.println(((QueueStatus.InProgress) update).getLogs());
                                }
                            })
                            .build());
            this.saveInpaintedImageIntoDB(result, image);
            return CommonResponse.builder()
                    .code(200)
                    .data(
                        ImageResponseDTO.builder()
                                .requestId(result.getRequestId())
                                .url(ImageLinkExtractor.extractImageUrl(result.getData()))
                                .build())
                    .build();
        } catch (Exception e) {
            return CommonResponse.builder()
                    .code(400)
                    .build();
        }
    }

    public CommonResponse upscale(String authorization, String requestId){
        String accessToken = JwtTokenProvider.stripTokenPrefix(authorization);
        String userId = jwtTokenProvider.getUserIdFromToken(accessToken);

        Image image = imageRepository.findImageByRequestId(requestId).orElseThrow();
        String imageUrl = image.getUrl();

        var input = Map.of(
            "scale", 2,
            "model", "RealESRGAN_x2plus",
            "face", false,
            "image_url", imageUrl
        );
        var result = fal.subscribe("fal-ai/esrgan",
                SubscribeOptions.<JsonObject>builder()
                        .input(input)
                        .logs(true)
                        .resultType(JsonObject.class)
                        .onQueueUpdate(update -> {
                            if (update instanceof QueueStatus.InProgress) {
                                System.out.println(((QueueStatus.InProgress) update).getLogs());
                            }
                        })
                        .build()
        );

        try {
            this.saveUpscaledImageIntoDB(result, image);
            return CommonResponse.builder()
                    .code(200)
                    .data(ImageResponseDTO.builder()
                            .requestId(result.getRequestId())
                            .url(ImageLinkExtractor.extractUpscaledImageUrl(result.getData()))
                            .build())
                    .build();
        } catch (Exception e) {
            return CommonResponse.builder()
                    .code(400)
                    .build();
        }
    }

    public CommonResponse removeTextByPhotoRoom(String authorization, String requestId){
        String accessToken = JwtTokenProvider.stripTokenPrefix(authorization);
        String userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        Image image = imageRepository.findImageByRequestId(requestId).orElseThrow();

        PhotoroomApiService apiService = PhotoroomRetrofitClient.create();
        String imageUrl = image.getUrl();
        Call<ResponseBody> call = apiService.removeTextFromImage(
                PHOTOROOM_API_KEY,
                false,
                "originalImage",
                "ai.all",
                imageUrl
        );

        try {
            ResponseBody responseBody = call.execute().body();

            if (responseBody != null) {
                String newRequestId = requestId + "-photoroom-text-removed";
                String bucketUrl = awsS3Service.upload(responseBody.byteStream(), newRequestId + ".png");
                return getObjectCommonResponse(requestId, newRequestId, userId, bucketUrl);
            } else {
                System.err.println("Response body is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CommonResponse.builder()
                .code(400)
                .build();
    }

    public CommonResponse removeTextByImggen(String authorization, String requestId) {
        String accessToken = JwtTokenProvider.stripTokenPrefix(authorization);
        String userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        Image image = imageRepository.findImageByRequestId(requestId).orElseThrow();

        ImgGenApiService apiService = ImggenRetrofitClient.create();
        String imageUrl = image.getUrl();
        File downloadedFile = null;
        try {
            // Step 1: Download file from URL
            downloadedFile = downloadFileFromUrl(imageUrl);
            RequestBody requestBody = RequestBody.create(
                    downloadedFile,
                    MediaType.parse("image/jpeg") // Adjust the MIME type based on your file format
            );
            MultipartBody.Part body = MultipartBody.Part.createFormData(
                    "image",
                    downloadedFile.getName(),
                    requestBody
            );

            // Step 3: Call API
            Call<ResponseBody> call = apiService.removeTextFromImage(IMGGEN_API_KEY, body);
            Response<ResponseBody> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map responseMap = objectMapper.readValue(response.body().string(), Map.class);
                String base64Image = (String) responseMap.get("image");

                // Step 3: Base64 디코딩 후 InputStream 생성
                byte[] decodedBytes = Base64.getDecoder().decode(base64Image);
                new ByteArrayInputStream(decodedBytes);

                String newRequestId = requestId + "-imggen-text-removed";
                String bucketUrl = awsS3Service.upload(new ByteArrayInputStream(decodedBytes), newRequestId + ".png");
                return getObjectCommonResponse(requestId, newRequestId, userId, bucketUrl);
            } else {
                return CommonResponse.builder()
                        .code(400)
                        .message("API call failed")
                        .build();
            }
        } catch (Exception e) {
            return CommonResponse.builder()
                    .code(400)
                    .message("API call failed")
                    .build();
        } finally {
            // Clean up downloaded file
            if (downloadedFile != null && downloadedFile.exists()) {
                downloadedFile.delete();
            }
        }
    }

    private CommonResponse<Object> getObjectCommonResponse(String requestId, String newRequestId, String userId, String bucketUrl) {
        Optional<Image> existingImage = imageRepository.findImageByRequestId(newRequestId);
        Image newImage = existingImage.orElse(Image.builder()
                .parentRequestId(requestId)
                .requestId(newRequestId)
                .userId(userId)
                .url(bucketUrl)
                .build());

        if (existingImage.isPresent()) {
            newImage.setParentRequestId(requestId);
            newImage.setUserId(userId);
            newImage.setUrl(bucketUrl);
        }

        // 데이터 저장 (기존 데이터는 업데이트, 없던 데이터는 새로 삽입)
        imageRepository.save(newImage);
        String url = awsS3Service.getUrl(userId, bucketUrl);
        return CommonResponse.builder()
                .code(200)
                .data(ImageResponseDTO.builder()
                        .requestId(newRequestId)
                        .url(url)
                        .build()
                )
                .build();
    }

    public CommonResponse getImage(String authorization, String requestId){
        String accessToken = JwtTokenProvider.stripTokenPrefix(authorization);
        String userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        Image image = imageRepository.findImageByRequestId(requestId).orElseThrow();

        if(!userId.equals(image.getUserId()))
            return CommonResponse.builder()
                    .code(400)
                    .message("Bad request")
                    .build();

        return CommonResponse.builder()
                .code(200)
                .data(ImageResponseDTO.builder()
                        .requestId(image.getRequestId())
                        .url(image.getUrl())
                        .build())
                .build();

    }

    public CommonResponse getAllImages(String authorization){
        String accessToken = JwtTokenProvider.stripTokenPrefix(authorization);
        String userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        List<Image> image = imageRepository.findImagesByUserId(userId);
        List<ImageResponseDTO> responses = new ArrayList<>();

        for (Image i : image) {
            responses.add(ImageResponseDTO.builder()
                    .requestId(i.getRequestId())
                    .url(i.getUrl())
                    .build());
        }

        return CommonResponse.builder()
                .code(200)
                .data(responses)
                .build();
    }

    private File downloadFileFromUrl(String fileUrl) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to download file: HTTP " + connection.getResponseCode());
        }

        File tempFile = File.createTempFile("downloaded-", ".jpg"); // Adjust extension if needed
        try (InputStream inputStream = connection.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        System.out.println("File downloaded to: " + tempFile.getAbsolutePath());
        return tempFile;
    }

    /**
     *
     * @param multipartFile
     * @param requestId
     * @param userId
     * @return
     */
    public String saveMaskImageIntoS3(MultipartFile multipartFile, String requestId, String userId) {
        requestId  = requestId + "-mask";
        // 이미지 생성, 저장 (접속 불가)
        awsS3Service.upload(multipartFile, requestId);
        // 24시간 임시 링크 (접속 가능) 레디스에 등록
        return awsS3Service.getUrl(userId, requestId);
    }

    /**
     * 스레드로 응답 지연 방지, S3 사용량 감소를 위해 생성된 이미지 링크는 모두 DB에 저장
     *
     * @param output 결과
     * @param userId 유저 아이디
     */
    //TODO 이미지 선택하지 않았을 경우 쓰레기 값 DB에 남음
    @Async
    @Transactional
    public void saveImageResultIntoDB(Output<JsonObject> output, String userId) {
        try {
            System.out.println(Thread.currentThread().getName());
            imageRepository.save(Image.builder()
                    .parentRequestId(null)
                    .requestId(output.getRequestId())
                    .url(ImageLinkExtractor.extractImageUrl(output.getData()))
                    .userId(userId)
                    .build());
        } catch (Exception e) {
            // 예외 처리 로깅
            System.err.println("이미지 저장 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     *
     * @param output
     * @param image
     */
    @Async
    @Transactional
    public void saveInpaintedImageIntoDB(Output<JsonObject> output, Image image) {
        try {
            System.out.println(Thread.currentThread().getName());
            imageRepository.save(Image.builder()
                    .url(ImageLinkExtractor.extractImageUrl(output.getData()))
                    .requestId(output.getRequestId())
                    .parentRequestId(image.getRequestId())
                    .userId(image.getUserId())
                    .build());
        } catch (Exception e) {
            System.err.println("이미지 저장 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * @param output
     * @param image
     */
    @Async
    @Transactional
    public void saveUpscaledImageIntoDB(Output<JsonObject> output, Image image) {
        try {
            System.out.println(Thread.currentThread().getName());
            imageRepository.save(Image.builder()
                    .url(ImageLinkExtractor.extractUpscaledImageUrl(output.getData()))
                    .requestId(output.getRequestId())
                    .parentRequestId(image.getRequestId())
                    .userId(image.getUserId())
                    .build());
            imageRepository.save(image);
        } catch (Exception e) {
            System.err.println("이미지 저장 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 사용자 선택한 이미지를 S3에 저장, 임시 링크 생성하여 레디스에 등록
     *
     * @param requestId fal.ai 요청 주소 (DB에 존재)
     * @param userId 유저 아이디
     */
    @Async
    public void saveImageToS3(String requestId, String userId) {
        try {
            Image image = imageRepository.findImageByRequestId(requestId).orElseThrow();

            String imageUrl = image.getUrl();
            System.out.println(Thread.currentThread().getName());
            // 이미지 생성, 저장 (접속 불가)
            String bucketUrl = awsS3Service.upload(imageUrl, requestId);
            // 24시간 임시 링크 (접속 가능) 레디스에 등록
            awsS3Service.getUrl(userId, requestId);

            imageRepository.save(
                    image.setUrl(bucketUrl)
            );

        } catch (Exception e) {
            // 예외 처리 로깅
            System.err.println("이미지 저장 중 오류 발생: " + e.getMessage());
        }
    }
}
