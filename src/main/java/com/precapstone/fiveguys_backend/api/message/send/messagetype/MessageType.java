package com.precapstone.fiveguys_backend.api.message.send.messagetype;

import com.precapstone.fiveguys_backend.api.message.send.option.Target;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 메세지를 전달하는 일반화된 타입
 */
@Getter
@RequiredArgsConstructor
public class MessageType {
    // 메세지 전송을 위한 추가적인 속성
    protected Map<String, Object> params = new HashMap<>();

    /**
     * 상속받은 객체들이 모두 이용해야하는 함수 정보
     *
     * @param ppurioAccount 서비스 사업자의 뿌리오 계정
     * @param fromNumber 전송 주체의 연락처
     * @param message 전송될 메세지 내용
     * @param targets 전송할 대상의 정보와 추가 속성
     */
    public MessageType(String ppurioAccount, String fromNumber, String message, List<Target> targets, LocalDateTime sendTime) {
        params.put("account", ppurioAccount);
        params.put("messageType", "SMS");
        params.put("from", fromNumber);
        params.put("content", message);
        params.put("duplicateFlag", "Y");
        params.put("rejectType", "AD");
        params.put("targetCount", targets.size());
        params.put("targets", targets);
        params.put("refKey", RandomStringUtils.random(32, true, true));
        if(sendTime != null) params.put("sendTime", sendTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
    }

    /**
     * 각 형식에 대한 에러처리 (잘못된 형식의 요청인지 판단한다.)
     *
     * @return
     */
    public boolean errorCheck() {
        //TODO()
        return true;
    }
}
