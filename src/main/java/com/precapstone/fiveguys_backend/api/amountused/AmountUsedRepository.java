package com.precapstone.fiveguys_backend.api.amountused;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AmountUsedRepository extends JpaRepository<AmountUsed, Long> {
    Optional<AmountUsed> findByAmountUsedId(Long amountUsed);
}
