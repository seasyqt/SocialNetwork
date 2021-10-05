package org.skillbox.socnet.service.account;

import org.apache.log4j.Logger;
import org.skillbox.socnet.api.dto.DTOError;
import org.skillbox.socnet.api.dto.DTOErrorDescription;
import org.skillbox.socnet.api.dto.DTOMessage;
import org.skillbox.socnet.api.dto.DTOSuccessfully;
import org.skillbox.socnet.api.response.error.ErrorResponse;
import org.skillbox.socnet.model.entity.TokenToUser;
import org.skillbox.socnet.model.entity.User;
import org.skillbox.socnet.model.repository.TokenToUserRepository;
import org.skillbox.socnet.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RegisterConfirmService {
    private final UserRepository userRepository;
    private final TokenToUserRepository tokenToUserRepository;
    private final Logger log = Logger.getLogger(RegisterConfirmService.class.getName());

    @Autowired
    public RegisterConfirmService(UserRepository userRepository, TokenToUserRepository tokenToUserRepository) {
        this.userRepository = userRepository;
        this.tokenToUserRepository = tokenToUserRepository;
    }

    public ResponseEntity<?> createResponse(Integer userId, String token) {
        List<TokenToUser> expired = tokenToUserRepository.selectExpiredToken(new Date());
        expired.forEach(tokenToUserRepository::delete);

        Optional<TokenToUser> tokenToUser = tokenToUserRepository.findByToken(token);

        if (tokenToUser.isEmpty()) {
            log.error(DTOErrorDescription.EXPIRED.get());
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    DTOError.INVALID_REQUEST.get(),
                    DTOErrorDescription.EXPIRED.get()));
        }

        if (userId != tokenToUser.get().getUserId()) {
            log.error(DTOErrorDescription.BAD_CREDENTIALS.get());
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    DTOError.INVALID_REQUEST.get(),
                    DTOErrorDescription.BAD_CREDENTIALS.get()));
        }

        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            log.error(DTOErrorDescription.BAD_CREDENTIALS.get());
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    DTOError.INVALID_REQUEST.get(),
                    DTOErrorDescription.BAD_CREDENTIALS.get()));
        }

        User user = userOptional.get();

        user.setIsApproved(true);
        userRepository.save(user);
        tokenToUserRepository.delete(tokenToUser.get());

        log.info("Register was confirm");

        return ResponseEntity.ok().body(new DTOSuccessfully(null,
                new Date().getTime() / 1000,
                new DTOMessage()));
    }
}
