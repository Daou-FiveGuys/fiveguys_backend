package com.precapstone.fiveguys_backend.exception.errorcode;

import com.precapstone.fiveguys_backend.exception.ErrorMessage;

public interface BasicErrorCode {
    ErrorMessage DATA_BASE_ERROR = new ErrorMessage(404,"데이터베이스 접근 오류");
}
