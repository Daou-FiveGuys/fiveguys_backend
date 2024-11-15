package com.precapstone.fiveguys_backend.api.contact2;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contact2/")
@RequiredArgsConstructor
public class Contact2Controller {
    private final Contact2Service contact2Service;
}
