package com.precapstone.fiveguys_backend.message.send.messagetype;

import com.precapstone.fiveguys_backend.message.send.option.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            createFileTestParams(filePaths.get(0))
        ));
    }

    /**
     * 파일을 접근하여 필요한 이미지 정보 속성을 반환한다.
     *
     * @param filePath 이미지가 저장된 경로위치
     *
     * @return 파일 전송에 필요한 이미지 정보 속성을 반환한다.
     *
     * @throws RuntimeException
     * @throws IOException
     */
    private Map<String, Object> createFileTestParams(String filePath) throws RuntimeException, IOException {
        FileInputStream fileInputStream = null;
        try {
            // 파일 경로를 통한 파일 접근
            File file = new File(filePath);
            byte[] fileBytes = new byte[ (int) file.length()];
            fileInputStream = new FileInputStream(file);
            int readBytes = fileInputStream.read(fileBytes);

            if (readBytes != file.length()) {
                throw new IOException();
            }

            String encodedFileData = Base64.getEncoder().encodeToString(fileBytes);

            HashMap<String, Object> params = new HashMap<>();
            params.put("size", file.length());
            params.put("name", file.getName());
            params.put("data", encodedFileData);
            return params;
        } catch (IOException e) {
            throw new RuntimeException("파일을 가져오는데 실패했습니다.", e);
        } finally {
            if(fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }
}
