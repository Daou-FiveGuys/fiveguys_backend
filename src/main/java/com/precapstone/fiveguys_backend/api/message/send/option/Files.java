package com.precapstone.fiveguys_backend.api.message.send.option;

import com.fasterxml.jackson.annotation.JsonValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 파일 경로에 존재하는 이미지의 속성을 저장하는 클래스
 * 실제로 MMS를 통해 전송할 수 있는 데이터는 1개이다.
 */
public class Files {
    private final String name;
    private final long size;
    private final String data;

    /**
     * 해당 클래스를 Map 형식으로 반환
     * 
     * @return name, size, data를 Map 형식으로 반환
     */
    @JsonValue
    public Map<String, Object> get() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("size", size);
        map.put("data", data);

        return map;
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
    public Files(String filePath) throws RuntimeException, IOException {
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

            size = file.length();
            name = file.getName();
            data = encodedFileData;
        } catch (IOException e) {
            throw new RuntimeException("파일을 가져오는데 실패했습니다.", e);
        } finally {
            if(fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }
}
