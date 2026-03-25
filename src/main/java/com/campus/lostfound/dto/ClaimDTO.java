package com.campus.lostfound.dto;

import com.campus.lostfound.model.Claim;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class ClaimDTO {

    private Long id;

    @NotNull(message = "Item ID is required")
    private Long itemId;

    @NotBlank(message = "Proof description is required")
    private String proofDescription;

    private String adminNote;

    private String documentPath;
    private String imagePath;

    // Response-only fields
    private Claim.ClaimStatus status;
    private Long claimedById;
    private String claimedByUsername;
    private String itemTitle;
    private LocalDateTime claimedAt;
    private LocalDateTime resolvedAt;

    public ClaimDTO() {}

    public static ClaimDTO fromEntity(Claim claim) {
        ClaimDTO dto = new ClaimDTO();
        dto.id = claim.getId();
        dto.proofDescription = claim.getProofDescription();
        dto.adminNote = claim.getAdminNote();
        dto.status = claim.getStatus();
        dto.claimedAt = claim.getClaimedAt();
        dto.resolvedAt = claim.getResolvedAt();

        dto.documentPath = claim.getDocumentPath();
        dto.imagePath = claim.getImagePath();

        if (claim.getItem() != null) {
            dto.itemId = claim.getItem().getId();
            dto.itemTitle = claim.getItem().getTitle();
        }

        if (claim.getClaimedBy() != null) {
            dto.claimedById = claim.getClaimedBy().getId();
            dto.claimedByUsername = claim.getClaimedBy().getUsername();
        }

        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public String getProofDescription() { return proofDescription; }
    public void setProofDescription(String proofDescription) { this.proofDescription = proofDescription; }

    public String getAdminNote() { return adminNote; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }

    public String getDocumentPath() { return documentPath; }
    public void setDocumentPath(String documentPath) { this.documentPath = documentPath; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public Claim.ClaimStatus getStatus() { return status; }
    public void setStatus(Claim.ClaimStatus status) { this.status = status; }

    public Long getClaimedById() { return claimedById; }
    public void setClaimedById(Long claimedById) { this.claimedById = claimedById; }

    public String getClaimedByUsername() { return claimedByUsername; }
    public void setClaimedByUsername(String claimedByUsername) { this.claimedByUsername = claimedByUsername; }

    public String getItemTitle() { return itemTitle; }
    public void setItemTitle(String itemTitle) { this.itemTitle = itemTitle; }

    public LocalDateTime getClaimedAt() { return claimedAt; }
    public void setClaimedAt(LocalDateTime claimedAt) { this.claimedAt = claimedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}