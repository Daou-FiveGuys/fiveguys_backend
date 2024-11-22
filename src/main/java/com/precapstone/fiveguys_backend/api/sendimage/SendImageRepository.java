package com.precapstone.fiveguys_backend.api.sendimage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SendImageRepository extends JpaRepository<SendImage, Long> {
    Optional<SendImage> findBySendImageId(Long sendImageId);

    void deleteBySendImageId(Long sendImageId);
}
