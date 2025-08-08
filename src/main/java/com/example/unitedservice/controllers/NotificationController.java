package com.example.unitedservice.controllers;

import com.example.unitedservice.dto.NotificationDTO;
import com.example.unitedservice.entities.Notification;
import com.example.unitedservice.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
class NotificationController {
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;

    @GetMapping
    public List<NotificationDTO> getAll() {
        return notificationService.getAllNotifications().stream()
                .map(notification -> modelMapper.map(notification, NotificationDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/recipient/{id}")
    public List<NotificationDTO> getByRecipient(@PathVariable Long id) {
        return notificationService.getNotificationsByRecipientId(id).stream()
                .map(notification -> modelMapper.map(notification, NotificationDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> getById(@PathVariable int id) {
        return notificationService.getNotificationById(id)
                .map(notification -> modelMapper.map(notification, NotificationDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public NotificationDTO create(@RequestBody NotificationDTO notificationDTO) {
        Notification notification = modelMapper.map(notificationDTO, Notification.class);
        Notification savedNotification = notificationService.saveNotification(notification);
        return modelMapper.map(savedNotification, NotificationDTO.class);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationDTO> update(@PathVariable int id, @RequestBody NotificationDTO notificationDTO) {
        return notificationService.getNotificationById(id)
                .map(existing -> {
                    Notification updatedNotification = modelMapper.map(notificationDTO, Notification.class);
                    updatedNotification.setId(id);
                    return ResponseEntity.ok(modelMapper.map(notificationService.saveNotification(updatedNotification), NotificationDTO.class));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/mark-read/{id}")
    public ResponseEntity<NotificationDTO> markRead(@PathVariable int id) {
        Notification updatedNotification = notificationService.markAsRead(id);
        return ResponseEntity.ok(modelMapper.map(updatedNotification, NotificationDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (notificationService.getNotificationById(id).isPresent()) {
            notificationService.deleteNotification(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/count/unread")
    public int countUnreadDashboard() {
        return notificationService.countUnreadForDashboard();
    }

    @GetMapping("/count/unread/{recipientId}")
    public int countUnreadByUser(@PathVariable Long recipientId) {
        return notificationService.countUnreadByUserId(recipientId);
    }

    @GetMapping("/dashboard")
    public List<NotificationDTO> dashboardList() {
        return notificationService.getAllForDashboard().stream()
                .map(notification -> modelMapper.map(notification, NotificationDTO.class))
                .collect(Collectors.toList());
    }
}