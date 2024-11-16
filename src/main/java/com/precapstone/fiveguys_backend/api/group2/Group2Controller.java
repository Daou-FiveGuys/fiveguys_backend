package com.precapstone.fiveguys_backend.api.group2;

import com.precapstone.fiveguys_backend.api.contact2.Contact2Service;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import com.precapstone.fiveguys_backend.common.auth.JwtFilter;
import com.precapstone.fiveguys_backend.entity.Contact2;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/group2/")
@RequiredArgsConstructor
public class Group2Controller {
    private final Group2Service group2Service;
    private final Contact2Service contact2Service;

    // 그룹2 생성
    @PostMapping
    public ResponseEntity create(@RequestBody Group2CreateDTO group2CreateDTO, @RequestHeader("Authorization") String authorization) {
        var accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");

        var group2 = group2Service.create(group2CreateDTO, accessToken);

        var response = CommonResponse.builder().code(200).message("그룹2 생성 성공").data(group2).build();
        return ResponseEntity.ok(response);
    }

    // 그룹2 조회
    @GetMapping("/{group2Id}")
    public ResponseEntity read(@PathVariable Long group2Id, @RequestHeader("Authorization") String authorization) {
        var accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");

        var group2 = group2Service.readGroup2(group2Id, accessToken);

        var response = CommonResponse.builder().code(200).message("그룹2 조회 성공").data(group2).build();
        return ResponseEntity.ok(response);
    }

    // 그룹2 수정
    @PatchMapping
    public ResponseEntity update(@RequestBody Group2UpdateDTO group2UpdateDTO, @RequestHeader("Authorization") String authorization) {
        var accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");

        var group2 = group2Service.update(group2UpdateDTO, accessToken);

        var response = CommonResponse.builder().code(200).message("그룹2 수정 성공").data(group2).build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{group2Id}")
    public ResponseEntity updateContact2s(@PathVariable Long group2Id, @RequestBody List<Contact2> contact2s, @RequestHeader("Authorization") String authorization) {
        var accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");

        contact2Service.deleteAllByGroup2Id(group2Id, accessToken);
        var group2 = group2Service.updateContact2s(group2Id, contact2s, accessToken);

        var response = CommonResponse.builder().code(200).message("그룹2 Contact2s 수정 성공").data(group2).build();
        return ResponseEntity.ok(response);
    }

    // 그룹2 삭제
    @DeleteMapping("/{group2Id}")
    public ResponseEntity delete(@PathVariable Long group2Id, @RequestHeader("Authorization") String authorization) {
        var accessToken = authorization.replace(JwtFilter.TOKEN_PREFIX, "");

        var group2 = group2Service.delete(group2Id, accessToken);

        var response = CommonResponse.builder().code(200).message("그룹2 삭제 성공").data(group2).build();
        return ResponseEntity.ok(response);
    }
}
