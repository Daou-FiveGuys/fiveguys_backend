package com.precapstone.fiveguys_backend.api.message.send;

import com.precapstone.fiveguys_backend.exception.ErrorMessage;

public interface PpurioErrorCode {
    ErrorMessage CONTENT_IS_TOO_LONG = new ErrorMessage(404, "본문이 너무 깁니다. (본문 크기는 최대 90/2000Byte입니다.)");
    ErrorMessage FILE_IS_TOO_BIG = new ErrorMessage(404, "이미지가 너무 큽니다. (이미지 크기는 최대 300KB입니다.)");
}
