package com.precapstone.fiveguys_backend.api.group2;

import com.precapstone.fiveguys_backend.api.folder2.Folder2Service;
import com.precapstone.fiveguys_backend.entity.Group2;
import com.precapstone.fiveguys_backend.exception.ControlledException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.precapstone.fiveguys_backend.exception.errorcode.Group2ErrorCode.GROUP2_NOT_FOUND;
import static com.precapstone.fiveguys_backend.exception.errorcode.Group2ErrorCode.INVALID_FORMAT_BY_FOLDER2_ID;

@Service
@RequiredArgsConstructor
public class Group2Service {
    private final Group2Repository group2Repository;
    private final Folder2Service folder2Service;

    public Group2 create(Group2CreateDTO group2CreateDTO) {
        var folder2 = folder2Service.readFolder2(group2CreateDTO.getFolder2Id());

        var group2 = Group2.builder()
                .folder2(folder2)
                .name(group2CreateDTO.getName())
                .build();

        group2Repository.save(group2);
        return group2;
    }

    public Group2 readGroup2(Long group2Id) {
        var group2 = group2Repository.findById(group2Id)
                .orElseThrow(()-> new ControlledException(GROUP2_NOT_FOUND));

        return group2;
    }

    public Group2 update(Group2UpdateDTO group2UpdateDTO) {
        var group2 = readGroup2(group2UpdateDTO.getGroup2Id());

        if(group2UpdateDTO.getName() != null)
            group2.setName(group2UpdateDTO.getName());

        if(group2UpdateDTO.getFolder2Id() != null) {
            try {
                var folder2Id = Long.getLong(group2UpdateDTO.getFolder2Id());
                var folder2 = folder2Service.readFolder2(folder2Id);
                group2.setFolder2(folder2);
            } catch (IllegalArgumentException e) {
                throw new ControlledException(INVALID_FORMAT_BY_FOLDER2_ID);
            }
        }

        group2Repository.save(group2);
        return group2;
    }

    public Group2 delete(Long group2Id) {
        var group2 = readGroup2(group2Id);

        group2Repository.deleteById(group2Id);
        return group2;
    }
}
