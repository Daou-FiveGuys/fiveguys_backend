package com.precapstone.fiveguys_backend.api.sendimage;

import com.precapstone.fiveguys_backend.api.messagehistory.messagehistory.MessageHistory;
import com.precapstone.fiveguys_backend.exception.ControlledException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.precapstone.fiveguys_backend.exception.errorcode.SendImageErrorCode.SEND_IMAGE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SendImageService {
    private final SendImageRepository sendImageRepository;

    public SendImage create(MessageHistory messageHistory, MultipartFile multipartFile) {
        // TODO: 여기서 이미지 링크 만들기

        var sendImage = SendImage.builder()
                .messageHistory(messageHistory)
                .url()
                .build();
        return null;
    }

    public SendImage read(Long sendImageId) {
        // 링크 등의 정보 반환
        var sendImage = sendImageRepository.findBySendImageId(sendImageId)
                .orElseThrow(() -> new ControlledException(SEND_IMAGE_NOT_FOUND));
        return sendImage;
    }

    public SendImage read(MessageHistory messageHistory) {
        return messageHistory.getSendImage();
    }

    public SendImage delete(Long sendImageId) {
        var sendImage = read(sendImageId);

        sendImageRepository.deleteBySendImageId(sendImageId);
        return sendImage;
    }
}
