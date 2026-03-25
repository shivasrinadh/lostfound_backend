package com.campus.lostfound.model;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, length = 100)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType type = ItemType.LOST;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.OPEN;

    private LocalDate dateOccurred;

    @Column(length = 500)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by_id", nullable = false)
    private User reportedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Claim> claims = new ArrayList<>();

    // ── Enums ─────────────────────────────────────────────────────────

    public enum Category {
        ELECTRONICS, CLOTHING, ACCESSORIES, BOOKS, KEYS,
        WALLET, BAGS, SPORTS, DOCUMENTS, JEWELRY, OTHER
    }

    public enum ItemType {
        LOST, FOUND
    }

    public enum Status {
        OPEN, CLAIMED, CLOSED
    }

    // ── JPA lifecycle ─────────────────────────────────────────────────

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ── Constructors ──────────────────────────────────────────────────

    public Item() {
    }

    // ── Getters ───────────────────────────────────────────────────────

    public Long getId()                  { return id; }
    public String getTitle()             { return title; }
    public String getDescription()       { return description; }
    public String getLocation()          { return location; }
    public Category getCategory()        { return category; }
    public ItemType getType()            { return type; }
    public Status getStatus()            { return status; }
    public LocalDate getDateOccurred()   { return dateOccurred; }
    public String getImageUrl()          { return imageUrl; }
    public User getReportedBy()          { return reportedBy; }
    public LocalDateTime getCreatedAt()  { return createdAt; }
    public LocalDateTime getUpdatedAt()  { return updatedAt; }
    public List<Claim> getClaims()       { return claims; }

    // ── Setters ───────────────────────────────────────────────────────

    public void setId(Long id)                        { this.id = id; }
    public void setTitle(String title)                { this.title = title; }
    public void setDescription(String description)    { this.description = description; }
    public void setLocation(String location)          { this.location = location; }
    public void setCategory(Category category)        { this.category = category; }
    public void setType(ItemType type)                { this.type = type; }
    public void setStatus(Status status)              { this.status = status; }
    public void setDateOccurred(LocalDate date)       { this.dateOccurred = date; }
    public void setImageUrl(String imageUrl)          { this.imageUrl = imageUrl; }
    public void setReportedBy(User reportedBy)        { this.reportedBy = reportedBy; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setClaims(List<Claim> claims)         { this.claims = claims; }

    // ── Builder ───────────────────────────────────────────────────────

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String title;
        private String description;
        private String location;
        private Category category;
        private ItemType type = ItemType.LOST;
        private Status status = Status.OPEN;
        private LocalDate dateOccurred;
        private String imageUrl;
        private User reportedBy;
        private LocalDateTime createdAt = LocalDateTime.now();
        private List<Claim> claims = new ArrayList<>();

        public Builder id(Long id)                      { this.id = id; return this; }
        public Builder title(String title)              { this.title = title; return this; }
        public Builder description(String desc)         { this.description = desc; return this; }
        public Builder location(String location)        { this.location = location; return this; }
        public Builder category(Category category)      { this.category = category; return this; }
        public Builder type(ItemType type)              { this.type = type; return this; }
        public Builder status(Status status)            { this.status = status; return this; }
        public Builder dateOccurred(LocalDate date)     { this.dateOccurred = date; return this; }
        public Builder imageUrl(String imageUrl)        { this.imageUrl = imageUrl; return this; }
        public Builder reportedBy(User user)            { this.reportedBy = user; return this; }
        public Builder createdAt(LocalDateTime dt)      { this.createdAt = dt; return this; }
        public Builder claims(List<Claim> claims)       { this.claims = claims; return this; }

        public Item build() {
            Item i = new Item();
            i.id           = this.id;
            i.title        = this.title;
            i.description  = this.description;
            i.location     = this.location;
            i.category     = this.category;
            i.type         = this.type;
            i.status       = this.status;
            i.dateOccurred = this.dateOccurred;
            i.imageUrl     = this.imageUrl;
            i.reportedBy   = this.reportedBy;
            i.createdAt    = this.createdAt;
            i.claims       = this.claims;
            return i;
        }
    }

    @Override
    public String toString() {
        return "Item{id=" + id + ", title='" + title + "', type=" + type + ", status=" + status + "}";
    }
}
