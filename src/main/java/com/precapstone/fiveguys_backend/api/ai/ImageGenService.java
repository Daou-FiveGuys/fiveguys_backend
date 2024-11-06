package com.precapstone.fiveguys_backend.api.ai;

import ai.fal.client.FalClient;
import ai.fal.client.SubscribeOptions;
import ai.fal.client.queue.QueueStatus;
import com.google.gson.JsonObject;
import com.precapstone.fiveguys_backend.api.auth.JwtTokenProvider;
import com.precapstone.fiveguys_backend.api.dto.ImageResponseDTO;
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
}
