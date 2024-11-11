package com.precapstone.fiveguys_backend.common;

public interface ResponseMessage {
    String SUCCESS = "success";
    String ERROR = "error";
    String EMAIL_VERIFICAITION_REQUIRED = "email_verificiation_required";
    String LOGIN_FAILED = "login_failed";
    String USER_NOT_FOUND = "user_not_found";
    String EMAIL_SENT = "eail_sent";
    String EMAIL_SENT_FAILED = "email_sent_failed";
    String CHATBOT_ANS_FAIL = "chatbot_answer_failed";
}
