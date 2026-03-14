package com.example.unitedservice.controllers;


import com.example.unitedservice.dto.NotificationRequest;
import com.example.unitedservice.dto.SubscriptionRequest;
import com.example.unitedservice.services.PushNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/push-notifications")
@CrossOrigin(origins = "*")
public class PushNotificationController {

    @Autowired
    private PushNotificationService notificationService;

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody SubscriptionRequest request) {
        try {
            notificationService.saveSubscription(request);
            return ResponseEntity.ok().body(Map.of("message", "Subscription saved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribe(@RequestBody SubscriptionRequest request) {
        try {
            notificationService.removeSubscription(request);
            return ResponseEntity.ok().body(Map.of("message", "Unsubscribed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/test")
    public ResponseEntity<?> sendTestNotification() {
        try {
            notificationService.sendTestNotification();
            return ResponseEntity.ok().body(Map.of("message", "Test notification sent"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@RequestBody NotificationRequest request) {
        try {
            if (request.getUserId() != null) {
                notificationService.sendNotificationToUser(request.getUserId(), request);
            } else {
                notificationService.sendNotificationToAll(request);
            }
            return ResponseEntity.ok().body(Map.of("message", "Notification sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Specific notification endpoints for your HR system
    @PostMapping("/leave-request/{userId}")
    public ResponseEntity<?> notifyLeaveRequest(@PathVariable Long userId,
                                                @RequestParam String employeeName,
                                                @RequestParam String status) {
        try {
            notificationService.notifyLeaveRequestStatus(userId, employeeName, status);
            return ResponseEntity.ok().body(Map.of("message", "Leave notification sent"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/mission/{userId}")
    public ResponseEntity<?> notifyNewMission(@PathVariable Long userId,
                                              @RequestParam String missionDetails) {
        try {
            notificationService.notifyNewMission(userId, missionDetails);
            return ResponseEntity.ok().body(Map.of("message", "Mission notification sent"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/advance/{userId}")
    public ResponseEntity<?> notifyAdvanceRequest(@PathVariable Long userId,
                                                  @RequestParam String amount,
                                                  @RequestParam String status) {
        try {
            notificationService.notifyAdvanceRequest(userId, amount, status);
            return ResponseEntity.ok().body(Map.of("message", "Advance notification sent"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}