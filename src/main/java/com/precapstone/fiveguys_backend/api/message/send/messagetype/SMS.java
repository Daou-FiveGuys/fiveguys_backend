package com.precapstone.fiveguys_backend.api.message.send.messagetype;

import com.precapstone.fiveguys_backend.api.message.PpurioMessageDTO;
import com.precapstone.fiveguys_backend.exception.ControlledException;

import java.util.List;

import static com.precapstone.fiveguys_backend.api.message.send.PpurioErrorCode.CONTENT_IS_TOO_LONG;

/**
 * 짧 메세지를 전달하는 클래스이다.
 * 90byte 이하 메세지 용량에서 사용가능하다.
 * 장문의 경우 LMS, 이미지를 전송할 시 MMS로 타입을 전송할 것
 *
 */
public class SMS extends MessageType {
    public SMS(String ppurioAccount, String fromNumber, String message, List targets) {
        super(ppurioAccount, fromNumber, message, targets);
        // TODO: 문자 메세지 길이 파악하기

        params.put("messageType", "SMS");
    }

    public SMS(String ppurioAccount, PpurioMessageDTO ppurioMessageDTO) {
        super(ppurioAccount, ppurioMessageDTO.getFromNumber(), ppurioMessageDTO.getContent(), ppurioMessageDTO.getTargets());

        // TODO: 문자 메세지 길이 파악하기
        if(ppurioMessageDTO.getContent().getBytes().length > 90) throw new ControlledException(CONTENT_IS_TOO_LONG);

        params.put("messageType", "SMS");
    }

    /**
     * 각 형식에 대한 에러처리 (잘못된 형식의 요청인지 판단한다.)
     *
     * @return
     */
    public boolean errorCheck() {
        super.errorCheck();

        //TODO()
        return true;
    }
}
