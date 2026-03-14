package com.example.unitedservice.dto;

public class SubscriptionRequest {

    private Subscription subscription;
    private String userAgent;
    private Long userId;

    public static class Subscription {
        private String endpoint;
        private Keys keys;

        // Getters and Setters
        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

        public Keys getKeys() { return keys; }
        public void setKeys(Keys keys) { this.keys = keys; }
    }

    public static class Keys {
        private String p256dh;
        private String auth;

        // Getters and Setters
        public String getP256dh() { return p256dh; }
        public void setP256dh(String p256dh) { this.p256dh = p256dh; }

        public String getAuth() { return auth; }
        public void setAuth(String auth) { this.auth = auth; }
    }

    // Getters and Setters
    public Subscription getSubscription() { return subscription; }
    public void setSubscription(Subscription subscription) { this.subscription = subscription; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}