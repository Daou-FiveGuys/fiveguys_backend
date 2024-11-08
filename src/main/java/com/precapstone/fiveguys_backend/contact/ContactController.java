package com.precapstone.fiveguys_backend.contact;

import com.precapstone.fiveguys_backend.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/contact/")
@RequiredArgsConstructor
public class ContactController {
    // 주소록 조회 (특정 연락처 전체 연락처 존재)
    @GetMapping
    public CommonResponse info() throws IOException {
        // ※ 전화번호 조회, 이름 조회 모두 가능해야 함
        // 그룹 조회
        // 주소록 조회
        return CommonResponse.builder().code(200).message("주소록 조회 성공").build();
    }
    
    /**
     * 특정 그룹에 주소록 생성
     * ※ 요청 사항에 그룹이 먼저 생성되어있어야 한다.
     */
    @PostMapping
    public CommonResponse create(@RequestBody ContactCreateParam contactCreateParam) throws IOException {
        // 그룹 내부에 주소록 생성
        return CommonResponse.builder().code(200).message("주소록 생성 성공").build();
    }

    // 주소록 삭제
    @DeleteMapping
    public CommonResponse delete() throws IOException {
        // 그룹 내부에 주소록 삭제
        return CommonResponse.builder().code(200).message("주소록 삭제 성공").build();
    }

    // 주소록 변경
    @PatchMapping
    public CommonResponse patch() throws IOException {
        // 그룹 내부에 주소록 변경
        // 1. 위치 이동의 경우
        // 2. 정보 변경의 경우
        return CommonResponse.builder().code(200).message("주소록 변경 성공").build();
    }
}
