package com.precapstone.fiveguys_backend.message.send.messagetype;

import com.precapstone.fiveguys_backend.message.send.option.Files;
import com.precapstone.fiveguys_backend.message.send.option.Target;

import java.io.IOException;
import java.util.List;

/**
 * 긴 메세지를 전달하는 클래스이다.
 * 90byte ~ 2000byte 메세지 용량에서 사용가능하다.
 * 필수로 이미지(files 속성)를 제공 해야한다.
 * 이미지는 리스트로 요청하지만 실제론 1개만 전송해야한다.
 *
 */
public class MMS extends MessageType {

    /**
     * @param filePaths 이미지가 저장된 경로위치
     *
     * @throws IOException
     */
    public MMS(String ppurioAccount, String fromNumber, String message, List<Target> targets, List<String> filePaths) throws IOException {
        super(ppurioAccount, fromNumber, message, targets);

        params.put("messageType", "MMS");
        params.put("files", List.of(
                new Files(filePaths.get(0)).get()
        ));
    }

}
