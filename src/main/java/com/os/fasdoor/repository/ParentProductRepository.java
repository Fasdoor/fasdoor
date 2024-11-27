package com.os.fasdoor.repository;

import com.os.fasdoor.model.ParentServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ParentProductRepository extends JpaRepository<ParentServices, Long> {
    Optional<ParentServices> findByName(String name);
    @Query(nativeQuery = true, value = "SELECT MAX(id) AS max_id FROM parent_service;")
    Long findMaxId();
}
