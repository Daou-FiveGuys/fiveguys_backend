package com.precapstone.fiveguys_backend.api.messagehistory;

import com.precapstone.fiveguys_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface MessageHistoryRepository extends JpaRepository<MessageHistory, Long> {
    Optional<MessageHistory> findByMessageHistoryId(Long messageHistoryId);

    Optional<List<MessageHistory>> findByUser(User user);

    void deleteByMessageHistoryId(Long messageHistoryId);
}