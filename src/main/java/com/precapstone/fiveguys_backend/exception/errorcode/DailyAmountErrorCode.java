package com.precapstone.fiveguys_backend.exception.errorcode;

import com.precapstone.fiveguys_backend.exception.ErrorMessage;

public interface DailyAmountErrorCode {
    ErrorMessage DAILY_AMOUNT_NOT_FOUND = new ErrorMessage(-7001, "조회된 일일 사용량이 없습니다.");
    ErrorMessage DAILY_AMOUNT_NOT_FOUND_TODAY = new ErrorMessage(-7002, "당일 조회된 일일 사용량이 없습니다.");
}
