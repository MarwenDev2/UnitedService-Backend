package com.example.unitedservice.repositories;

import com.example.unitedservice.entities.Notification;
import com.example.unitedservice.entities.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByRecipient(Worker worker);
    List<Notification> findByRecipientAndReadFalse(Worker worker);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipient.id = :recipientId AND n.read = false")
    int countUnreadByRecipientId(@Param("recipientId") Long recipientId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.read = false")
    int countUnreadForDashboard();

    @Query("SELECT n FROM Notification n ORDER BY n.timestamp DESC")
    List<Notification> findAllOrderByTimestampDesc();
}
