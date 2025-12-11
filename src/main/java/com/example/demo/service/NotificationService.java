package com.example.demo.service;

import com.example.demo.model.Notification;
import com.example.demo.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepo;

    public NotificationService(NotificationRepository notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    // --------------- CREATE NOTIFICATION ---------------
    public void notifyUser(String userId,
                           String fromUserId,
                           String type,
                           String message,
                           String referenceId) {

        Notification n = new Notification();
        n.setUserId(userId);
        n.setFromUserId(fromUserId);
        n.setType(type);
        n.setMessage(message);
        n.setReferenceId(referenceId);
        n.setCreatedAt(System.currentTimeMillis());
        n.setRead(false);

        notificationRepo.save(n);
    }

    // --------------- GET NOTIFICATIONS FOR CURRENT USER ---------------
    public List<Notification> getForUser(String userId) {
        return notificationRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // --------------- MARK ALL AS READ ----------------
    public void markAllRead(String userId) {
        List<Notification> list = notificationRepo.findByUserIdOrderByCreatedAtDesc(userId);
        for (Notification n : list) {
            if (!n.isRead()) {
                n.setRead(true);
            }
        }
        notificationRepo.saveAll(list);
    }
}
