package com.precapstone.fiveguys_backend.exception.errorcode;

import com.precapstone.fiveguys_backend.exception.ErrorMessage;

public interface BasicErrorCode {
    ErrorMessage ERROR_NOT_FOUND = new ErrorMessage(404, "예기치 못한 오류");
    ErrorMessage ACCESS_DENIED = new ErrorMessage(403, "접근 권한이 없습니다.");
    ErrorMessage DATA_BASE_ERROR = new ErrorMessage(404,"데이터베이스 접근 오류");
}
