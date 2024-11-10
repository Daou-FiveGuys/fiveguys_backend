package com.precapstone.fiveguys_backend.api.dto;

import com.precapstone.fiveguys_backend.common.enums.UserRole;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@Data
public class UserInfoResponseDTO {
    String email;
    String userId;
    String name;
    UserRole userRole;
}
