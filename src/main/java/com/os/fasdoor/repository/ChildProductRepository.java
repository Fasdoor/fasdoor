//package com.os.fasdoor.repository;
//
//import com.os.fasdoor.model.ChildService;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//
//import java.util.Optional;
//
//public interface ChildProductRepository extends JpaRepository<ChildService, Long> {
//    Optional<ChildService> findByName(String name);
//    Optional<ChildService> existsByParentServiceId(Long parentId);
//    @Query(nativeQuery = true, value = "SELECT MAX(id) AS max_id FROM child_service;")
//    Long findMaxId();
//}
