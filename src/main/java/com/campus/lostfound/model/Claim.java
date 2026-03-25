package com.campus.lostfound.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claimed_by_id", nullable = false)
    private User claimedBy;

    @Column(length = 1000, nullable = false)
    private String proofDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status = ClaimStatus.PENDING;

    @Column(length = 500)
    private String adminNote;

    @Column(nullable = false, updatable = false)
    private LocalDateTime claimedAt = LocalDateTime.now();

    @Column
    private LocalDateTime resolvedAt;

    @Column(length = 500)
    private String documentPath;

    @Column(length = 500)
    private String imagePath;

    public enum ClaimStatus {
        PENDING, APPROVED, REJECTED
    }

    public Claim() {
    }

    public Long getId() { return id; }
    public Item getItem() { return item; }
    public User getClaimedBy() { return claimedBy; }
    public String getProofDescription() { return proofDescription; }
    public ClaimStatus getStatus() { return status; }
    public String getAdminNote() { return adminNote; }
    public LocalDateTime getClaimedAt() { return claimedAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public String getDocumentPath() { return documentPath; }
    public String getImagePath() { return imagePath; }

    public void setId(Long id) { this.id = id; }
    public void setItem(Item item) { this.item = item; }
    public void setClaimedBy(User claimedBy) { this.claimedBy = claimedBy; }
    public void setProofDescription(String proofDescription) { this.proofDescription = proofDescription; }
    public void setStatus(ClaimStatus status) { this.status = status; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }
    public void setClaimedAt(LocalDateTime claimedAt) { this.claimedAt = claimedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    public void setDocumentPath(String documentPath) { this.documentPath = documentPath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Item item;
        private User claimedBy;
        private String proofDescription;
        private ClaimStatus status = ClaimStatus.PENDING;
        private String adminNote;
        private LocalDateTime claimedAt = LocalDateTime.now();
        private LocalDateTime resolvedAt;
        private String documentPath;
        private String imagePath;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder item(Item item) { this.item = item; return this; }
        public Builder claimedBy(User claimedBy) { this.claimedBy = claimedBy; return this; }
        public Builder proofDescription(String proofDescription) { this.proofDescription = proofDescription; return this; }
        public Builder status(ClaimStatus status) { this.status = status; return this; }
        public Builder adminNote(String adminNote) { this.adminNote = adminNote; return this; }
        public Builder claimedAt(LocalDateTime claimedAt) { this.claimedAt = claimedAt; return this; }
        public Builder resolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; return this; }
        public Builder documentPath(String documentPath) { this.documentPath = documentPath; return this; }
        public Builder imagePath(String imagePath) { this.imagePath = imagePath; return this; }

        public Claim build() {
            Claim c = new Claim();
            c.id = this.id;
            c.item = this.item;
            c.claimedBy = this.claimedBy;
            c.proofDescription = this.proofDescription;
            c.status = this.status;
            c.adminNote = this.adminNote;
            c.claimedAt = this.claimedAt;
            c.resolvedAt = this.resolvedAt;
            c.documentPath = this.documentPath;
            c.imagePath = this.imagePath;
            return c;
        }
    }

    @Override
    public String toString() {
        return "Claim{id=" + id + ", status=" + status + "}";
    }
}