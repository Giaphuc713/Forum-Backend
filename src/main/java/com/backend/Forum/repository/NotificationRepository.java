package com.backend.Forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.Forum.entity.Notification;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId);
}
