package com.precapstone.fiveguys_backend.api.group2;

import com.precapstone.fiveguys_backend.entity.Group2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Group2Repository extends JpaRepository<Group2, Long> {
}
