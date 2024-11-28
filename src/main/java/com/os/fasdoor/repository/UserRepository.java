package com.os.fasdoor.repository;

import com.os.fasdoor.model.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
    Optional<ApplicationUser> findByEmailId(String email);
    Optional<ApplicationUser> findByPhoneNumber(String phoneNumber);
}
