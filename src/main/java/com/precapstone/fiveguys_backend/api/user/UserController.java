package com.precapstone.fiveguys_backend.api.user;

import com.precapstone.fiveguys_backend.api.dto.UserDTO;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "회원관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<CommonResponse> signup(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.register(userDTO));
    }

    @GetMapping("/exists")
    public ResponseEntity<CommonResponse> emailExists(@RequestParam String email) {
        return ResponseEntity.ok(userService.emailExists(email));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<CommonResponse> delete(@RequestHeader String userId, @RequestHeader String password) {
        return ResponseEntity.ok(userService.deleteByUserId(userId, password));
    }
}
