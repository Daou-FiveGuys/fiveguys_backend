package com.precapstone.fiveguys_backend.api.ai;

import ai.fal.client.FalClient;
import ai.fal.client.SubscribeOptions;
import ai.fal.client.queue.QueueStatus;
import com.google.gson.JsonObject;
import com.precapstone.fiveguys_backend.api.auth.JwtTokenProvider;
import com.precapstone.fiveguys_backend.api.aws.AwsS3Service;
import com.precapstone.fiveguys_backend.api.dto.ImageInpaintDTO;
import com.precapstone.fiveguys_backend.api.dto.ImageResponseDTO;
import com.precapstone.fiveguys_backend.api.dto.ImageUpscaleDTO;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.entity.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    private final AwsS3Service awsS3Service;
    private final ImageGenRepository imageGenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private static final FalClient fal = FalClient.withEnvCredentials();

    /**
     * 이미지 생성
     * @param authorization 인증 헤더
     * @param prompt 이미지 생성 프롬프트 (KOR)
     * @return
     */
    public CommonResponse generate(String authorization, String prompt){
        //TODO if prompt == korean -> translate
        String accessToken = JwtTokenProvider.stripTokenPrefix(authorization);
        String memberId = jwtTokenProvider.getUserIdFromToken(accessToken);
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
            // 이미지 생성, 저장 (접속 불가)
            String bucketUrl = awsS3Service.upload(result.getData(), result.getRequestId());
            // 24시간 임시 링크 (접속 가능)
            String imageUrl = awsS3Service.generatePresignedUrl(memberId, result.getRequestId());
            //TODO 클래스에 영구 링크 보존하고 다른 방법으로 임시 링크 전달
            Image image = imageGenRepository.save(Image.builder()
                    .memberId(memberId)
                    .originalImageLink(imageUrl)
                    .requestId(result.getRequestId())
                    .editedImageLink(null)
                    .build());

            return CommonResponse.builder()
                    .code(200)
                    .data(image)
                    .build();
        } catch (Exception e) {
            return CommonResponse.builder()
                    .code(400)
                    .build();
        }
    }

    public CommonResponse inpaint(String authorization, ImageInpaintDTO imageInpaintDTO){
        String accessToken = JwtTokenProvider.stripTokenPrefix(authorization);
        String memberId = jwtTokenProvider.getUserIdFromToken(accessToken);
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
        String memberId = jwtTokenProvider.getUserIdFromToken(accessToken);
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
}
