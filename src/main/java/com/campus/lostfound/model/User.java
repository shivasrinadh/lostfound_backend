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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "reportedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Item> reportedItems = new ArrayList<>();

    @OneToMany(mappedBy = "claimedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Claim> claims = new ArrayList<>();

    // ── Enum ──────────────────────────────────────────────────────────

    public enum Role {
        USER, ADMIN
    }

    // ── Constructors ──────────────────────────────────────────────────

    public User() {
    }

    public User(Long id, String username, String email, String password,
                String phone, Role role, LocalDateTime createdAt,
                List<Item> reportedItems, List<Claim> claims) {
        this.id           = id;
        this.username     = username;
        this.email        = email;
        this.password     = password;
        this.phone        = phone;
        this.role         = role;
        this.createdAt    = createdAt;
        this.reportedItems = reportedItems != null ? reportedItems : new ArrayList<>();
        this.claims       = claims != null ? claims : new ArrayList<>();
    }

    // ── Getters ───────────────────────────────────────────────────────

    public Long getId()                    { return id; }
    public String getUsername()            { return username; }
    public String getEmail()               { return email; }
    public String getPassword()            { return password; }
    public String getPhone()               { return phone; }
    public Role getRole()                  { return role; }
    public LocalDateTime getCreatedAt()    { return createdAt; }
    public List<Item> getReportedItems()   { return reportedItems; }
    public List<Claim> getClaims()         { return claims; }

    // ── Setters ───────────────────────────────────────────────────────

    public void setId(Long id)                          { this.id = id; }
    public void setUsername(String username)            { this.username = username; }
    public void setEmail(String email)                  { this.email = email; }
    public void setPassword(String password)            { this.password = password; }
    public void setPhone(String phone)                  { this.phone = phone; }
    public void setRole(Role role)                      { this.role = role; }
    public void setCreatedAt(LocalDateTime createdAt)   { this.createdAt = createdAt; }
    public void setReportedItems(List<Item> items)      { this.reportedItems = items; }
    public void setClaims(List<Claim> claims)           { this.claims = claims; }

    // ── Builder ───────────────────────────────────────────────────────

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String username;
        private String email;
        private String password;
        private String phone;
        private Role role = Role.USER;
        private LocalDateTime createdAt = LocalDateTime.now();
        private List<Item> reportedItems = new ArrayList<>();
        private List<Claim> claims = new ArrayList<>();

        public Builder id(Long id)                       { this.id = id; return this; }
        public Builder username(String username)         { this.username = username; return this; }
        public Builder email(String email)               { this.email = email; return this; }
        public Builder password(String password)         { this.password = password; return this; }
        public Builder phone(String phone)               { this.phone = phone; return this; }
        public Builder role(Role role)                   { this.role = role; return this; }
        public Builder createdAt(LocalDateTime dt)       { this.createdAt = dt; return this; }
        public Builder reportedItems(List<Item> items)   { this.reportedItems = items; return this; }
        public Builder claims(List<Claim> claims)        { this.claims = claims; return this; }

        public User build() {
            User u = new User();
            u.id            = this.id;
            u.username      = this.username;
            u.email         = this.email;
            u.password      = this.password;
            u.phone         = this.phone;
            u.role          = this.role;
            u.createdAt     = this.createdAt;
            u.reportedItems = this.reportedItems;
            u.claims        = this.claims;
            return u;
        }
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', role=" + role + "}";
    }
}
