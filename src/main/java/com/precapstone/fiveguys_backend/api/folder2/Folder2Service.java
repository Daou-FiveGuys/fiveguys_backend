package com.precapstone.fiveguys_backend.api.folder2;

import com.precapstone.fiveguys_backend.api.user.UserService;
import com.precapstone.fiveguys_backend.entity.Folder2;
import com.precapstone.fiveguys_backend.exception.ControlledException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.precapstone.fiveguys_backend.exception.errorcode.Folder2ErrorCode.*;

@Service
@RequiredArgsConstructor
public class Folder2Service {
    private final UserService userService;
    private final Folder2Repository folder2Repository;

    public Folder2 create(Folder2CreateDTO folder2CreateDTO) {
        var user = userService.findById(folder2CreateDTO.getUserId());

        // 1. [예외처리] Folder name이 해당 유저에게 이미 존재하는 경우
        if(folder2Repository.findByName(folder2CreateDTO.getName()).isPresent())
            throw new ControlledException(FOLDER2_NAME_ALREADY_EXISTS);

        var folder2 = Folder2.builder()
                .name(folder2CreateDTO.getName())
                .user(user)
                .build();

        return folder2;
    }

    public Folder2 readFolder2(Long folder2Id) {
        var folder2 = folder2Repository.findById(folder2Id)
                .orElseThrow(()->new ControlledException(FOLDER2_NOT_FOUND));

        return folder2;
    }

    public List<Folder2> readALLFolder2(Long userId) {
        var user = userService.findById(userId);
        var folder2s = folder2Repository.findByUser(user)
                .orElseThrow(()->new ControlledException(FOLDER2_NOT_FOUND_BY_USER));

        return folder2s;
    }

    public Folder2 update(Folder2UpdateDTO folder2UpdateDTO) {
        var folder2 = folder2Repository.findById(folder2UpdateDTO.getFolder2Id())
                .orElseThrow(()->new ControlledException(FOLDER2_NOT_FOUND));

        if(folder2UpdateDTO.getFolder2Id() != null)
            folder2.setName(folder2UpdateDTO.getName());

        folder2Repository.save(folder2);
        return folder2;
    }

    public Folder2 delete(Long folder2Id) {
        var folder2 = folder2Repository.findById(folder2Id)
                .orElseThrow(()->new ControlledException(FOLDER2_NOT_FOUND));

        folder2Repository.deleteById(folder2Id);
        return folder2;
    }
}
