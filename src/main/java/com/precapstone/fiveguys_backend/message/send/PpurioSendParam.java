package com.precapstone.fiveguys_backend.message.send;

import com.precapstone.fiveguys_backend.message.send.option.Target;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 뿌리오 메세지 전송 API를 이용하기 위한 최소한의 데이터
 * ※ 나머지 데이터는 MessageType 클래스에서 모두 할당
 */
@Builder
@Getter
public class PpurioSendParam {
    String messageType;
    String content;
    String fromNumber;
    List<Target> targets;
    String subject;
    List<String> filePaths;
    //TODO: 예약 시간 설정
}
