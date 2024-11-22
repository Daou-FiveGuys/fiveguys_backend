package com.precapstone.fiveguys_backend.api.message.send.messagetype;

import com.precapstone.fiveguys_backend.api.message.PpurioMessageDTO;
import com.precapstone.fiveguys_backend.api.message.send.option.Files;
import com.precapstone.fiveguys_backend.api.message.send.option.Target;
import com.precapstone.fiveguys_backend.exception.ControlledException;

import java.io.IOException;
import java.util.List;

import static com.precapstone.fiveguys_backend.api.message.send.PpurioErrorCode.*;

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

    public MMS(String ppurioAccount, PpurioMessageDTO ppurioMessageDTO) {
        super(ppurioAccount, ppurioMessageDTO.getFromNumber(), ppurioMessageDTO.getContent(), ppurioMessageDTO.getTargets());

        // TODO: 프론트 주소록 및 전송 기능 수정하기
        Files files;
        try {
            files = new Files(ppurioMessageDTO.getMultipartFile());
        } catch (IOException e) { throw new ControlledException(MULTIPART_FILE_NOT_FOUND); }

        // 문자 메세지 길이 파악하기(더 길 시 에러메세지 발송)
        if(ppurioMessageDTO.getContent().getBytes().length > 2000) throw new ControlledException(CONTENT_IS_TOO_LONG);
        // 파일 크기 예외 처리
        if(files.getByteSize() > 307200) throw new ControlledException(FILE_IS_TOO_BIG);

        params.put("messageType", "MMS");
        params.put("files", List.of(
                files.get()
        ));
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
