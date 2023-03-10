package com.sparta.sogonsogon.notification.repository;

import com.sparta.sogonsogon.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
