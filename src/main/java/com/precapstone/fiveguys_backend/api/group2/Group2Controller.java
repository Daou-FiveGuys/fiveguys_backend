package com.precapstone.fiveguys_backend.api.group2;

import com.precapstone.fiveguys_backend.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/group2/")
@RequiredArgsConstructor
public class Group2Controller {
    private final Group2Service group2Service;

    // 그룹2 생성
    @PostMapping
    public ResponseEntity create(@RequestBody Group2CreateDTO group2CreateDTO) {
        var group2 = group2Service.create(group2CreateDTO);

        var response = CommonResponse.builder().code(200).message("그룹2 생성 성공").data(group2).build();
        return ResponseEntity.ok(response);
    }

    // 그룹2 조회
    @GetMapping("/{group2Id}")
    public ResponseEntity read(@PathVariable Long group2Id) {
        var group2 = group2Service.readGroup2(group2Id);

        var response = CommonResponse.builder().code(200).message("그룹2 조회 성공").data(group2).build();
        return ResponseEntity.ok(response);
    }

    // 폴더에 대한 그룹2 전체 조회
    @GetMapping("/folder/{folder2Id}")
    public ResponseEntity readAllByFolder2(@PathVariable Long folder2Id) {
        var group2s = group2Service.readALLByFolder2(folder2Id);

        var response = CommonResponse.builder().code(200).message("그룹2 조회 성공").data(group2s).build();
        return ResponseEntity.ok(response);
    }

    // 유저에 대한 그룹2 전체 조회
    @GetMapping("/user/{user2Id}")
    public ResponseEntity readAllByUser(@PathVariable Long user2Id) {
        var group2s = group2Service.readALLByUser(user2Id);

        var response = CommonResponse.builder().code(200).message("그룹2 조회 성공").data(group2s).build();
        return ResponseEntity.ok(response);
    }

    // 그룹2 수정
    @PatchMapping
    public ResponseEntity update(@RequestBody Group2UpdateDTO group2UpdateDTO) {
        var group2 = group2Service.update(group2UpdateDTO);

        var response = CommonResponse.builder().code(200).message("그룹2 수정 성공").data(group2).build();
        return ResponseEntity.ok(response);
    }

    // 그룹2 삭제
    @DeleteMapping("/{group2Id}")
    public ResponseEntity delete(@PathVariable Long group2Id) {
        var group2 = group2Service.delete(group2Id);

        var response = CommonResponse.builder().code(200).message("그룹2 삭제 성공").data(group2).build();
        return ResponseEntity.ok(response);
    }
}
