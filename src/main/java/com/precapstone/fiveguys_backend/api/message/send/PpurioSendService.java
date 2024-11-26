package com.precapstone.fiveguys_backend.api.message.send;

import com.precapstone.fiveguys_backend.api.amountused.AmountUsedService;
import com.precapstone.fiveguys_backend.api.amountused.AmountUsedType;
import com.precapstone.fiveguys_backend.api.auth.JwtTokenProvider;
import com.precapstone.fiveguys_backend.api.aws.AwsS3Service;
import com.precapstone.fiveguys_backend.api.dto.PpurioSendDTO;
import com.precapstone.fiveguys_backend.api.message.PpurioMessageDTO;
import com.precapstone.fiveguys_backend.api.message.auth.PpurioAuth;
import com.precapstone.fiveguys_backend.api.message.send.messagetype.MessageType;
import com.precapstone.fiveguys_backend.api.message.send.messagetype.SMS;
import com.precapstone.fiveguys_backend.api.message.send.messagetype.MMS;
import com.precapstone.fiveguys_backend.api.message.send.messagetype.LMS;
import com.precapstone.fiveguys_backend.api.message.send.option.Target;
import com.precapstone.fiveguys_backend.api.messagehistory.MessageHistoryDTO;
import com.precapstone.fiveguys_backend.api.messagehistory.MessageHistoryService;
import com.precapstone.fiveguys_backend.api.reservation.ReservationService;
import com.precapstone.fiveguys_backend.api.reservation.ReservationState;
import com.precapstone.fiveguys_backend.common.utils.RandomStringGenerator;
import com.precapstone.fiveguys_backend.entity.Contact2;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PpurioSendService {
    private final RestTemplate restTemplate;
    private final AmountUsedService amountUsedService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MessageHistoryService messageHistoryService;
    private final AwsS3Service awsS3Service;
    private final ReservationService reservationService;

    // 뿌리오 계정
    @Value("${spring.ppurio.account}")
    private String ppurioAccount;

    // 뿌리오 API 이용을 위한 고정 주소
    @Value("${spring.ppurio.base-url}")
    private String url;

    private final PpurioAuth ppurioAuth;

    public PpurioSendResponse message(PpurioMessageDTO ppurioMessageDTO, MultipartFile multipartFile, String fiveguysAccessToken) {
        // 토큰 발급
        String accessToken = ppurioAuth.getAccessToken();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+ accessToken);

        // 바디 설정
        var requestBody = createMessageParams(ppurioMessageDTO, multipartFile);

        // 전송 데이터 생성
        HttpEntity<Map> request = new HttpEntity<>(requestBody, headers);

        // 사용량 증가
        var userId = jwtTokenProvider.getUserIdFromToken(fiveguysAccessToken);
        if(ppurioMessageDTO.getMessageType().equals("MMS")) {
            amountUsedService.plus(userId, AmountUsedType.IMG_GCNT, 1);
            amountUsedService.plus(userId, AmountUsedType.IMG_SCNT, ppurioMessageDTO.getTargets().size());
        }
        else {
            amountUsedService.plus(userId, AmountUsedType.MSG_GCNT, 1);
            amountUsedService.plus(userId, AmountUsedType.MSG_SCNT, ppurioMessageDTO.getTargets().size());
        }
        
        // 전송
        var ppurioSendResponse = restTemplate.postForObject(url+"/v1/message", request, PpurioSendResponse.class);

        // 메세지 저장
        var messageHistoryDTO = getMessageHistoryDTO(userId, multipartFile, ppurioMessageDTO);
        messageHistoryService.create(messageHistoryDTO, ppurioSendResponse.getMessageKey());
        return ppurioSendResponse;
    }

    public PpurioSendResponse messageLink(PpurioMessageDTO ppurioMessageDTO, MultipartFile multipartFile, String fiveguysAccessToken) {
        // 토큰 발급
        String accessToken = ppurioAuth.getAccessToken();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+ accessToken);
        String fileName = RandomStringGenerator.generateRandomString(20);
        try {
            String bucketURL = awsS3Service.upload(multipartFile,fileName  + ".jpg");
            ppurioMessageDTO.setMessageType("LMS");
            ppurioMessageDTO.setContent(ppurioMessageDTO.getContent() + "\n\n이미지: " + bucketURL);
            var requestBody = createMessageParams(ppurioMessageDTO, null);
            HttpEntity<Map> request = new HttpEntity<>(requestBody, headers);
            var userId = jwtTokenProvider.getUserIdFromToken(fiveguysAccessToken);

            amountUsedService.plus(userId, AmountUsedType.MSG_GCNT, 1);
            amountUsedService.plus(userId, AmountUsedType.MSG_SCNT, ppurioMessageDTO.getTargets().size());

            var ppurioSendResponse = restTemplate.postForObject(url+"/v1/message", request, PpurioSendResponse.class);

            var messageHistoryDTO = getMessageHistoryDTO(userId, null, ppurioMessageDTO);
            messageHistoryService.createLink(messageHistoryDTO, bucketURL, ppurioSendResponse.getMessageKey());
            return ppurioSendResponse;
        } catch (Exception e){
            return null;
        }
    }

    /**
     * 안쓰는 코드
     * @param ppurioSendDTO
     * @throws IOException
     */
    public void sendMessage(PpurioSendDTO ppurioSendDTO) throws IOException {
        // 토큰 발급
        String accessToken = ppurioAuth.getAccessToken();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+ accessToken);

        // 바디 설정
        var requestBody = createSendParams(ppurioSendDTO);

        // 전송 데이터 생성
        HttpEntity<Map> request = new HttpEntity<>(requestBody, headers);

        // 전송
        restTemplate.postForObject(url+"/v1/message", request, PpurioSendResponse.class);
    }

    /**
     * 안쓰는 코드
     * PpurioSendParam을 통해 전달받은 MessageType을 통해 메세지 정보를 분기 처리
     * 
     * @param ppurioSendDTO 사용자가 요청한 속성 정보
     *
     * @return 전송할 메세지 생성 (Map)
     * 
     * @throws IOException
     */
    private Map createSendParams(PpurioSendDTO ppurioSendDTO) throws IOException {
        // 각 메세지 타입에 맞게 requestBody 분리
        MessageType messageType = switch (ppurioSendDTO.getMessageType()) {
            case "MMS" -> new MMS(ppurioAccount, ppurioSendDTO.getFromNumber(), ppurioSendDTO.getContent(), ppurioSendDTO.getTargets(), ppurioSendDTO.getFilePaths());
            case "LMS" -> new LMS(ppurioAccount, ppurioSendDTO.getFromNumber(), ppurioSendDTO.getContent(), ppurioSendDTO.getTargets());
            case "SMS" -> new SMS(ppurioAccount, ppurioSendDTO.getFromNumber(), ppurioSendDTO.getContent(), ppurioSendDTO.getTargets());
            default -> null;
        };

        return messageType.getParams();
    }

    private Map createMessageParams(PpurioMessageDTO ppurioMessageDTO, MultipartFile multipartFile) {
        // 각 메세지 타입에 맞게 requestBody 분리
        MessageType messageType = switch (ppurioMessageDTO.getMessageType()) {
            case "MMS" -> new MMS(ppurioAccount, ppurioMessageDTO, multipartFile);
            case "LMS" -> new LMS(ppurioAccount, ppurioMessageDTO);
            case "SMS" -> new SMS(ppurioAccount, ppurioMessageDTO);
            default -> null;
        };

        return messageType.getParams();
    }

    private List<Contact2> getContact2s(List<Target> targets) {
        // Target 리스트를 Contact2 리스트로 변환
        var contact2 = targets.stream()
                .map(Contact2::new) // Contact2(Target target) 생성자를 사용
                .toList();

        return contact2;
    }

    /**
     * PpurioMessageDTO를 MessageHistoryDIO로 변경하는 함수
     *
     */
    private MessageHistoryDTO getMessageHistoryDTO(String userId, MultipartFile multipartFile, PpurioMessageDTO ppurioMessageDTO) {
        return MessageHistoryDTO.builder()
                .userId(userId)
                .content(ppurioMessageDTO.getContent())
                .fromNumber(ppurioMessageDTO.getFromNumber())
                // MessageType 중복 사용
                .messageType(com.precapstone.fiveguys_backend.entity.messagehistory.MessageType.valueOf(ppurioMessageDTO.getMessageType()))
                .subject(ppurioMessageDTO.getSubject())
                .contact2s(getContact2s(ppurioMessageDTO.getTargets())) // TODO: target으로 반환하는 문제 발생
                .sendImage(multipartFile)
                .build();
    }

    public PpurioCancelResponse cancel(Long messageHistoryId, String fiveguysAccessToken) {
        var messageHistory = messageHistoryService.read(messageHistoryId, fiveguysAccessToken);

        // 토큰 발급
        String accessToken = ppurioAuth.getAccessToken();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+ accessToken);

        // 바디 설정
        var requestBody = cancelMessageParam(ppurioAccount, messageHistory.getMessageKey());

        // 전송 데이터 생성
        HttpEntity<Map> request = new HttpEntity<>(requestBody, headers);

        // 전송
        var ppurioCancelResponse = restTemplate.postForObject(url+"/v1/cancel", request, PpurioCancelResponse.class);

        reservationService.changeType(messageHistory, ReservationState.CANCEL);

        return ppurioCancelResponse;
    }

    private Map<String, String> cancelMessageParam(String ppurioAccount, String messageKey) {
        Map<String, String> params = new HashMap<>();
        params.put("account", ppurioAccount);
        params.put("messageKey", messageKey);
        return params;
    }
}
