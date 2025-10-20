package com.example.documentsmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.documentsmanagement.model.Librarian;
import java.util.List;
import java.util.Optional;

public interface LibrarianRepository extends JpaRepository<Librarian, Long> {

    // 🔍 Tìm kiếm theo tên (giữ nguyên của bạn)
    @Query("""
        SELECT e FROM Librarian e 
        WHERE LOWER(COALESCE(e.fullName, '')) LIKE LOWER(CONCAT('%', :q, '%'))
    """)
    List<Librarian> searchByName(@Param("q") String q);

    // 🔑 Tìm theo username (để kiểm tra đăng nhập)
    Optional<Librarian> findByUsername(String username);

    // 🔒 Hoặc nếu muốn kiểm tra username + password cùng lúc:
    Optional<Librarian> findByUsernameAndPassword(String username, String password);
}
