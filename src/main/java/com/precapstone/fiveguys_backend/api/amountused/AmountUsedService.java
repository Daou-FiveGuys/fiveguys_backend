package com.precapstone.fiveguys_backend.api.amountused;

import com.precapstone.fiveguys_backend.entity.User;
import com.precapstone.fiveguys_backend.exception.ControlledException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.precapstone.fiveguys_backend.exception.errorcode.AmountUsedErrorCode.AMOUNT_USED_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AmountUsedService {
    public final AmountUsedRepository amountUsedRepository;

    public AmountUsed create(User user) {
        var amountUsed = AmountUsed.builder()
                .user(user)
                .build();

        amountUsedRepository.save(amountUsed);
        return amountUsed;
    }

    public AmountUsed read(Long amountUsedId) {
        var amountUsed = amountUsedRepository.findByAmountUsedId(amountUsedId)
                .orElseThrow(()->new ControlledException(AMOUNT_USED_NOT_FOUND));

        return amountUsed;
    }

    // 메세지 사용량 카운트를 증가시키는 함수
    public AmountUsed plus(Long amountUsedId, AmountUsedType amountUsedType, Integer plus) {
        var amountUsed = read(amountUsedId);

        switch (amountUsedType) {
            case MSG_SCNT: // 문자 전송 카운트
                amountUsed.setMsgScnt(amountUsed.getMsgScnt()+plus);
                break;
            case MSG_GCNT: // 문자 생성 카운트
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
     * @return
     */
    public List<AmountUsed> initDcnt() {
        var allAmountUsed = amountUsedRepository.findAll();

        allAmountUsed.forEach(amountUsed -> {amountUsed.setImgDcnt(0);});

        return allAmountUsed;
    }

    // 5. 일 이미지 생성 횟수 증가 (초기화 하는 배치 시스템 만들 것)

    public AmountUsed delete(Long amountUsedId) {
        var amountUsed = read(amountUsedId);

        amountUsedRepository.deleteById(amountUsedId);
        return amountUsed;
    }
}
