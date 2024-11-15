package com.precapstone.fiveguys_backend.exception.errorcode;

import com.precapstone.fiveguys_backend.exception.ErrorMessage;

public interface Group2ErrorCode {
    ErrorMessage GROUP2_NOT_FOUND = new ErrorMessage(-4001, "조회된 그룹2가 없습니다.");
    ErrorMessage INVALID_FORMAT_BY_FOLDER2_ID = new ErrorMessage(-4002, "잘못된 형식의 FOLDER2ID 입니다.");
    ErrorMessage GROUP2_NAME_ALREADY_EXISTS_IN_THIS_FOLDER2 = new ErrorMessage(-4003, "이미 해당 폴더 내부에 동일한 이름의 그룹이 있습니다.");
}
