package com.precapstone.fiveguys_backend.api.ai;

import com.precapstone.fiveguys_backend.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageGenRepository extends JpaRepository<Image, Long> {
}
