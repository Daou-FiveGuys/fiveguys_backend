package com.precapstone.fiveguys_backend.api.contact2;

import com.precapstone.fiveguys_backend.api.group2.Group2CreateDTO;
import com.precapstone.fiveguys_backend.api.group2.Group2UpdateDTO;
import com.precapstone.fiveguys_backend.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contact2/")
@RequiredArgsConstructor
public class Contact2Controller {
    private final Contact2Service contact2Service;

    // 주소록2 생성
    @PostMapping
    public ResponseEntity create(@RequestBody Contact2CreateDTO contact2CreateDTO) {
        var contact2 = contact2Service.create(contact2CreateDTO);

        var response = CommonResponse.builder().code(200).message("주소록2 생성 성공").data(contact2).build();
        return ResponseEntity.ok(response);
    }

    // 주소록2 조회
    @GetMapping("/{contact2Id}")
    public ResponseEntity read(@PathVariable Long contact2Id) {
        var contact2 = contact2Service.readContact2(contact2Id);

        var response = CommonResponse.builder().code(200).message("주소록2 조회 성공").data(contact2).build();
        return ResponseEntity.ok(response);
    }

    // 주소록2 수정
    @PatchMapping
    public ResponseEntity update(@RequestBody Contact2UpdateDTO contact2UpdateDTO) {
        var contact2 = contact2Service.update(contact2UpdateDTO);

        var response = CommonResponse.builder().code(200).message("주소록2 수정 성공").data(contact2).build();
        return ResponseEntity.ok(response);
    }

    // 주소록2 삭제
    @DeleteMapping("/{contact2Id}")
    public ResponseEntity delete(@PathVariable Long contact2Id) {
        var contact2 = contact2Service.delete(contact2Id);

        var response = CommonResponse.builder().code(200).message("주소록2 삭제 성공").data(contact2).build();
        return ResponseEntity.ok(response);
    }
}
