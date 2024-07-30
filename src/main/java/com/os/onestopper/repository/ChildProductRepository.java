package com.os.onestopper.repository;

import com.os.onestopper.model.ChildService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ChildProductRepository extends JpaRepository<ChildService, Long> {
    Optional<ChildService> findByName(String name);
    Optional<ChildService> existsByParentServiceId(Long parentId);
    @Query(nativeQuery = true, value = "SELECT MAX(id) AS max_id FROM child_service;")
    Long findMaxId();
}
