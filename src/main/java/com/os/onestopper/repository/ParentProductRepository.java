package com.os.onestopper.repository;

import com.os.onestopper.model.ParentServices;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParentProductRepository extends JpaRepository<ParentServices, Long> {
    Optional<ParentServices> findByName(String name);
}
