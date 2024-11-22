package com.precapstone.fiveguys_backend.api.amountused;

import com.precapstone.fiveguys_backend.api.auth.JwtTokenProvider;
import com.precapstone.fiveguys_backend.api.user.UserService;
import com.precapstone.fiveguys_backend.entity.AmountUsed;
import com.precapstone.fiveguys_backend.entity.User;
import com.precapstone.fiveguys_backend.exception.ControlledException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.precapstone.fiveguys_backend.exception.errorcode.AmountUsedErrorCode.AMOUNT_USED_NOT_FOUND;
import static com.precapstone.fiveguys_backend.exception.errorcode.UserErrorCode.USER_AUTHORIZATION_FAILED;
import static com.precapstone.fiveguys_backend.exception.errorcode.UserErrorCode.USER_NOT_FOUND;

@Log
@Service
@RequiredArgsConstructor
public class AmountUsedService {
    public final AmountUsedRepository amountUsedRepository;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    // private final DailyThread dailyThread = new DailyThread();

    public AmountUsed create(User user) {
        var amountUsed = AmountUsed.builder()
                .user(user)
                .build();

        amountUsedRepository.save(amountUsed);
        return amountUsed;
    }

    /**
     * Controller 용
     */
    public AmountUsed read(String accessToken) {
        var userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        var user = userService.findByUserId(userId)
                .orElseThrow(() -> new ControlledException(USER_NOT_FOUND));

        var amountUsed = user.getAmountUsed();

        return amountUsed;
    }

    /**
     * 서버용
     */
    public AmountUsed read(Long amountUsedId) {
        var amountUsed = amountUsedRepository.findByAmountUsedId(amountUsedId)
                .orElseThrow(()->new ControlledException(AMOUNT_USED_NOT_FOUND));

        return amountUsed;
    }

    /**
     * 메세지 사용량 카운트를 증가시키는 함수
     *
     */
    public AmountUsed plus(String userId, AmountUsedType amountUsedType, Integer plus) {
        var amountUsed = userService.findByUserId(userId)
                .orElseThrow(()->new ControlledException(USER_NOT_FOUND)).getAmountUsed();

        switch (amountUsedType) {
            case MSG_SCNT: // 문자 전송 카운트
                amountUsed.setMsgScnt(amountUsed.getMsgScnt()+plus);
                break;
            case MSG_GCNT: // 문자 생성 카운트 // TODO: 이거 기준이 모호
                amountUsed.setMsgGcnt(amountUsed.getMsgGcnt()+plus);
                break;
            case IMG_SCNT: // 이미지 전송 카운트
                amountUsed.setImgScnt(amountUsed.getImgScnt()+plus);
                break;
            case IMG_GCNT: // 이미지 생성 카운트
                amountUsed.setImgGcnt(amountUsed.getImgGcnt()+plus);
                amountUsed.setImgDcnt(amountUsed.getImgDcnt()+plus);
                amountUsed.setImgDate(LocalDateTime.now());
                break;
            default:
                throw new ControlledException(AMOUNT_USED_NOT_FOUND);
        }

        amountUsedRepository.save(amountUsed);
        return amountUsed;
    }

    /**
     * 일간 이용자 카운트 초기화
     *
     */
    public List<AmountUsed> initDcnt() {
        var allAmountUsed = amountUsedRepository.findAll();

        allAmountUsed.forEach(amountUsed -> {amountUsed.setImgDcnt(0);});

        return allAmountUsed;
    }

    public AmountUsed delete(Long amountUsedId) {
        var amountUsed = read(amountUsedId);

        amountUsedRepository.deleteById(amountUsedId);
        return amountUsed;
    }

    private class DailyThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    log.info("진입!!");
                    // 현재 시간 가져오기
                    long currentTimeMillis = System.currentTimeMillis();
                    java.util.Calendar calendar = java.util.Calendar.getInstance();
                    calendar.setTimeInMillis(currentTimeMillis);

                    // 다음 00시 00분까지의 대기 시간 계산
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 1); // 다음 날로 설정
                    calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
                    calendar.set(java.util.Calendar.MINUTE, 0);
                    calendar.set(java.util.Calendar.SECOND, 0);
                    calendar.set(java.util.Calendar.MILLISECOND, 0);

                    long nextMidnightMillis = calendar.getTimeInMillis();
                    long sleepTime = nextMidnightMillis - currentTimeMillis;

                    // 다음 00시 00분까지 대기
                    Thread.sleep(sleepTime);

                    // 00시 00분이 지나면 initDcnt() 호출
                    initDcnt();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
