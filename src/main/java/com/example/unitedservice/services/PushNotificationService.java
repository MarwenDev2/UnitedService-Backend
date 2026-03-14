package com.example.unitedservice.services;

import com.example.unitedservice.dto.NotificationRequest;
import com.example.unitedservice.dto.SubscriptionRequest;
import com.example.unitedservice.entities.PushSubscription;
import com.example.unitedservice.repositories.PushSubscriptionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class PushNotificationService {

    @Autowired
    private PushService pushService;

    @Autowired
    private PushSubscriptionRepository subscriptionRepository;

    public void saveSubscription(SubscriptionRequest request) {
        // Check if subscription already exists
        List<PushSubscription> existing = subscriptionRepository.findByEndpoint(
                request.getSubscription().getEndpoint()
        );

        if (!existing.isEmpty()) {
            // Update existing subscription
            PushSubscription sub = existing.get(0);
            sub.setPublicKey(request.getSubscription().getKeys().getP256dh());
            sub.setAuthToken(request.getSubscription().getKeys().getAuth());
            sub.setUserAgent(request.getUserAgent());
            if (request.getUserId() != null) {
                sub.setUserId(request.getUserId());
            }
            subscriptionRepository.save(sub);
        } else {
            // Create new subscription
            PushSubscription subscription = new PushSubscription();
            subscription.setEndpoint(request.getSubscription().getEndpoint());
            subscription.setPublicKey(request.getSubscription().getKeys().getP256dh());
            subscription.setAuthToken(request.getSubscription().getKeys().getAuth());
            subscription.setUserAgent(request.getUserAgent());
            if (request.getUserId() != null) {
                subscription.setUserId(request.getUserId());
            }
            subscriptionRepository.save(subscription);
        }
    }

    public void removeSubscription(SubscriptionRequest request) {
        subscriptionRepository.deleteByEndpoint(request.getSubscription().getEndpoint());
    }

    @Async
    public CompletableFuture<Void> sendNotificationToUser(Long userId, NotificationRequest notificationRequest) {
        List<PushSubscription> subscriptions = subscriptionRepository.findByUserId(userId);
        return sendNotificationToSubscriptions(subscriptions, notificationRequest);
    }

    @Async
    public CompletableFuture<Void> sendNotificationToAll(NotificationRequest notificationRequest) {
        List<PushSubscription> allSubscriptions = subscriptionRepository.findAll();
        return sendNotificationToSubscriptions(allSubscriptions, notificationRequest);
    }

    @Async
    public CompletableFuture<Void> sendTestNotification() {
        NotificationRequest testNotification = new NotificationRequest();
        testNotification.setTitle("United RH - Test");
        testNotification.setBody("This is a test notification from United RH system");
        testNotification.setIcon("/assets/icons/icon-192x192.png");
        testNotification.setType("system");

        Map<String, Object> data = new HashMap<>();
        data.put("url", "/dashboard");
        data.put("type", "test");
        testNotification.setData(data);

        return sendNotificationToAll(testNotification);
    }

    // Specific notification methods for your HR system
    @Async
    public CompletableFuture<Void> notifyLeaveRequestStatus(Long userId, String employeeName, String status) {
        NotificationRequest notification = new NotificationRequest();
        notification.setTitle("Leave Request Update");
        notification.setBody("Leave request for " + employeeName + " has been " + status);
        notification.setIcon("/assets/icons/icon-192x192.png");
        notification.setType("leave_request");
        notification.setUserId(userId);

        Map<String, Object> data = new HashMap<>();
        data.put("url", "/leave-requests");
        data.put("type", "leave_request");
        data.put("status", status);
        notification.setData(data);

        return sendNotificationToUser(userId, notification);
    }

    @Async
    public CompletableFuture<Void> notifyNewMission(Long userId, String missionDetails) {
        NotificationRequest notification = new NotificationRequest();
        notification.setTitle("New Mission Assignment");
        notification.setBody("You have been assigned a new mission: " + missionDetails);
        notification.setIcon("/assets/icons/icon-192x192.png");
        notification.setType("mission");
        notification.setUserId(userId);

        Map<String, Object> data = new HashMap<>();
        data.put("url", "/missions");
        data.put("type", "mission");
        notification.setData(data);

        return sendNotificationToUser(userId, notification);
    }

    @Async
    public CompletableFuture<Void> notifyAdvanceRequest(Long userId, String amount, String status) {
        NotificationRequest notification = new NotificationRequest();
        notification.setTitle("Advance Request " + status);
        notification.setBody("Your advance request of " + amount + " has been " + status);
        notification.setIcon("/assets/icons/icon-192x192.png");
        notification.setType("advance");
        notification.setUserId(userId);

        Map<String, Object> data = new HashMap<>();
        data.put("url", "/advance-requests");
        data.put("type", "advance");
        data.put("status", status);
        notification.setData(data);

        return sendNotificationToUser(userId, notification);
    }

    private CompletableFuture<Void> sendNotificationToSubscriptions(List<PushSubscription> subscriptions, NotificationRequest notificationRequest) {
        return CompletableFuture.runAsync(() -> {
            for (PushSubscription subscription : subscriptions) {
                try {
                    sendPushNotification(subscription, notificationRequest);
                } catch (Exception e) {
                    System.err.println("Failed to send notification to: " + subscription.getEndpoint());
                    // Remove invalid subscriptions
                    if (e.getMessage().contains("410") || e.getMessage().contains("404")) {
                        subscriptionRepository.delete(subscription);
                    }
                }
            }
        });
    }

    private void sendPushNotification(PushSubscription subscription, NotificationRequest notificationRequest) {
        try {
            System.out.println("🚀 === SENDING PUSH NOTIFICATION ===");
            System.out.println("📱 To user ID: " + subscription.getUserId());
            System.out.println("🔗 Endpoint: " + subscription.getEndpoint());
            System.out.println("📝 Title: " + notificationRequest.getTitle());
            System.out.println("📄 Body: " + notificationRequest.getBody());

            // Create a proper JSON object with all required fields
            Map<String, Object> payload = new HashMap<>();
            payload.put("title", notificationRequest.getTitle());
            payload.put("body", notificationRequest.getBody());
            payload.put("icon", notificationRequest.getIcon() != null ?
                    notificationRequest.getIcon() : "/uploads/photos/logo.ico");
            payload.put("image", notificationRequest.getImage());
            payload.put("tag", notificationRequest.getTag() != null ?
                    notificationRequest.getTag() : "general");
            payload.put("data", notificationRequest.getData() != null ?
                    notificationRequest.getData() : new HashMap<>());

            // Convert to JSON - use Jackson if available
            ObjectMapper mapper = new ObjectMapper();
            String payloadJson = mapper.writeValueAsString(payload);

            System.out.println("📦 Payload JSON: " + payloadJson);

            // Create web-push notification
            Notification notification = new Notification(
                    subscription.getEndpoint(),
                    subscription.getPublicKey(),
                    subscription.getAuthToken(),
                    payloadJson
            );

            // Send notification
            pushService.send(notification);

            // Update last used timestamp
            subscription.setLastUsed(java.time.LocalDateTime.now());
            subscriptionRepository.save(subscription);

            System.out.println("✅ Notification sent to: " + subscription.getEndpoint());

        } catch (Exception e) {
            System.err.println("❌ FAILED to send push notification!");
            System.err.println("Endpoint: " + subscription.getEndpoint());
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send push notification", e);
        }
    }

    private String convertToJson(Map<String, Object> payload) {
        // Simple JSON conversion (you can use Jackson/Gson for more complex objects)
        StringBuilder json = new StringBuilder("{");
        boolean first = true;

        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(entry.getKey()).append("\":");

            if (entry.getValue() instanceof String) {
                json.append("\"").append(escapeJson(entry.getValue().toString())).append("\"");
            } else if (entry.getValue() instanceof Map) {
                json.append(convertToJson((Map<String, Object>) entry.getValue()));
            } else {
                json.append(entry.getValue());
            }

            first = false;
        }

        json.append("}");
        return json.toString();
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}