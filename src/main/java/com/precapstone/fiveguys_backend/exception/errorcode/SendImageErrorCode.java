package com.precapstone.fiveguys_backend.exception.errorcode;

import com.precapstone.fiveguys_backend.exception.ErrorMessage;

public interface SendImageErrorCode {
    ErrorMessage SEND_IMAGE_NOT_FOUND = new ErrorMessage(-7001, "조회된 전송 이미지 기록이 없습니다.");
}
