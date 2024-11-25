package com.precapstone.fiveguys_backend.api.dailyamount;

import com.precapstone.fiveguys_backend.api.amountused.AmountUsedType;
import com.precapstone.fiveguys_backend.entity.AmountUsed;
import com.precapstone.fiveguys_backend.entity.DailyAmount;
import com.precapstone.fiveguys_backend.exception.ControlledException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static com.precapstone.fiveguys_backend.exception.errorcode.AmountUsedErrorCode.AMOUNT_USED_NOT_FOUND;

@Log4j2
@Service
@RequiredArgsConstructor
public class DailyAmountService {
    private final DailyAmountRepository dailyAmountRepository;

    public DailyAmount read(AmountUsed amountUsed, LocalDate localDate) {
        var dailyAmount = dailyAmountRepository.findByAmountUsedAndDate(amountUsed, localDate)
                .orElseGet(() -> dailyAmountRepository.save(DailyAmount.builder().amountUsed(amountUsed).date(localDate).build()));

        log.info("AmountUsed Info: "+dailyAmount.getAmountUsed());
        return dailyAmount;
    }

    public DailyAmount plus(AmountUsed amountUsed, AmountUsedType amountUsedType, Integer plus) {
        var dailyAmount = read(amountUsed, LocalDate.now());

        switch (amountUsedType) {
            case MSG_SCNT: // 문자 전송 카운트 (문자를 전송한 인원수)
                dailyAmount.setMsgScnt(dailyAmount.getMsgScnt()+plus);
                break;
            case MSG_GCNT: // 문자 생성 카운트 (문자 전송 횟수)
                dailyAmount.setMsgGcnt(dailyAmount.getMsgGcnt()+plus);
                break;
            case IMG_SCNT: // 이미지 전송 카운트 (이미지를 전송한 인원수)
                dailyAmount.setImgScnt(dailyAmount.getImgScnt()+plus);
                break;
            case IMG_GCNT: // 이미지 생성 카운트 (이미지 전송 횟수)
                dailyAmount.setImgGcnt(dailyAmount.getImgGcnt()+plus);
                break;
            default:
                throw new ControlledException(AMOUNT_USED_NOT_FOUND);
        }

        dailyAmountRepository.save(dailyAmount);
        return dailyAmount;
    }
}
