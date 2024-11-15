package com.precapstone.fiveguys_backend.api.group2;

import com.precapstone.fiveguys_backend.api.folder2.Folder2Service;
import com.precapstone.fiveguys_backend.api.user.UserService;
import com.precapstone.fiveguys_backend.entity.Group2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Group2Service {
    private final Group2Repository group2Repository;
    private final UserService userService;
    private final Folder2Service folder2Service;

    public Group2 create(Group2CreateDTO group2CreateDTO) {
    }

    public Group2 readGroup2(Long group2Id) {
    }

    public List<Group2> readALLByFolder2(Long folder2Id) {
    }

    public List<Group2> readALLByUser(Long user2Id) {
        var user = userService.findById(user2Id);
        var folder2s = folder2Service.readALLByUser(user.getId());
        var groups = List();
    }

    public Group2 update(Group2UpdateDTO group2UpdateDTO) {
    }

    public Group2 delete(Long group2Id) {
    }
}
