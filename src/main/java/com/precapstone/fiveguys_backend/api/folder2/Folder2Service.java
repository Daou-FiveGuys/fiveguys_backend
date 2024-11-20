package com.precapstone.fiveguys_backend.api.folder2;

import com.precapstone.fiveguys_backend.api.auth.JwtTokenProvider;
import com.precapstone.fiveguys_backend.api.user.UserService;
import com.precapstone.fiveguys_backend.entity.Folder2;
import com.precapstone.fiveguys_backend.exception.ControlledException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.precapstone.fiveguys_backend.exception.errorcode.Folder2ErrorCode.*;
import static com.precapstone.fiveguys_backend.exception.errorcode.UserErrorCode.USER_AUTHORIZATION_FAILED;
import static com.precapstone.fiveguys_backend.exception.errorcode.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class Folder2Service {
    private final UserService userService;
    private final Folder2Repository folder2Repository;
    private final JwtTokenProvider jwtTokenProvider;

    public Folder2 create(String newFolderName, String accessToken) {
        // [보안] 데이터의 주인이 호출한 API인지 accessToken을 통해 확인
        var userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        var user = userService.findByUserId(userId)
                .orElseThrow(() -> new ControlledException(USER_NOT_FOUND));
        if(!user.getUserId().equals(userId))
            throw new ControlledException(USER_AUTHORIZATION_FAILED);

        // 1. [예외처리] Folder name이 해당 유저에게 이미 존재하는 경우
        if(folder2Repository.findByUserAndName(user, newFolderName).isPresent())
            throw new ControlledException(FOLDER2_NAME_ALREADY_EXISTS);

        var folder2 = Folder2.builder()
                .name(newFolderName)
                .user(user)
                .build();

        folder2Repository.save(folder2);
        return folder2;
    }

    public Folder2 readFolder2(Long folder2Id, String accessToken) {
        var folder2 = folder2Repository.findById(folder2Id)
                .orElseThrow(()->new ControlledException(FOLDER2_NOT_FOUND));

        // [보안] 데이터의 주인이 호출한 API인지 accessToken을 통해 확인
        var userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        if(!folder2.getUser().getUserId().equals(userId))
            throw new ControlledException(USER_AUTHORIZATION_FAILED);

        return folder2;
    }

    public List<Folder2> readALLByUser(String accessToken) {
        // [보안] 데이터의 주인이 호출한 API인지 accessToken을 통해 확인
        var userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        var user = userService.findByUserId(userId)
                .orElseThrow(()-> new ControlledException(USER_NOT_FOUND));

        var folder2s = folder2Repository.findByUser(user)
                .orElseThrow(()->new ControlledException(FOLDER2_NOT_FOUND_BY_USER));

        return folder2s;
    }

    public Folder2 update(Folder2UpdateDTO folder2UpdateDTO, String accessToken) {
        var folder2 = readFolder2(folder2UpdateDTO.getFolder2Id(), accessToken);

        // [보안] 데이터의 주인이 호출한 API인지 accessToken을 통해 확인
        // ※ 이미 readGroup2()에서 인증을 거치지만 형식상 추가
        var userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        var user = userService.findByUserId(userId)
                .orElseThrow(() -> new ControlledException(USER_NOT_FOUND));
        if(!folder2.getUser().getUserId().equals(userId))
            throw new ControlledException(USER_AUTHORIZATION_FAILED);

        if(folder2UpdateDTO.getFolder2Id() != null) {
            // 1. [예외처리] Folder name이 해당 유저에게 이미 존재하는 경우
            if(folder2Repository.findByUserAndName(user, folder2UpdateDTO.getName()).isPresent())
                throw new ControlledException(FOLDER2_NAME_ALREADY_EXISTS);

            folder2.setName(folder2UpdateDTO.getName());
        }

        folder2Repository.save(folder2);
        return folder2;
    }

    public Folder2 delete(Long folder2Id, String accessToken) {
        var folder2 = readFolder2(folder2Id, accessToken);

        // [보안] 데이터의 주인이 호출한 API인지 accessToken을 통해 확인
        // ※ 이미 readGroup2()에서 인증을 거치지만 형식상 추가
        var userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        if(!folder2.getUser().getUserId().equals(userId))
            throw new ControlledException(USER_AUTHORIZATION_FAILED);

        folder2Repository.deleteById(folder2Id);
        return folder2;
    }
}
