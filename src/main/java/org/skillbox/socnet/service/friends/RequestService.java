package org.skillbox.socnet.service.friends;

import org.apache.log4j.Logger;
import org.skillbox.socnet.api.dto.DTOError;
import org.skillbox.socnet.api.dto.DTOErrorDescription;
import org.skillbox.socnet.api.dto.DTOMessage;
import org.skillbox.socnet.api.dto.DTOSuccessfully;
import org.skillbox.socnet.api.response.error.ErrorResponse;
import org.skillbox.socnet.model.entity.Friendship;
import org.skillbox.socnet.model.entity.Notification;
import org.skillbox.socnet.model.entity.User;
import org.skillbox.socnet.model.entity.enums.FriendshipStatus;
import org.skillbox.socnet.model.entity.enums.NotificationType;
import org.skillbox.socnet.model.repository.FriendshipRepository;
import org.skillbox.socnet.model.repository.NotificationRepository;
import org.skillbox.socnet.model.repository.UserRepository;
import org.skillbox.socnet.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
public class RequestService {
    private final NotificationRepository notificationRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final Logger log = Logger.getLogger(RequestService.class.getName());

    public RequestService(NotificationRepository notificationRepository, FriendshipRepository friendshipRepository,
                          UserRepository userRepository, UserService userService) {
        this.notificationRepository = notificationRepository;
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public ResponseEntity createResponse(Integer dstUserId) {
        User currentUser;
        User dstUser;

        //Проверка авторизирвоан ли пользователь
        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOErrorDescription.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(new ErrorResponse(
                    DTOError.UNAUTHORIZED.get(),
                    DTOErrorDescription.UNAUTHORIZED.get()));
        }

        Optional<User> friend = userRepository.findById(dstUserId);

        //Получен ли пользователь
        if (friend.isEmpty()) {
            log.error(DTOErrorDescription.BAD_CREDENTIALS.get());
            return ResponseEntity.status(400).body(new ErrorResponse(
                    DTOError.INVALID_REQUEST.get(),
                    DTOErrorDescription.BAD_CREDENTIALS.get()));
        }

        dstUser = friend.get();

        //Не является ли добавляемый user текущим
        if (dstUser == currentUser) {
            log.error("cannot add yourself");
            return ResponseEntity.status(400).body(new ErrorResponse(
                    DTOError.INVALID_REQUEST.get(),
                    DTOErrorDescription.BAD_CREDENTIALS.get()));
        }

        createFriendShip(currentUser, dstUser);

        createNotification(currentUser, dstUser);

        log.info("Successfully");

        return ResponseEntity.ok(new DTOSuccessfully(
                null,
                new Date().getTime() / 1000,
                new DTOMessage()));

    }

    private void createFriendShip(User current, User dst) {
        Friendship friendship = new Friendship();
        friendship.setStatus(FriendshipStatus.REQUEST);
        friendship.setTime(LocalDateTime.now());
        friendship.setDstUser(dst);
        friendship.setSrcUser(current);

        friendshipRepository.save(friendship);
    }

    //Todo сменить в будущем на NotificationApi
    private void createNotification(User current, User dst) {
        Notification notification;
        int friendshipId;

        Optional<Friendship> optionalFriendship = friendshipRepository.findFriendshipForUser(current, dst);
        if (optionalFriendship.isEmpty()) {
            notification = new Notification();
            friendshipId = optionalFriendship.get().getId();

            notification.setType(NotificationType.FRIEND_REQUEST);
            notification.setUser(dst);
            notification.setEntityId(friendshipId);
            notification.setSentTime(LocalDateTime.now());
            //Todo send Email and sms
            notification.setEmail(dst.getEmail());
            notification.setPhone(dst.getPhone());

            notificationRepository.save(notification);
        } else log.error("request not found");
    }
}
