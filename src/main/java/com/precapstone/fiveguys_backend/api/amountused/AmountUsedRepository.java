package com.precapstone.fiveguys_backend.api.amountused;

import com.precapstone.fiveguys_backend.entity.AmountUsed;
import com.precapstone.fiveguys_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AmountUsedRepository extends JpaRepository<AmountUsed, Long> {
    Optional<AmountUsed> findByAmountUsedId(Long amountUsed);

    Optional<AmountUsed> findByUser(User user);
}
