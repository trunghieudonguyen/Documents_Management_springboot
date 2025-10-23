package com.example.documentsmanagement.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.documentsmanagement.model.Librarian;
import java.util.List;
import java.util.Optional;


public interface LibrarianRepository extends JpaRepository<Librarian, Long> {


    @Query("""
        SELECT e FROM Librarian e
        WHERE LOWER(COALESCE(e.fullName, '')) LIKE LOWER(CONCAT('%', :q, '%'))
    """)
    List<Librarian> searchByName(@Param("q") String q);


    Optional<Librarian> findByUsername(String username);


    // Sử dụng SQL gốc để áp dụng Oracle STANDARD_HASH vào mật khẩu đến và so sánh
    @Query(value = "SELECT * FROM librarian l WHERE l.username = :username AND l.password = RAWTOHEX(STANDARD_HASH(:password, 'SHA256'))", nativeQuery = true)
    Optional<Librarian> findByUsernameAndPasswordHashed(@Param("username") String username, @Param("password") String password);
}