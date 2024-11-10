package com.precapstone.fiveguys_backend.exception.errorcode;

import com.precapstone.fiveguys_backend.exception.ErrorMessage;

public interface ContactErrorCode {
    ErrorMessage CONTACT_NAME_ALREADY_EXISTS = new ErrorMessage(-2001,"그룹내에 동일한 명칭이 이미 존재합니다.");
    ErrorMessage CONTACT_TELNUM_ALREADY_EXISTS = new ErrorMessage(-2002,"그룹내에 동일한 전화번호가 이미 존재합니다.");
    ErrorMessage CONTACT_NOT_FOUND = new ErrorMessage(-2003,"조회된 연락처가 없습니다.");
}
