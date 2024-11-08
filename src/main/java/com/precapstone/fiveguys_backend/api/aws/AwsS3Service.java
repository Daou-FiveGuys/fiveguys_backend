package com.precapstone.fiveguys_backend.api.aws;

import com.precapstone.fiveguys_backend.api.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;

/**
 * 이미지 저장 서비스
 * 🚨 In S3, the 'key' property means 'filename'. 🚨
 * fal.ai requestId -> key
 *
 *
 * @author 6-keem
 * @since 2024-11-07
 *
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AwsS3Service {
    /**
        TODO 이미지 업로드, 조회, 수정 시 redis 저장 after 30 min -> expire
     *  이미지 업로드 시에는 용량 및 일일 업로드 횟수 기록(redis)
     */
    private final S3Presigner s3Presigner;
    private final RedisService redisService;
    private final S3Client s3Client;
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    /**
     * 파일 업로드 (URL 또는 MultipartFile)
     * @param input 이미지 URL 또는 MultipartFile
     * @param key S3에 저장될 파일 이름
     * @return S3 저장소 URL
     */
    public <T> String upload(T input, String key) {
        try {
            return uploadToS3(input, key);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 파일 삭제
     * @param key 파일 이름
     * @return 삭제 여부
     */
    public String delete(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        try {
            DeleteObjectResponse response = s3Client.deleteObject(deleteObjectRequest);
            return "Successfully deleted : " + key;
        } catch (Exception e) {
            return "Failed to delete : " + key;
        }
    }

    /**
     * 조회 (임시 링크)
     * @param userId 유저 아이디
     * @param key 파일 이름
     * @return 임시 URL
     */
    public String getUrl(String userId, String key) {
        return generatePresignedUrl(userId, key);
    }

    /**
     * 임시 링크  (일정 시간이 지나면 접근 불가)
     * @param userId 유저 아이디
     * @param key 파일 이름
     * @return 임시 링크
     */
    private String generatePresignedUrl(String userId, String key){
        /**
         * TODO 이미지가 userId에 귀속된 이미지인지 확인 필요
         * 주소 생성 횟수 줄이기 위해 redis 임시 사용
         */

        String redisKey = userId + ":" + bucketName + ":" + key;
        if(redisService.exists(redisKey))
            return redisService.get(redisKey);

        Duration expiration = Duration.ofHours(24);
        GetObjectRequest getObjectRequest  = getGetObjectRequest(key);
        try{
            PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(p -> p
                    .getObjectRequest(getObjectRequest)
                    .signatureDuration(expiration));
            URL presignedUrl = presignedGetObjectRequest.url();

//            if(redisService.exists(redisKey))
//                redisService.delete(redisKey);
            redisService.setDataExpire(redisKey, presignedUrl.toString(), expiration.toMillis());
            return presignedUrl.toString();
        } catch (Exception e){
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> String uploadToS3(T input, String key) throws IOException {
        PutObjectRequest objectRequest = getPutObjectRequest(key);
        RequestBody requestBody;

        if (input instanceof MultipartFile) {
            MultipartFile multipartFile = (MultipartFile) input;
            requestBody = RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize());
        } else if (input instanceof String) {
            String imageUrl = (String) input;
            requestBody = saveFileFromUrlToS3(imageUrl);
        } else {
            throw new IllegalArgumentException("Unsupported input type: " + input.getClass().getName());
        }

        s3Client.putObject(objectRequest, requestBody);
        return findUrlByKey(key);
    }

    public RequestBody saveFileFromUrlToS3(String imageUrl) throws IOException {
        // URL에서 InputStream 생성
        URL url = new URL(imageUrl);
        try (InputStream inputStream = url.openStream();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            // InputStream을 ByteArray로 변환
            byte[] data = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, bytesRead);
            }
            buffer.flush();
            byte[] fileBytes = buffer.toByteArray();
            // RequestBody 생성
            return RequestBody.fromBytes(fileBytes);
        }
    }

    /**
     * s3에 저장된 이미지의 링크 (영구)
     * @param key 파일 이름
     * @return 이미지 링크
     */
    private String findUrlByKey (String key) {
        S3Utilities s3Utilities = s3Client.utilities();
        GetUrlRequest request = GetUrlRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        URL url = s3Utilities.getUrl(request);
        return url.toString();
    }


    /**
     * 조회 요청 객체 생성
     * @param key 파일 이름
     * @return 조회 요청 객체
     */
    private GetObjectRequest getGetObjectRequest(String key) {
        return GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .responseContentType("image/jpeg")
                .build();
    }

    /**
     * 추가 요청 객체 생성
     * @param key 파일 이름
     * @return 추가 요청 객체
     */
    private PutObjectRequest getPutObjectRequest(String key) {
        return PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("image/jpeg")
                .build();
    }
}