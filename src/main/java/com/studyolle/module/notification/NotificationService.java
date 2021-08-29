package com.studyolle.module.notification;

import com.studyolle.module.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<Notification> findByAccountAndCheckedOrderByCreatedDateTimeDesc(Account account, boolean checked) {
        return notificationRepository.findByAccountAndCheckedOrderByCreatedDateTimeDesc(account, checked);
    }

    public long countByAccountAndChecked(Account account, boolean checked) {
        return notificationRepository.countByAccountAndChecked(account, checked);
    }



    public void markAsRead(List<Notification> notifications) {
        notifications.forEach(n -> n.setChecked(true));
        notificationRepository.saveAll(notifications);
    }

    public void putCategorizedNotifications(Model model, List<Notification> notifications, long numberOfChecked, long numberOfNotChecked) {
        List<Notification> newStudyNotifications = new ArrayList<>();
        List<Notification> watchingStudyNotification = new ArrayList<>();
        List<Notification> eventEnrollmentNotifications = new ArrayList<>();

        for (Notification notification : notifications) {
            switch (notification.getNotificationType()) {
                case STUDY_CREATED: newStudyNotifications.add(notification); break;
                case STUDY_UPDATED: watchingStudyNotification.add(notification); break;
                case EVENT_ENROLLMENT: eventEnrollmentNotifications.add(notification); break;
            }
        }

        model.addAttribute("notifications", notifications);
        model.addAttribute("numberOfChecked", numberOfChecked);
        model.addAttribute("numberOfNotChecked", numberOfNotChecked);
        model.addAttribute("newStudyNotifications", newStudyNotifications);
        model.addAttribute("watchingStudyNotifications", watchingStudyNotification);
        model.addAttribute("eventEnrollmentNotifications", eventEnrollmentNotifications);
    }

    public void deleteByAccountAndChecked(Account account, boolean checked) {
        notificationRepository.deleteByAccountAndChecked(account, checked);
    }
}
