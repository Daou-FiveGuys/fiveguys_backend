package com.precapstone.fiveguys_backend.exception.errorcode;

import com.precapstone.fiveguys_backend.exception.ErrorMessage;

public interface MessageHistoryErrorCode {
    ErrorMessage MESSAGE_HISTORY_NOT_FOUND = new ErrorMessage(-6001, "조회된 메세지 기록이 없습니다.");
    ErrorMessage MESSAGE_HISTORY_NOT_FOUND_BY_USER = new ErrorMessage(-6001, "유저로부터 조회된 메세지 기록이 없습니다.");
}
