package com.precapstone.fiveguys_backend.message.send.messagetype;

import java.util.List;

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
