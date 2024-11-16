package com.precapstone.fiveguys_backend.api.folder2;

import com.precapstone.fiveguys_backend.entity.Folder2;
import com.precapstone.fiveguys_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Folder2Repository extends JpaRepository<Folder2, Long> {
    Optional<Folder2> findByUserAndName(User user, String name);
    Optional<List<Folder2>> findByUser(User user);
}
