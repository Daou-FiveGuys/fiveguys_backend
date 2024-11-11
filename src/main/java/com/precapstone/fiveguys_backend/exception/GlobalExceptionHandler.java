package com.precapstone.fiveguys_backend.exception;

import com.precapstone.fiveguys_backend.common.CommonResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.precapstone.fiveguys_backend.exception.errorcode.BasicErrorCode.DATA_BASE_ERROR;
import static com.precapstone.fiveguys_backend.exception.errorcode.BasicErrorCode.ERROR_NOT_FOUND;

@Log4j2
@RestControllerAdvice
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * ControlledException을 통해 팀원이 직접적으로 추가한 ErrorCode를 제어한다.
     *
     * @param ex 반환된 ErrorCode 정보
     * @return exception.errorcode 패키지에 작성한 정보를 요청 클라이언트로 전송한다.
     */
    @ExceptionHandler(ControlledException.class)
    protected ResponseEntity handleControlledException(ControlledException ex) {
        var code = ex.getErrorCode().getStatus();
        var message = ex.getErrorCode().getMessage();

        log.info("error code: "+code+" message: "+message);
        log.info("explain: "+ ex.getMessage());

        return ResponseEntity.ok(CommonResponse.builder().code(code).message(message).build());
    }

    /**
     * mariadb 내부에서 발생한 오류를 제어한다.
     * ex) id가 조회되지 않는다. 중복된 id를 생성한다. ect...
     *
     * @param ex 반환된 Error 정보 ※ 직접적인 원인은 SpringBoot log를 통해 조회가능
     * @return [404] 데이버베이스 접근 오류
     */
    @ExceptionHandler(DataAccessException.class)
    protected ResponseEntity handleDataAccessException(DataAccessException ex) {
        var code = DATA_BASE_ERROR.getStatus();
        var message = DATA_BASE_ERROR.getMessage();

        log.info("error code: "+code+" message: "+message);
        log.info("explain: "+ ex.getMessage());

        return ResponseEntity.ok(CommonResponse.builder().code(code).message(message).build());
    }

    /**
     * 아무 Exception을 거치치 않고 반환된 ErrorException을 제어하는 핸들러
     * ex) 예측하지 못한 Error와 새로 제어해야 할 Exception 발생 시
     *
     * @param ex 반환된 Error 정보 ※ 직접적인 원인은 SpringBoot log를 통해 조회가능
     * @return [500] 에러 원인 불명
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity handleServerException(Exception ex) {
        var code = ERROR_NOT_FOUND.getStatus();
        var message = ERROR_NOT_FOUND.getMessage();

        log.info("#################### 예기치 못한 오류 발생 ####################");
        log.info("error code: " + code + " message: " + message);
        log.info("explain: " + ex.getMessage());

        return ResponseEntity.ok(CommonResponse.builder().code(code).message(message).build());
    }
}
