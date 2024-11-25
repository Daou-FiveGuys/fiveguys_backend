package com.precapstone.fiveguys_backend.api.dailyamount;

import com.precapstone.fiveguys_backend.entity.AmountUsed;
import com.precapstone.fiveguys_backend.entity.DailyAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface DailyAmountRepository extends JpaRepository<DailyAmount, Long> {
    Optional<DailyAmount> findByAmountUsedAndDate(AmountUsed amountUsed, LocalDate date);
    Optional<DailyAmount> findByDailyAmountId(Long dailyAmountId);
    void deleteByDailyAmountId(Long dailyAmountId);
}
