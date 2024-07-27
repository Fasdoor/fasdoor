package com.os.onestopper.repository;

import com.os.onestopper.model.ChildService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChildProductRepository extends JpaRepository<ChildService, Long> {
    Optional<ChildService> findByName(String name);
}
