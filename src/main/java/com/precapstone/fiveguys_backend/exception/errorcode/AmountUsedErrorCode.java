package com.precapstone.fiveguys_backend.exception.errorcode;

import com.precapstone.fiveguys_backend.exception.ErrorMessage;

public interface AmountUsedErrorCode {
    ErrorMessage AMOUNT_USED_NOT_FOUND = new ErrorMessage(-5001, "조회된 사용량이 없습니다.");
    ErrorMessage AMOUNT_USED_TYPE_NOT_FOUND = new ErrorMessage(-5002, "올바르지 않은 사용량 타입입니다.");
}
