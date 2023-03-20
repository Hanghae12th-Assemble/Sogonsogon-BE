package com.sparta.sogonsogon.noti.repository;

import com.sparta.sogonsogon.noti.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

}
