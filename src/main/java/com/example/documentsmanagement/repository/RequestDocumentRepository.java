package com.example.documentsmanagement.repository;

import com.example.documentsmanagement.model.RequestDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RequestDocumentRepository extends JpaRepository<RequestDocument, Long> {

    // 🔍 Tìm kiếm theo số tài liệu hoặc người ký
    @Query("""
        SELECT r FROM RequestDocument r
        WHERE LOWER(r.documentNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(r.signer) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ORDER BY r.borrowDate DESC
    """)
    List<RequestDocument> searchByKeyword(@Param("keyword") String keyword);

    // 📅 Các phiếu sắp đến hạn và đã quá hạn
    List<RequestDocument> findByReturnDeadlineBetween(LocalDate start, LocalDate end);
    List<RequestDocument> findByReturnDeadlineBefore(LocalDate date);

    // 📜 Lịch sử mượn của 1 tài liệu cụ thể (theo idDocument)
    @Query("""
        SELECT r FROM RequestDocument r
        JOIN FETCH r.documents
        WHERE r.idRequestDocument IN (
            SELECT r2.idRequestDocument FROM RequestDocument r2
            JOIN r2.documents d2
            WHERE d2.idDocument = :documentId
        )
        ORDER BY r.borrowDate DESC
    """)
    List<RequestDocument> findHistoryByDocumentId(@Param("documentId") Long documentId);


    // 📆 Lấy các phiếu mượn trong 1 ngày cụ thể
    @Query("""
        SELECT r FROM RequestDocument r
        WHERE r.borrowDate = :date
        ORDER BY r.borrowDate DESC
    """)
    List<RequestDocument> findByDate(@Param("date") LocalDate date);

    // 🗓️ Lấy các phiếu mượn trong 1 tháng + năm cụ thể (chuẩn Oracle)
    @Query("""
        SELECT r FROM RequestDocument r
        WHERE EXTRACT(MONTH FROM r.borrowDate) = :month
          AND EXTRACT(YEAR FROM r.borrowDate) = :year
        ORDER BY r.borrowDate DESC
    """)
    List<RequestDocument> findByMonth(@Param("month") int month, @Param("year") int year);

    // 📅 Lấy các phiếu mượn trong 1 năm cụ thể (chuẩn Oracle)
    @Query("""
        SELECT r FROM RequestDocument r
        WHERE EXTRACT(YEAR FROM r.borrowDate) = :year
        ORDER BY r.borrowDate DESC
    """)
    List<RequestDocument> findByYear(@Param("year") int year);

    // ⏳ Lấy các phiếu mượn trong khoảng thời gian cụ thể
    @Query("""
        SELECT r FROM RequestDocument r
        WHERE r.borrowDate BETWEEN :start AND :end
        ORDER BY r.borrowDate DESC
    """)
    List<RequestDocument> findByDateRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

    // 📈 Đếm số phiếu mượn trong tháng cụ thể
    @Query("""
        SELECT COUNT(r) FROM RequestDocument r
        WHERE EXTRACT(MONTH FROM r.borrowDate) = :month
          AND EXTRACT(YEAR FROM r.borrowDate) = :year
    """)
    long countByMonth(@Param("month") int month, @Param("year") int year);

    // 📊 Đếm số phiếu mượn trong năm cụ thể
    @Query("""
        SELECT COUNT(r) FROM RequestDocument r
        WHERE EXTRACT(YEAR FROM r.borrowDate) = :year
    """)
    long countByYear(@Param("year") int year);
}
