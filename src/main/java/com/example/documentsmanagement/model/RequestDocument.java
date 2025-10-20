package com.example.documentsmanagement.model;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "request_document")
public class RequestDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRequestDocument;

    private String documentNumber;

    private LocalDate borrowDate;

    private String copyType; // e.g. Original, Copy

    private LocalDate returnDeadline;

    private int extensionCount;

    private String signer;

    private String attachmentPath;

    @ManyToOne
    @JoinColumn(name = "librarian_id")
    private Librarian librarian;

    @ManyToOne
    @JoinColumn(name = "borrower_id")
    private Borrower borrower;

    @ManyToOne
    @JoinColumn(name = "document_set_id")
    private DocumentSet documentSet;

    // Constructors
    public RequestDocument() {}

    public RequestDocument(String documentNumber, LocalDate borrowDate, String copyType, LocalDate returnDeadline, int extensionCount, String signer, String attachmentPath, Librarian librarian, Borrower borrower, DocumentSet documentSet) {
        this.documentNumber = documentNumber;
        this.borrowDate = borrowDate;
        this.copyType = copyType;
        this.returnDeadline = returnDeadline;
        this.extensionCount = extensionCount;
        this.signer = signer;
        this.attachmentPath = attachmentPath;
        this.librarian = librarian;
        this.borrower = borrower;
        this.documentSet = documentSet;
    }

    // Getters & Setters
    public Long getIdRequestDocument() {
        return idRequestDocument;
    }

    public void setId(Long idRequestDocument) {
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

    public String getSigner() {
        return signer;
    }

    public void setSigner(String signer) {
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

    public DocumentSet getDocumentSet() {
        return documentSet;
    }

    public void setDocumentSet(DocumentSet documentSet) {
        this.documentSet = documentSet;
    }
}