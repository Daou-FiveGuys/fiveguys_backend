package com.precapstone.fiveguys_backend.api.contact2;

import com.precapstone.fiveguys_backend.entity.Contact2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Contact2Repository extends JpaRepository<Contact2, Long> {
}
