package org.skillbox.socnet.service.account;

import com.github.cage.GCage;
import org.apache.log4j.Logger;
import org.skillbox.socnet.api.dto.DTOError;
import org.skillbox.socnet.api.dto.DTOErrorDescription;
import org.skillbox.socnet.api.dto.DTOMessage;
import org.skillbox.socnet.api.dto.DTOSuccessfully;
import org.skillbox.socnet.api.response.error.ErrorResponse;
import org.skillbox.socnet.mailSender.MailSender;
import org.skillbox.socnet.model.entity.TokenToUser;
import org.skillbox.socnet.model.entity.User;
import org.skillbox.socnet.model.repository.TokenToUserRepository;
import org.skillbox.socnet.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;


@Service
public class RecoveryService {

    private final UserRepository userRepository;
    private final TokenToUserRepository tokenToUserRepository;
    private final Logger log = Logger.getLogger(RecoveryService.class.getName());

    @Autowired
    public RecoveryService(UserRepository userRepository, TokenToUserRepository tokenToUserRepository) {
        this.userRepository = userRepository;
        this.tokenToUserRepository = tokenToUserRepository;
    }

    public ResponseEntity<?> createResponse(String email, String address) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            log.error(DTOErrorDescription.BAD_CREDENTIALS.get());
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    DTOError.INVALID_REQUEST.get(),
                    DTOErrorDescription.BAD_CREDENTIALS.get()));
        }

        String token = new GCage().getTokenGenerator().next();

        TokenToUser tokenToUser = new TokenToUser();
        tokenToUser.setToken(token);
        tokenToUser.setUserId(user.get().getId());

        tokenToUserRepository.save(tokenToUser);
        MailSender.sendMessage(email, "Recovery link", address + "/change-password?token=" + token);

        log.info("Recovery link was send");

        return ResponseEntity.ok(new DTOSuccessfully(
                null,
                new Date().getTime() / 1000,
                new DTOMessage()));
    }
}
