package org.skillbox.socnet.service.shedule;

import org.apache.log4j.Logger;
import org.skillbox.socnet.model.entity.User;
import org.skillbox.socnet.model.entity.enums.NotificationType;
import org.skillbox.socnet.service.NotificationApi;
import org.skillbox.socnet.service.UserService;
import org.skillbox.socnet.service.friends.FriendsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@ConditionalOnProperty(name = "scheduler.enable", havingValue = "true", matchIfMissing = true)
public class AppScheduler {

    private final NotificationApi notificationApi;
    private final UserService userService;
    private final FriendsService friendsService;

    private final Logger log = Logger.getLogger(AppScheduler.class.getName());

    public AppScheduler(NotificationApi notificationApi, UserService userService, FriendsService friendsService) {
        this.notificationApi = notificationApi;
        this.userService = userService;
        this.friendsService = friendsService;
    }

    @Scheduled(fixedRateString = "${scheduler.fixedRate}")
    public void sentMailNotification() {
        notificationApi.sendNotificationEmail();
        log.info("Done scheduler for sent main notification");
    }

    @Scheduled(cron = "${scheduler.cronBirthDay}")
    public void checkBirthDay() {
        List<User> userByBirthDay = userService.getUserByBirthDay(LocalDateTime.now());

        if (userByBirthDay.isEmpty()) {
            return;
        }


        userByBirthDay.forEach(user -> {
                    List<User> allUserFriends = friendsService.getAllMyFriends(user);
                    allUserFriends.forEach(friend -> {
                        notificationApi.createNotification(NotificationType.FRIEND_BIRTHDAY, friend, user.getId());
                    });
                }
        );
    }
}
