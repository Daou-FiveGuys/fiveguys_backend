package com.precapstone.fiveguys_backend.api.ai;

import com.precapstone.fiveguys_backend.entity.ImageResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageGenRepository extends JpaRepository<ImageResult, Long> {
    Optional<ImageResult> findImageByOriginalRequestId(String originalRequestId);
}
