package com.sparta.sogonsogon.noti.repository;

import com.sparta.sogonsogon.noti.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {
//    List<Notification> findByMemberId(Long MemberId);
}
