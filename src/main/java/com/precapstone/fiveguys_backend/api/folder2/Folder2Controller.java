package com.precapstone.fiveguys_backend.api.folder2;

import com.precapstone.fiveguys_backend.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/folder2/")
@RequiredArgsConstructor
public class Folder2Controller {
    private final Folder2Service folder2Service;

    // 폴더 생성
    @PostMapping
    public ResponseEntity create(@RequestBody Folder2CreateDTO folder2CreateDTO) {
        var folder2 = folder2Service.create(folder2CreateDTO);

        var response = CommonResponse.builder().code(200).message("폴더 생성 성공").data(folder2).build();
        return ResponseEntity.ok(response);
    }

    // 폴더 조회
    @GetMapping("/{folder2Id}")
    public ResponseEntity read(@PathVariable Long folder2Id) {
        var folder2 = folder2Service.readFolder2(folder2Id);

        var response = CommonResponse.builder().code(200).message("폴더 조회 성공").data(folder2).build();
        return ResponseEntity.ok(response);
    }

    // 유저에 대한 폴더 전체 조회
    @GetMapping("/all/{userId}")
    public ResponseEntity readAll(@PathVariable Long userId) {
        var folder2s = folder2Service.readALLFolder2(userId);

        var response = CommonResponse.builder().code(200).message("폴더 조회 성공").data(folder2s).build();
        return ResponseEntity.ok(response);
    }

    // 폴더 수정
    @PatchMapping
    public ResponseEntity update(@RequestBody Folder2UpdateDTO folder2UpdateDTO) {
        var folder2 = folder2Service.update(folder2UpdateDTO);

        var response = CommonResponse.builder().code(200).message("폴더 수정 성공").data(folder2).build();
        return ResponseEntity.ok(response);
    }

    // 폴더 삭제
    @DeleteMapping("/{folder2Id}")
    public ResponseEntity delete(@PathVariable Long folder2Id) {
        var folder2 = folder2Service.delete(folder2Id);

        var response = CommonResponse.builder().code(200).message("폴더 삭제 성공").data(folder2).build();
        return ResponseEntity.ok(response);
    }
}
