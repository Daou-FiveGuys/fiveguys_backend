package com.precapstone.fiveguys_backend.exception.errorcode;

import com.precapstone.fiveguys_backend.exception.ErrorMessage;

public interface Contact2ErrorCode {
    ErrorMessage CONTACT2_NOT_FOUND = new ErrorMessage(-5001, "조회된 주소록2가 없습니다.");
}
