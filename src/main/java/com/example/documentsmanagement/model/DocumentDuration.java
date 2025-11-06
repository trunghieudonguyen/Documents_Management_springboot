package com.example.documentsmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entity representing a document Duration (renewal) record.
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
    @Column(name = "ID_DOCUMENT_Duration")
    private Long idDocumentDuration;

    /**
     * The related RequestDocument entity that this Duration belongs to.
     * Many Durations can be associated with one document request.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_REQUEST_DOCUMENT", nullable = false)
    private RequestDocument requestDocument;

    /**
     * Number of times this document has been extended (renewal count).
     */
    @Column(name = "Duration_COUNT")
    private Integer durationCount;

    /**
     * Date when the document Duration was issued.
     */
    @Column(name = "Duration_DATE")
    private LocalDate durationDate;

    /**
     * Path or filename of the attached image (e.g. proof or supporting document).
     */
    @Column(name = "ATTACHED_IMAGE", columnDefinition = "CLOB")
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
     * @param durationCount  The number of Durations made.
     * @param DurationDate   The date of this Duration.
     * @param attachedImage   The attached image (if any).
     */
    public DocumentDuration(RequestDocument requestDocument, Integer durationCount,
                            LocalDate DurationDate, String attachedImage) {
        this.requestDocument = requestDocument;
        this.durationCount = durationCount;
        this.durationDate = DurationDate;
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

    public Integer getDurationCount() {
        return durationCount;
    }

    public void setDurationCount(Integer durationCount) {
        this.durationCount = durationCount;
    }

    public LocalDate getDurationDate() {
        return durationDate;
    }

    public void setDurationDate(LocalDate durationDate) {
        this.durationDate = durationDate;
    }

    public String getAttachedImage() {
        return attachedImage;
    }

    public void setAttachedImage(String attachedImage) {
        this.attachedImage = attachedImage;
    }
}