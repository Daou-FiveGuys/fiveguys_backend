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
import com.precapstone.fiveguys_backend.api.dto.ImageResponseDTO;
import com.precapstone.fiveguys_backend.api.dto.ImageUpscaleDTO;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.entity.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;


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
            this.imageSaveToDB(result, userId);
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
     * 이미지 저장
     * @param authorization 인증 헤더
     * @param requestId 이미지 요청 코드
     * @return ResponseEntity<CommonResponse> 이미지 정보
     */
    public CommonResponse upload(String authorization, String requestId){
        //TODO if prompt == korean -> translate
        String accessToken = JwtTokenProvider.stripTokenPrefix(authorization);
        String userId = jwtTokenProvider.getUserIdFromToken(accessToken);

        //TODO 리턴하고 저장
        try {
            //스레드
            this.imageSaveToS3(requestId, userId);

            return CommonResponse.builder()
                    .code(200)
                    .build();
        } catch (Exception e) {
            return CommonResponse.builder()
                    .code(400)
                    .build();
        }
    }

    public CommonResponse inpaint(String authorization, ImageInpaintDTO imageInpaintDTO){
        String accessToken = JwtTokenProvider.stripTokenPrefix(authorization);
        String userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        //TODO 이미지, 마스킹 이미지 링크 생성
        String originalImageUrl = "";
        String maskImageUrl = "";
        var input = Map.of(
            "image_url", originalImageUrl,
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

        return CommonResponse.builder()
                .code(200)
                .data(ImageResponseDTO.builder()
                        .image(result)
                        .imageId(imageInpaintDTO.getImageId())
                        .build())
                .build();
    }

    public CommonResponse upscale(String authorization, ImageUpscaleDTO imageUpscaleDTO){
        String accessToken = JwtTokenProvider.stripTokenPrefix(authorization);
        String userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        String imageUrl = "";
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

        return CommonResponse.builder()
                .code(200)
                .data(ImageResponseDTO.builder()
                        .image(result)
                        .imageId(imageUpscaleDTO.getImageId())
                        .build())
                .build();
    }

    //TODO 이미지 선택하지 않았을 경우 쓰레기 값 DB에 남음
    @Async
    public void imageSaveToDB(Output<JsonObject> output, String userId) {
        try {
            System.out.println(Thread.currentThread().getName());
            imageGenRepository.save(Image.builder()
                    .userId(userId)
                    .originalImageLink(ImageLinkExtractor.extractImageUrl(output.getData()))
                    .requestId(output.getRequestId())
                    .editedImageLink(null)
                    .build());
        } catch (Exception e) {
            // 예외 처리 로깅
            System.err.println("이미지 저장 중 오류 발생: " + e.getMessage());
        }
    }

    //TODO 선택한 경우 S3에 넣음
    public void imageSaveToS3(String requestId, String userId) {
        try {
            Optional<Image> optionalImage = imageGenRepository.findImageByRequestId(requestId);
            if(optionalImage.isEmpty())
                throw new Exception("Does not exist image");

            var image = optionalImage.get();
            String imageUrl = image.getOriginalImageLink();
            System.out.println(Thread.currentThread().getName());
            // 이미지 생성, 저장 (접속 불가)
            String bucketUrl = awsS3Service.upload(imageUrl, requestId);
            // 24시간 임시 링크 (접속 가능) 레디스에 등록
            String presignedUrl = awsS3Service.generatePresignedUrl(userId, requestId);

            imageGenRepository.save(
                    image.setOriginalImageLink(imageUrl)
            );

        } catch (Exception e) {
            // 예외 처리 로깅
            System.err.println("이미지 저장 중 오류 발생: " + e.getMessage());
        }
    }
}
