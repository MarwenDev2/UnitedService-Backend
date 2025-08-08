package com.example.unitedservice.services;

import com.example.unitedservice.entities.*;
import com.example.unitedservice.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final WorkerRepository workerRepository;

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public List<Notification> getByRecipient(Worker worker) {
        return notificationRepository.findByRecipient(worker);
    }

    public List<Notification> getUnreadByRecipient(Worker worker) {
        return notificationRepository.findByRecipientAndReadFalse(worker);
    }

    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public Optional<Notification> getNotificationById(int id) {
        return notificationRepository.findById(id);
    }

    public void deleteNotification(int id) {
        notificationRepository.deleteById(id);
    }

    // ✅ This method was missing
    public List<Notification> getNotificationsByRecipientId(Long recipientId) {
        Optional<Worker> workerOpt = workerRepository.findById(recipientId);
        return workerOpt.map(notificationRepository::findByRecipient).orElse(List.of());
    }

    // ✅ Also missing
    public Notification markAsRead(int id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    public int countUnreadForDashboard() {
        return notificationRepository.countUnreadForDashboard();
    }

    public int countUnreadByUserId(Long userId) {
        return notificationRepository.countUnreadByRecipientId(userId);
    }

    public List<Notification> getAllForDashboard() {
        return notificationRepository.findAllOrderByTimestampDesc();
    }

}