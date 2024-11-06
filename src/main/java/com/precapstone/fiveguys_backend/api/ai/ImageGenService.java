package com.precapstone.fiveguys_backend.api.ai;

import ai.fal.client.FalClient;
import ai.fal.client.SubscribeOptions;
import ai.fal.client.queue.QueueStatus;
import com.google.gson.JsonObject;
import com.precapstone.fiveguys_backend.api.auth.JwtTokenProvider;
import com.precapstone.fiveguys_backend.api.dto.ImageInpaintDTO;
import com.precapstone.fiveguys_backend.api.dto.ImageResponseDTO;
import com.precapstone.fiveguys_backend.api.dto.ImageUpscaleDTO;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.entity.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageGenService {
    private final ImageGenRepository imageGenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private static final FalClient fal = FalClient.withEnvCredentials();

    public CommonResponse generate(String accessToken, String prompt){
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

        Image image = imageGenRepository.save(Image.builder()
                .memberId(memberId)
                .editedImage(null)
                .originalImage(result)
                .build());

        return CommonResponse.builder()
                .code(200)
                .data(ImageResponseDTO.builder()
                        .image(result)
                        .imageId(image.getImageId())
                        .build())
                .build();
    }

    public CommonResponse inpaint(String accessToken, ImageInpaintDTO imageInpaintDTO){
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

    public CommonResponse upscale(String accessToken, ImageUpscaleDTO imageUpscaleDTO){
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
