package com.precapstone.fiveguys_backend.api.sendimage;

import com.precapstone.fiveguys_backend.api.aws.AwsS3Service;
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
    private final AwsS3Service awsS3Service;

    public SendImage create(MessageHistory messageHistory, MultipartFile multipartFile) {
        var url = awsS3Service.upload(multipartFile, multipartFile.getOriginalFilename());

        var sendImage = SendImage.builder()
                .messageHistory(messageHistory)
                .url(url)
                .build();

        sendImageRepository.save(sendImage);
        return sendImage;
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
