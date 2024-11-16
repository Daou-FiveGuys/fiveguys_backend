package com.precapstone.fiveguys_backend.exception.errorcode;

import com.precapstone.fiveguys_backend.exception.ErrorMessage;

public interface Contact2ErrorCode {
    ErrorMessage CONTACT2_NOT_FOUND = new ErrorMessage(-5001, "조회된 주소록2가 없습니다.");
    ErrorMessage CONTACT2_NAME_ALREADY_EXISTS_IN_THIS_GROUP2 = new ErrorMessage(-5002, "이미 해당 그룹 내부에 동일한 이름의 주소록이 있습니다.");
    ErrorMessage CONTACT2_TELNUM_ALREADY_EXISTS_IN_THIS_GROUP2 = new ErrorMessage(-5002, "이미 해당 그룹 내부에 동일한 전화번호의 주소록이 있습니다.");
}
