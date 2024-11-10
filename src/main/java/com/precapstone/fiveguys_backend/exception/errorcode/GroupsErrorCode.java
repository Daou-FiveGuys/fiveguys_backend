package com.precapstone.fiveguys_backend.exception.errorcode;

import com.precapstone.fiveguys_backend.exception.ErrorMessage;

public interface GroupsErrorCode {
    ErrorMessage GROUP_NOT_FOUND = new ErrorMessage(-1001, "조회된 그룹이 없습니다.");
    ErrorMessage PARENT_GROUP_NOT_FOUND = new ErrorMessage(-1002,"존재하지 않는 ParentGroup입니다.");
    ErrorMessage GROUP_ALREADY_EXISTS = new ErrorMessage(-1003,"그룹명이 이미 존재합니다.");
}
