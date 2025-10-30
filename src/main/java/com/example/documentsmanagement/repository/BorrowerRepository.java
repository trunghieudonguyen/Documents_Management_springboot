package com.example.documentsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.documentsmanagement.model.Borrower;
import java.util.List;

import java.util.Optional;


public interface BorrowerRepository extends JpaRepository<Borrower, Long> {

    // Simple example search by a 'name' property if present
    @Query("""
        select e from Borrower e where lower(COALESCE(e.fullName, '')) like lower(concat('%', :q, '%'))
        OR lower(COALESCE(e.employeeCode, '')) like lower(concat('%', :q, '%'))
    """)

    List<Borrower> searchByName(@Param("q") String q);

    Optional<Borrower> findByEmployeeCode(String employeeCode);
}