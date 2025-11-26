package com.example.documentsmanagement.repository;

import com.example.documentsmanagement.model.Signer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

import java.util.Optional;

public interface SignerRepository extends JpaRepository<Signer, Long> {
    @Query("SELECT s FROM Signer s WHERE LOWER(s.fullName) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(s.staffCode) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Signer> search(@Param("q") String q);

    Optional<Signer> findByStaffCode(String staffCode);
}