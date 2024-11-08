package com.precapstone.fiveguys_backend.group;

import com.precapstone.fiveguys_backend.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/contact/")
@RequiredArgsConstructor
public class GroupController {
//    // 그룹 조회
//    @GetMapping
//    public CommonResponse info() throws IOException {
//        // 그룹 조회
//        return CommonResponse.builder().code(200).message("그룹 조회 성공").build();
//    }

    // 그룹 생성
    @PostMapping
    public CommonResponse create(GroupCreateParm groupCreateParm) throws IOException {
        // 그룹 생성
        return CommonResponse.builder().code(200).message("그룹 생성 성공").build();
    }

    // 그룹 삭제
    @DeleteMapping
    public CommonResponse delete() throws IOException {
        // 그룹 삭제
        return CommonResponse.builder().code(200).message("그룹 삭제 성공").build();
    }

//    // 그룹 변경
//    @PatchMapping
//    public CommonResponse patch() throws IOException {
//        // 그룹 변경
//        // 1. 위치 이동의 경우
//        // 2. 정보 변경의 경우
//        return CommonResponse.builder().code(200).message("그룹 변경 성공").build();
//    }
}
