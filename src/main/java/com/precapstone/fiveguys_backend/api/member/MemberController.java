package com.precapstone.fiveguys_backend.api.member;

import com.precapstone.fiveguys_backend.api.dto.MemberDTO;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member", description = "회원관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<CommonResponse> signup(@RequestBody MemberDTO memberDTO) {
        return ResponseEntity.ok(memberService.register(memberDTO));
    }

    @GetMapping("/exists")
    public ResponseEntity<CommonResponse> emailExists(@RequestParam String email) {
        return ResponseEntity.ok(memberService.emailExists(email));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<CommonResponse> delete(@RequestHeader String userId, @RequestHeader String password) {
        return ResponseEntity.ok(memberService.deleteByUserId(userId, password));
    }
}
