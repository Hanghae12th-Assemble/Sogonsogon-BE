package com.sparta.sogonsogon.noti.repository;

import com.sparta.sogonsogon.noti.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
//    List<Notification> findByMemberId(Long MemberId);
}
