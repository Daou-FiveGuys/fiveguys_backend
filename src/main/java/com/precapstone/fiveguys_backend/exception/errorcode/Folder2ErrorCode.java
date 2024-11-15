package com.precapstone.fiveguys_backend.exception.errorcode;

import com.precapstone.fiveguys_backend.exception.ErrorMessage;

public interface Folder2ErrorCode {
    ErrorMessage FOLDER2_NAME_ALREADY_EXISTS = new ErrorMessage(-3001, "이미 존재하는 FOLDER2_NAME입니다.");
    ErrorMessage FOLDER2_NOT_FOUND = new ErrorMessage(-3002, "조회된 FOLDER2가 없습니다.");
    ErrorMessage FOLDER2_NOT_FOUND_BY_USER = new ErrorMessage(-3002, "해당 유저로 조회된 FOLDER2가 없습니다.");
}
