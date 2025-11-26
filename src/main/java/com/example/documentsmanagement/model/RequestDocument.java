package com.example.documentsmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "REQUEST_DOCUMENT")
public class RequestDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_REQUEST_DOCUMENT")
    private Long idRequestDocument;

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    @Column(name = "RETURN_DATE")
    private LocalDate returnDate;

    @Column(name = "DOCUMENT_NUMBER", length = 100)
    private String documentNumber;

    @Column(name = "BORROW_DATE")
    private LocalDate borrowDate;

    @Column(name = "COPY_TYPE", length = 50)
    private String copyType;

    @Column(name = "RETURN_DEADLINE")
    private LocalDate returnDeadline;

    @Column(name = "EXTENSION_COUNT")
    private int extensionCount;

    @ManyToOne
    @JoinColumn(name = "ID_SIGNER")
    private Signer signer;

    @Column(name = "ATTACHMENT_PATH", length = 255)
    private String attachmentPath;

    @ManyToOne
    @JoinColumn(name = "LIBRARIAN_ID")
    private Librarian librarian;

    @ManyToOne
    @JoinColumn(name = "BORROWER_ID")
    private Borrower borrower;

    // 🔗 Thêm liên kết nhiều RequestDocuments thuộc về một Document
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "REQUEST_DOCUMENT_ITEMS",
            joinColumns = @JoinColumn(name = "ID_REQUEST_DOCUMENT"),
            inverseJoinColumns = @JoinColumn(name = "ID_DOCUMENT")
    )
    private List<Document> documents = new ArrayList<>();

    @Column(name = "NOTE", columnDefinition = "CLOB")
    private String note;
    // Constructors
    public RequestDocument() {}

    public RequestDocument(String documentNumber, LocalDate borrowDate, String copyType,
                           LocalDate returnDeadline, int extensionCount, Signer signer,
                           String attachmentPath, Librarian librarian, Borrower borrower, List<Document> documents) {
        this.documentNumber = documentNumber;
        this.borrowDate = borrowDate;
        this.copyType = copyType;
        this.returnDeadline = returnDeadline;
        this.extensionCount = extensionCount;
        this.signer = signer;
        this.attachmentPath = attachmentPath;
        this.librarian = librarian;
        this.borrower = borrower;
        this.documents = documents;
    }

    // Getters & Setters
    public Long getIdRequestDocument() {
        return idRequestDocument;
    }

    public void setIdRequestDocument(Long idRequestDocument) {
        this.idRequestDocument = idRequestDocument;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getCopyType() {
        return copyType;
    }

    public void setCopyType(String copyType) {
        this.copyType = copyType;
    }

    public LocalDate getReturnDeadline() {
        return returnDeadline;
    }

    public void setReturnDeadline(LocalDate returnDeadline) {
        this.returnDeadline = returnDeadline;
    }

    public int getExtensionCount() {
        return extensionCount;
    }

    public void setExtensionCount(int extensionCount) {
        this.extensionCount = extensionCount;
    }

    public Signer getSigner() {
        return signer;
    }

    public void setSigner(Signer signer) {
        this.signer = signer;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public Librarian getLibrarian() {
        return librarian;
    }

    public void setLibrarian(Librarian librarian) {
        this.librarian = librarian;
    }

    public Borrower getBorrower() {
        return borrower;
    }

    public void setBorrower(Borrower borrower) {
        this.borrower = borrower;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    // @JsonIgnore
    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

}