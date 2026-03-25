package com.campus.lostfound.dto;

import com.campus.lostfound.model.Item;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ItemDTO {

    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Category is required")
    private Item.Category category;

    @NotNull(message = "Type (LOST/FOUND) is required")
    private Item.ItemType type;

    private Item.Status   status;
    private LocalDate     dateOccurred;
    private String        imageUrl;

    // Response-only fields
    private Long          reportedById;
    private String        reportedByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ── Constructors ──────────────────────────────────────────────────

    public ItemDTO() {}

    // ── Static factory ────────────────────────────────────────────────

    public static ItemDTO fromEntity(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.id                  = item.getId();
        dto.title               = item.getTitle();
        dto.description         = item.getDescription();
        dto.location            = item.getLocation();
        dto.category            = item.getCategory();
        dto.type                = item.getType();
        dto.status              = item.getStatus();
        dto.dateOccurred        = item.getDateOccurred();
        dto.imageUrl            = item.getImageUrl();
        dto.createdAt           = item.getCreatedAt();
        dto.updatedAt           = item.getUpdatedAt();
        if (item.getReportedBy() != null) {
            dto.reportedById       = item.getReportedBy().getId();
            dto.reportedByUsername = item.getReportedBy().getUsername();
        }
        return dto;
    }

    // ── Getters & Setters ─────────────────────────────────────────────

    public Long getId()                            { return id; }
    public void setId(Long id)                     { this.id = id; }

    public String getTitle()                       { return title; }
    public void setTitle(String title)             { this.title = title; }

    public String getDescription()                 { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation()                    { return location; }
    public void setLocation(String location)       { this.location = location; }

    public Item.Category getCategory()             { return category; }
    public void setCategory(Item.Category category){ this.category = category; }

    public Item.ItemType getType()                 { return type; }
    public void setType(Item.ItemType type)        { this.type = type; }

    public Item.Status getStatus()                 { return status; }
    public void setStatus(Item.Status status)      { this.status = status; }

    public LocalDate getDateOccurred()             { return dateOccurred; }
    public void setDateOccurred(LocalDate date)    { this.dateOccurred = date; }

    public String getImageUrl()                    { return imageUrl; }
    public void setImageUrl(String imageUrl)       { this.imageUrl = imageUrl; }

    public Long getReportedById()                          { return reportedById; }
    public void setReportedById(Long reportedById)         { this.reportedById = reportedById; }

    public String getReportedByUsername()                          { return reportedByUsername; }
    public void setReportedByUsername(String reportedByUsername)   { this.reportedByUsername = reportedByUsername; }

    public LocalDateTime getCreatedAt()                    { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)      { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt()                    { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt)      { this.updatedAt = updatedAt; }
}
