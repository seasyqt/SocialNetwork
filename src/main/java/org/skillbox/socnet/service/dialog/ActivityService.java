package org.skillbox.socnet.service.dialog;

import org.apache.log4j.Logger;
import org.skillbox.socnet.api.dto.DTOError;
import org.skillbox.socnet.api.dto.DTOErrorDescription;
import org.skillbox.socnet.api.dto.DTOMessage;
import org.skillbox.socnet.api.dto.DTOSuccessfully;
import org.skillbox.socnet.api.dto.dialog.DTOUserOnline;
import org.skillbox.socnet.api.response.error.ErrorResponse;
import org.skillbox.socnet.model.entity.User;
import org.skillbox.socnet.model.entity.UserPrintStatus;
import org.skillbox.socnet.model.entity.enums.UserStatus;
import org.skillbox.socnet.model.repository.DialogRepository;
import org.skillbox.socnet.model.repository.UserPrintStatusRepository;
import org.skillbox.socnet.model.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
public class ActivityService {

    private final UserPrintStatusRepository userPrintStatusRepository;
    private final DialogRepository dialogRepository;
    private final UserRepository userRepository;
    private final Logger log = Logger.getLogger(ActivityService.class.getName());

    public ActivityService(UserPrintStatusRepository userPrintStatusRepository, DialogRepository dialogRepository, UserRepository userRepository) {

        this.userPrintStatusRepository = userPrintStatusRepository;
        this.dialogRepository = dialogRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> getPrintingStatus(Integer id, Integer userId) {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            log.error(DTOErrorDescription.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(new ErrorResponse(
                    DTOError.UNAUTHORIZED.get(),
                    DTOErrorDescription.UNAUTHORIZED.get()));
        }

        if (dialogRepository.findDialogById(id).isEmpty() || userRepository.findUserById(userId).isEmpty()) {
            log.error(DTOErrorDescription.BAD_REQUEST);
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        }

        UserPrintStatus printStatus;

        Optional<UserPrintStatus> optional = userPrintStatusRepository.findByDialogIdAndUserId(id, userId);
        printStatus = optional.orElseGet(() -> newPrintStatus(id, userId));

        if (!printStatus.getStatus().equals(UserStatus.PRINTS)) {
            printStatus.setStatus(UserStatus.PRINTS);
        }
        printStatus.setTime(LocalDateTime.now());

        userPrintStatusRepository.save(printStatus);


        return ResponseEntity.ok(new DTOSuccessfully(
                "string",
                new Date().getTime() / 1000,
                new DTOMessage()));

    }

    private UserPrintStatus newPrintStatus(Integer id, Integer userId) {

        UserPrintStatus printStatus = new UserPrintStatus();
        printStatus.setDialogId(id);
        printStatus.setUserId(userId);
        printStatus.setStatus(UserStatus.PRINTS);

        return printStatus;
    }

    public ResponseEntity<?> getOnlineStatus(Integer id, Integer userId) {

        long timestamp = LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).toEpochSecond();
        User user;
        DTOUserOnline userOnline;

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            log.error(DTOErrorDescription.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(new ErrorResponse(
                    DTOError.UNAUTHORIZED.get(),
                    DTOErrorDescription.UNAUTHORIZED.get()));
        }

        if (dialogRepository.findDialogById(id).isEmpty() || userRepository.findUserById(userId).isEmpty()) {
            log.error(DTOErrorDescription.BAD_REQUEST);
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        } else user = userRepository.findUserById(userId).get();

        byte isOnline = user.getIsOnline();

        if (isOnline == 1) {
            userOnline = new DTOUserOnline(true, timestamp);
        } else
            userOnline = new DTOUserOnline(false, user.getLastOnlineTime().atZone(ZoneId.of("Europe/Moscow")).toEpochSecond());

        return ResponseEntity.ok(new DTOSuccessfully("string", timestamp, userOnline));
    }
}
