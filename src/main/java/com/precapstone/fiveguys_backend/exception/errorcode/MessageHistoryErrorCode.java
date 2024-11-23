package com.precapstone.fiveguys_backend.exception.errorcode;

import com.precapstone.fiveguys_backend.exception.ErrorMessage;

public interface MessageHistoryErrorCode {
    ErrorMessage MESSAGE_HISTORY_NOT_FOUND = new ErrorMessage(-6001, "조회된 메세지 기록이 없습니다.");
}
