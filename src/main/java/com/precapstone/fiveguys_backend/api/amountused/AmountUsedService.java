package com.precapstone.fiveguys_backend.api.amountused;

import com.precapstone.fiveguys_backend.api.auth.JwtTokenProvider;
import com.precapstone.fiveguys_backend.api.dailyamount.DailyAmountService;
import com.precapstone.fiveguys_backend.api.user.UserService;
import com.precapstone.fiveguys_backend.entity.AmountUsed;
import com.precapstone.fiveguys_backend.entity.DailyAmount;
import com.precapstone.fiveguys_backend.entity.User;
import com.precapstone.fiveguys_backend.exception.ControlledException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private final DailyAmountService dailyAmountService;

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
    @Transactional
    public AmountUsed read(String accessToken) {
        var userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        var user = userService.findByUserId(userId)
                .orElseThrow(() -> new ControlledException(USER_NOT_FOUND));

        var amountUsed = user.getAmountUsed();
        Hibernate.initialize(amountUsed.getDailyAmounts());

        return amountUsed;
    }

    /**
     * 서버용
     */
    @Transactional
    public AmountUsed read(Long amountUsedId) {
        var amountUsed = amountUsedRepository.findByAmountUsedId(amountUsedId)
                .orElseThrow(()->new ControlledException(AMOUNT_USED_NOT_FOUND));

        Hibernate.initialize(amountUsed.getDailyAmounts());

        return amountUsed;
    }

    public DailyAmount readByDay(String accessToken, LocalDate localDate) {
        var userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        var amountUsed = userService.findByUserId(userId)
                .orElseThrow(() -> new ControlledException(USER_NOT_FOUND))
                .getAmountUsed();

        var dailyAmount = dailyAmountService.read(amountUsed, localDate);
        return dailyAmount;
    }

    /**
     * 메세지 사용량 카운트를 증가시키는 함수
     *
     */
    public AmountUsed plus(String userId, AmountUsedType amountUsedType, Integer plus) {
        var amountUsed = userService.findByUserId(userId)
                .orElseThrow(()->new ControlledException(USER_NOT_FOUND)).getAmountUsed();

        switch (amountUsedType) {
            case MSG_SCNT: // 문자 전송 카운트 (문자를 전송한 인원수)
                amountUsed.setMsgScnt(amountUsed.getMsgScnt()+plus);
                dailyAmountService.plus(amountUsed, amountUsedType, plus);
                amountUsed.setLastDate(LocalDateTime.now());
                amountUsedRepository.save(amountUsed);
                break;
            case MSG_GCNT: // 문자 생성 카운트 (문자 전송 횟수)
                amountUsed.setMsgGcnt(amountUsed.getMsgGcnt()+plus);
                dailyAmountService.plus(amountUsed, amountUsedType, plus);
                break;
            case IMG_SCNT: // 이미지 전송 카운트 (이미지를 전송한 인원수)
                amountUsed.setImgScnt(amountUsed.getImgScnt()+plus);
                dailyAmountService.plus(amountUsed, amountUsedType, plus);
                break;
            case IMG_GCNT: // 이미지 생성 카운트 (이미지 전송 횟수)
                amountUsed.setImgGcnt(amountUsed.getImgGcnt()+plus);
                dailyAmountService.plus(amountUsed, amountUsedType, plus);
                break;
            default:
                throw new ControlledException(AMOUNT_USED_NOT_FOUND);
        }

        amountUsedRepository.save(amountUsed);
        return amountUsed;
    }

    public AmountUsed delete(Long amountUsedId) {
        var amountUsed = read(amountUsedId);

        amountUsedRepository.deleteById(amountUsedId);
        return amountUsed;
    }
}
