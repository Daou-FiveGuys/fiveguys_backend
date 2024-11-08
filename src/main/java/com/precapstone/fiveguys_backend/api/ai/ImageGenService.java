package com.precapstone.fiveguys_backend.api.ai;

import ai.fal.client.FalClient;
import ai.fal.client.Output;
import ai.fal.client.SubscribeOptions;
import ai.fal.client.queue.QueueStatus;
import com.google.gson.JsonObject;
import com.precapstone.fiveguys_backend.api.auth.JwtTokenProvider;
import com.precapstone.fiveguys_backend.api.aws.AwsS3Service;
import com.precapstone.fiveguys_backend.api.aws.ImageLinkExtractor;
import com.precapstone.fiveguys_backend.api.dto.ImageInpaintDTO;
import com.precapstone.fiveguys_backend.api.dto.ImageUpscaleDTO;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.entity.ImageInfo;
import com.precapstone.fiveguys_backend.entity.ImageResult;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


/**
 * Generate image with flux.1 (dev)
 *
 * @author 6-keem
 * @since 2024-11-07
 *
 */
@Service
@RequiredArgsConstructor
public class ImageGenService {

    @Qualifier("taskExecutor")
    private final TaskExecutor taskExecutor;
    private final AwsS3Service awsS3Service;
    private final ImageGenRepository imageGenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private static final FalClient fal = FalClient.withEnvCredentials();

    /**
     * 이미지 생성
     * @param authorization 인증 헤더
     * @param prompt 이미지 생성 프롬프트 (KOR)
     * @return ResponseEntity<CommonResponse> 이미지 정보
     */
    public CommonResponse generate(String authorization, String prompt){
        //TODO if prompt == korean -> translate
        String accessToken = JwtTokenProvider.stripTokenPrefix(authorization);
        String userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        var input = Map.of(
            "prompt", prompt);

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
            //스레드
            this.saveImageResultIntoDB(result, userId);
            return CommonResponse.builder()
                    .code(200)
                    .data(ImageLinkExtractor.extractImageUrl(result.getData()))
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
    public CommonResponse inpaint(String authorization, ImageInpaintDTO imageInpaintDTO){
        String accessToken = JwtTokenProvider.stripTokenPrefix(authorization);
        String userId = jwtTokenProvider.getUserIdFromToken(accessToken);

        String requestId = imageInpaintDTO.getRequestId();
        ImageResult imageResult = imageGenRepository.findImageByOriginalRequestId(requestId).orElseThrow();
        String maskImageUrl = saveMaskImageIntoS3(imageInpaintDTO.getMask(), requestId, userId);

        var input = Map.of(
            "image_url", imageResult.getOriginalImageInfo().getUrl(),
            "mask_url", maskImageUrl,
            "prompt", imageInpaintDTO.getPrompt()
        );

        var result = fal.subscribe("fal-ai/fast-sdxl/inpainting",
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
            this.saveInpaintedImageIntoDB(result, imageResult);
            return CommonResponse.builder()
                    .code(200)
                    .data(ImageLinkExtractor.extractImageUrl(result.getData()))
                    .build();
        } catch (Exception e) {
            return CommonResponse.builder()
                    .code(400)
                    .build();
        }
    }

    public CommonResponse upscale(String authorization, ImageUpscaleDTO imageUpscaleDTO){
        String accessToken = JwtTokenProvider.stripTokenPrefix(authorization);
        String userId = jwtTokenProvider.getUserIdFromToken(accessToken);

        ImageResult imageResult = imageGenRepository.findImageByOriginalRequestId(imageUpscaleDTO.getOriginalRequestId()).orElseThrow();
        String imageUrl = imageResult.getEditedImageInfo() != null ?
                imageResult.getEditedImageInfo().getUrl() : imageResult.getOriginalImageInfo().getUrl();

        var input = Map.of(
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
            this.saveUpscaledImageIntoDB(result, imageResult);
            return CommonResponse.builder()
                    .code(200)
                    .data(ImageLinkExtractor.extractUpscaledImageUrl(result.getData()))
                    .build();
        } catch (Exception e) {
            return CommonResponse.builder()
                    .code(400)
                    .build();
        }
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
            imageGenRepository.save(ImageResult.builder()
                    .userId(userId)
                    .originalRequestId(output.getRequestId())
                    .originalImageInfo(
                            ImageInfo.builder()
                                    .requestId(output.getRequestId())
                                    .url(ImageLinkExtractor.extractImageUrl(output.getData()))
                                    .build()
                    )
                    .editedImageInfo(null)
                    .build());
        } catch (Exception e) {
            // 예외 처리 로깅
            System.err.println("이미지 저장 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     *
     * @param output
     * @param imageResult
     */
    @Async
    @Transactional
    public void saveInpaintedImageIntoDB(Output<JsonObject> output, ImageResult imageResult) {
        try {
            System.out.println(Thread.currentThread().getName());
            imageResult.setEditedImageInfo(ImageInfo.builder()
                    .requestId(output.getRequestId())
                    .url(ImageLinkExtractor.extractImageUrl(output.getData()))
                    .build());
            imageGenRepository.save(imageResult);
        } catch (Exception e) {
            System.err.println("이미지 저장 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     *
     * @param output
     * @param imageResult
     */
    @Async
    @Transactional
    public void saveUpscaledImageIntoDB(Output<JsonObject> output, ImageResult imageResult) {
        try {
            System.out.println(Thread.currentThread().getName());
            imageResult.setEditedImageInfo(ImageInfo.builder()
                    .requestId(output.getRequestId())
                    .url(ImageLinkExtractor.extractUpscaledImageUrl(output.getData()))
                    .build());
            imageGenRepository.save(imageResult);
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
    //TODO 선택한 경우 S3에 넣음
    @Async
    public void saveImageToS3(String requestId, String userId) {
        try {
            ImageResult imageResult = imageGenRepository.findImageByOriginalRequestId(requestId).orElseThrow();

            String imageUrl = imageResult.getOriginalImageInfo().getUrl();
            System.out.println(Thread.currentThread().getName());
            // 이미지 생성, 저장 (접속 불가)
            String bucketUrl = awsS3Service.upload(imageUrl, requestId);
            // 24시간 임시 링크 (접속 가능) 레디스에 등록
            awsS3Service.getUrl(userId, requestId);

            ImageInfo originalImageInfo = imageResult.getOriginalImageInfo();
            originalImageInfo.setUrl(bucketUrl);
            imageGenRepository.save(
                    imageResult.setOriginalImageInfo(originalImageInfo)
            );

        } catch (Exception e) {
            // 예외 처리 로깅
            System.err.println("이미지 저장 중 오류 발생: " + e.getMessage());
        }
    }
}
