package com.precapstone.fiveguys_backend.api.messagehistory;

import com.precapstone.fiveguys_backend.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Repository
public interface MessageHistoryRepository extends JpaRepository<MessageHistory, Long> {
    Optional<MessageHistory> findByMessageHistoryId(Long messageHistoryId);

    Optional<List<MessageHistory>> findByUser(User user);

    void deleteByMessageHistoryId(Long messageHistoryId);

    List<MessageHistory> findByUserAndCreatedAtBetween(User user, LocalDateTime start, LocalDateTime end);
}