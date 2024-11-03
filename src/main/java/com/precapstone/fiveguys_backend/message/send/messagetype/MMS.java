package com.precapstone.fiveguys_backend.message.send.messagetype;

import com.precapstone.fiveguys_backend.message.send.option.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MMS extends MessageType {
    public MMS(String ppurioAccount, String fromNumber, String message, List<Target> targets, List<String> filePaths) throws IOException {
        super(ppurioAccount, fromNumber, message, targets);

        params.put("messageType", "MMS");
        params.put("files", List.of(
            createFileTestParams(filePaths.get(0))
        ));
    }

    private Map<String, Object> createFileTestParams(String filePath) throws RuntimeException, IOException {
        FileInputStream fileInputStream = null;
        try {
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
