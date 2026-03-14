package com.example.unitedservice.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "push_subscriptions")
public class PushSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String endpoint;

    @Column(name = "public_key")
    private String publicKey;

    @Column(name = "auth_token")
    private String authToken;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "user_id")
    private Long userId; // Link to your User entity

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_used")
    private LocalDateTime lastUsed;

    @Column(name = "is_active")
    private boolean active = true;

    // Constructors
    public PushSubscription() {
        this.createdAt = LocalDateTime.now();
        this.lastUsed = LocalDateTime.now();
    }

    public PushSubscription(Long id, String endpoint, String publicKey, String authToken, String userAgent, Long userId, LocalDateTime createdAt, LocalDateTime lastUsed, boolean active) {
        this.id = id;
        this.endpoint = endpoint;
        this.publicKey = publicKey;
        this.authToken = authToken;
        this.userAgent = userAgent;
        this.userId = userId;
        this.createdAt = createdAt;
        this.lastUsed = lastUsed;
        this.active = active;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

    public String getAuthToken() { return authToken; }
    public void setAuthToken(String authToken) { this.authToken = authToken; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastUsed() { return lastUsed; }
    public void setLastUsed(LocalDateTime lastUsed) { this.lastUsed = lastUsed; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}