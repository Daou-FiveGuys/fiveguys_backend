package com.precapstone.fiveguys_backend.exception.errorcode;

import com.precapstone.fiveguys_backend.exception.ErrorMessage;

public interface Group2ErrorCode {
    ErrorMessage GROUP2_NOT_FOUND = new ErrorMessage(-4001, "조회된 그룹2가 없습니다.");
    ErrorMessage INVALID_FORMAT_BY_FOLDER2_ID = new ErrorMessage(-4002, "잘못된 형식의 FOLDER2ID 입니다.");
}
