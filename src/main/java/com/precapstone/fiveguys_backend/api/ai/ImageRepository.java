package com.precapstone.fiveguys_backend.api.ai;

import com.precapstone.fiveguys_backend.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findImageByRequestId(String originalRequestId);
}
