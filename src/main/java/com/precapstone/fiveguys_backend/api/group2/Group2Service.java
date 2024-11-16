package com.precapstone.fiveguys_backend.api.group2;

import com.precapstone.fiveguys_backend.api.auth.JwtTokenProvider;
import com.precapstone.fiveguys_backend.api.folder2.Folder2Service;
import com.precapstone.fiveguys_backend.entity.Contact2;
import com.precapstone.fiveguys_backend.entity.Group2;
import com.precapstone.fiveguys_backend.exception.ControlledException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.precapstone.fiveguys_backend.exception.errorcode.Group2ErrorCode.*;
import static com.precapstone.fiveguys_backend.exception.errorcode.UserErrorCode.USER_AUTHORIZATION_FAILED;

@Service
@RequiredArgsConstructor
public class Group2Service {
    private final Group2Repository group2Repository;
    private final Folder2Service folder2Service;
    private final JwtTokenProvider jwtTokenProvider;

    public Group2 create(Group2CreateDTO group2CreateDTO, String accessToken) {
        var folder2 = folder2Service.readFolder2(group2CreateDTO.getFolder2Id(), accessToken);

        // [보안] 데이터의 주인이 호출한 API인지 accessToken을 통해 확인
        var userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        if(!folder2.getUser().getUserId().equals(userId))
            throw new ControlledException(USER_AUTHORIZATION_FAILED);

        // 1. [예외처리] 본인 Folder2 안에 Group2가 이미 존재하는 경우
        var groups = folder2.getGroup2s();
        for(var group : groups)
            if(group.getName().equals(group2CreateDTO.getName()))
                throw new ControlledException(GROUP2_NAME_ALREADY_EXISTS_IN_THIS_FOLDER2);

        var group2 = Group2.builder()
                .folder2(folder2)
                .name(group2CreateDTO.getName())
                .build();

        group2Repository.save(group2);
        return group2;
    }

    public Group2 readGroup2(Long group2Id, String accessToken) {
        var group2 = group2Repository.findById(group2Id)
                .orElseThrow(()-> new ControlledException(GROUP2_NOT_FOUND));

        // [보안] 데이터의 주인이 호출한 API인지 accessToken을 통해 확인
        var userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        if(!group2.getFolder2().getUser().getUserId().equals(userId))
            throw new ControlledException(USER_AUTHORIZATION_FAILED);

        return group2;
    }

    public Group2 update(Group2UpdateDTO group2UpdateDTO, String accessToken) {
        var group2 = readGroup2(group2UpdateDTO.getGroup2Id(), accessToken);

        // [보안] 데이터의 주인이 호출한 API인지 accessToken을 통해 확인
        // ※ 이미 readGroup2()에서 인증을 거치지만 형식상 추가
        var userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        if(!group2.getFolder2().getUser().getUserId().equals(userId))
            throw new ControlledException(USER_AUTHORIZATION_FAILED);

        if(group2UpdateDTO.getName() != null) {
            // 1. [예외처리] 본인 Folder2 안에 Group2가 이미 존재하는 경우
            var groups = group2.getFolder2().getGroup2s();
            for(var group : groups)
                if(group.getName().equals(group2UpdateDTO.getName()))
                    throw new ControlledException(GROUP2_NAME_ALREADY_EXISTS_IN_THIS_FOLDER2);
            group2.setName(group2UpdateDTO.getName());
        }

        if(group2UpdateDTO.getFolder2Id() != null) {
            try {
                var folder2Id = Long.getLong(group2UpdateDTO.getFolder2Id());
                var folder2 = folder2Service.readFolder2(folder2Id, accessToken);
                group2.setFolder2(folder2);
            } catch (IllegalArgumentException e) {
                throw new ControlledException(INVALID_FORMAT_BY_FOLDER2_ID);
            }
        }

        group2Repository.save(group2);
        return group2;
    }

    public Group2 updateContact2s(Long group2Id, List<Contact2> contact2s, String accessToken) {
        var group2 = readGroup2(group2Id, accessToken);

        // [보안] 데이터의 주인이 호출한 API인지 accessToken을 통해 확인
        // ※ 이미 readGroup2()에서 인증을 거치지만 형식상 추가
        var userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        if(!group2.getFolder2().getUser().getUserId().equals(userId))
            throw new ControlledException(USER_AUTHORIZATION_FAILED);

        group2.setContact2s(contact2s);

        group2Repository.save(group2);
        return group2;
    }

    public Group2 delete(Long group2Id, String accessToken) {
        var group2 = readGroup2(group2Id, accessToken);

        // [보안] 데이터의 주인이 호출한 API인지 accessToken을 통해 확인
        // ※ 이미 readGroup2()에서 인증을 거치지만 형식상 추가
        var userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        if(!group2.getFolder2().getUser().getUserId().equals(userId))
            throw new ControlledException(USER_AUTHORIZATION_FAILED);

        group2Repository.deleteById(group2Id);
        return group2;
    }
}
