package com.example.documentsmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entity representing a document extension (renewal) record.
 *
 * <p>
 * Each {@link RequestDocument} can have multiple {@link DocumentDuration} entries,
 * representing the history of renewal actions.
 * </p>
 */
@Entity
@Table(name = "DOCUMENT_DURATION")
public class DocumentDuration {

    /**
     * Primary key for DocumentDuration.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DOCUMENT_EXTENSION")
    private Long idDocumentDuration;

    /**
     * The related RequestDocument entity that this extension belongs to.
     * Many extensions can be associated with one document request.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_REQUEST_DOCUMENT", nullable = false)
    private RequestDocument requestDocument;

    /**
     * Number of times this document has been extended (renewal count).
     */
    @Column(name = "EXTENSION_COUNT")
    private Integer extensionCount;

    /**
     * Date when the document extension was issued.
     */
    @Column(name = "EXTENSION_DATE")
    private LocalDate extensionDate;

    /**
     * Path or filename of the attached image (e.g. proof or supporting document).
     */
    @Column(name = "ATTACHED_IMAGE", columnDefinition = "TEXT")
    private String attachedImage;

    // ------------------------------
    // Constructors
    // ------------------------------

    /**
     * Default constructor.
     */
    public DocumentDuration() {}

    /**
     * Parameterized constructor for convenience.
     *
     * @param requestDocument The related RequestDocument.
     * @param extensionCount  The number of extensions made.
     * @param extensionDate   The date of this extension.
     * @param attachedImage   The attached image (if any).
     */
    public DocumentDuration(RequestDocument requestDocument, Integer extensionCount,
                            LocalDate extensionDate, String attachedImage) {
        this.requestDocument = requestDocument;
        this.extensionCount = extensionCount;
        this.extensionDate = extensionDate;
        this.attachedImage = attachedImage;
    }

    // ------------------------------
    // Getters and Setters
    // ------------------------------

    public Long getIdDocumentDuration() {
        return idDocumentDuration;
    }

    public void setIdDocumentDuration(Long idDocumentDuration) {
        this.idDocumentDuration = idDocumentDuration;
    }

    public RequestDocument getRequestDocument() {
        return requestDocument;
    }

    public void setRequestDocument(RequestDocument requestDocument) {
        this.requestDocument = requestDocument;
    }

    public Integer getExtensionCount() {
        return extensionCount;
    }

    public void setExtensionCount(Integer extensionCount) {
        this.extensionCount = extensionCount;
    }

    public LocalDate getExtensionDate() {
        return extensionDate;
    }

    public void setExtensionDate(LocalDate extensionDate) {
        this.extensionDate = extensionDate;
    }

    public String getAttachedImage() {
        return attachedImage;
    }

    public void setAttachedImage(String attachedImage) {
        this.attachedImage = attachedImage;
    }
}
