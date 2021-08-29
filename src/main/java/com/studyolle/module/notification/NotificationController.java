package com.studyolle.module.notification;

import com.studyolle.module.account.Account;
import com.studyolle.module.account.CurrentAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;


    @GetMapping("/notifications")
    public String getNotifications(@CurrentAccount Account account, Model model) {
        List<Notification> notifications = notificationService.findByAccountAndCheckedOrderByCreatedDateTimeDesc(account, false);
        long numberOfChecked = notificationService.countByAccountAndChecked(account, true);

        notificationService.putCategorizedNotifications(model, notifications, numberOfChecked, notifications.size());

        model.addAttribute("isNew", true);
        notificationService.markAsRead(notifications);

        return "notification/list";
    }

    @GetMapping("/notifications/old")
    public String getOldNotifications(@CurrentAccount Account account, Model model) {
        List<Notification> notifications = notificationService.findByAccountAndCheckedOrderByCreatedDateTimeDesc(account, true);
        long numberOfNotChecked = notificationService.countByAccountAndChecked(account, false);

        notificationService.putCategorizedNotifications(model, notifications, notifications.size(), numberOfNotChecked);

        model.addAttribute("isNew", false);

        return "notification/list";
    }

    @DeleteMapping("/notifications")
    public String deleteNotifications(@CurrentAccount Account account) {
        notificationService.deleteByAccountAndChecked(account, true);
        return "redirect:/notifications";
    }

}
