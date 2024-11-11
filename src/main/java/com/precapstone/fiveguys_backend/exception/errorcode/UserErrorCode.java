package com.precapstone.fiveguys_backend.exception.errorcode;

import com.precapstone.fiveguys_backend.exception.ErrorMessage;

public interface UserErrorCode {
    ErrorMessage USER_NOT_FOUND = new ErrorMessage(404, "올바르지 않은 AccessToken입니다.");
}
