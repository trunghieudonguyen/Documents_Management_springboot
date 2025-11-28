package com.example.documentsmanagement.repository;

import com.example.documentsmanagement.model.Librarian;
import com.example.documentsmanagement.model.Signer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link Librarian} entities.
 *
 * <p>Provides methods for searching, authentication, and convenient data access.</p>
 */
public interface LibrarianRepository extends JpaRepository<Librarian, Long> {

    // ============================================================
    // 🔍 TÌM KIẾM CƠ BẢN
    // ============================================================

    /**
     * Tìm kiếm theo tên (không phân biệt hoa thường).
     */
    @Query("""
        SELECT l FROM Librarian l
        WHERE LOWER(COALESCE(l.fullName, '')) LIKE LOWER(CONCAT('%', :q, '%'))
    """)
    List<Librarian> searchByName(@Param("q") String q);

    /**
     * Tìm theo username.
     */
    Optional<Librarian> findByUsername(String username);

    /**
     * Tìm theo email.
     */
    Optional<Librarian> findByEmail(String email);

    /**
     * Tìm theo số điện thoại.
     */
    Optional<Librarian> findByPhoneNumber(String phoneNumber);

    /**
     * Tìm theo email hoặc số điện thoại (dùng cho chức năng "Quên mật khẩu").
     */
    @Query("SELECT l FROM Librarian l WHERE l.email = :input OR l.phoneNumber = :input")
    Optional<Librarian> findByEmailOrPhone(@Param("input") String input);

    
    Optional<Librarian> findByStaffCode(String staffCode);

    // ============================================================
    // 🔐 XÁC THỰC TÀI KHOẢN
    // ============================================================

    /**
     * Tìm theo username và password (đã hash bằng Oracle STANDARD_HASH).
     *
     * Lưu ý: so sánh trực tiếp giá trị hash trong CSDL (Oracle RAWTOHEX(STANDARD_HASH(..., 'SHA256'))).
     */
    @Query(value = """
        SELECT * FROM librarian l
        WHERE l.username = :username
        AND l.password = RAWTOHEX(STANDARD_HASH(:password, 'SHA256'))
        """, nativeQuery = true)
    Optional<Librarian> findByUsernameAndPasswordHashed(@Param("username") String username,
                                                        @Param("password") String password);

    // ============================================================
    // 🧩 TIỆN ÍCH BỔ SUNG
    // ============================================================

    /**
     * Lấy thủ thư theo ID, ném lỗi nếu không tìm thấy.
     * Giúp tránh lặp lại đoạn Optional.orElseThrow() trong Service.
     *
     * @param id ID của thủ thư
     * @return {@link Librarian} tương ứng
     * @throws IllegalArgumentException nếu không tìm thấy thủ thư
     */
    default Librarian getByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thủ thư với ID: " + id));
    }
}
