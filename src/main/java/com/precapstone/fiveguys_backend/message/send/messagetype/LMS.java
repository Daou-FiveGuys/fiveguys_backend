package com.precapstone.fiveguys_backend.message.send.messagetype;

import com.precapstone.fiveguys_backend.message.send.option.Target;

import java.util.List;

/**
 * 긴 메세지를 전달하는 클래스이다.
 * 90byte ~ 2000byte 메세지 용량에서 사용가능하다.
 * 이미지를 전송할 시 MMS로 타입을 전송할 것
 * 
 */
public class LMS extends MessageType {

    /**
     * MessageType와 동일한 방식으로 진행된다.
     */
    public LMS(String ppurioAccount, String fromNumber, String message, List<Target> targets) {
        super(ppurioAccount, fromNumber, message, targets);

        // TODO: 문자 메세지 길이 파악하기(더 길 시 에러메세지 발송)

        params.put("messageType", "LMS");
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
