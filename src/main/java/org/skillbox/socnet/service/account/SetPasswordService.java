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
import org.skillbox.socnet.service.UserService;
import org.skillbox.socnet.service.auth.LoginLogoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SetPasswordService {

    private final TokenToUserRepository tokenToUserRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final LoginLogoutService loginLogoutService;
    private final Logger log = Logger.getLogger(SetPasswordService.class.getName());

    @Autowired
    public SetPasswordService(TokenToUserRepository tokenToUserRepository, UserRepository userRepository, UserService userService, LoginLogoutService loginLogoutService) {
        this.tokenToUserRepository = tokenToUserRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.loginLogoutService = loginLogoutService;
    }

    public ResponseEntity<?> createResponse(String token, String password) {

        User user;

        List<TokenToUser> expired = tokenToUserRepository.selectExpiredToken(new Date());
        if (!expired.isEmpty()) {
            log.info("Expired token was delete");
            expired.forEach(tokenToUserRepository::delete);
        }

        Optional<TokenToUser> byToken = tokenToUserRepository.findByToken(token);
        if (byToken.isEmpty()) {
            try {
                user = userService.getCurrentUser();
            } catch (UsernameNotFoundException ex) {
                log.error(DTOErrorDescription.EXPIRED.get());
                return ResponseEntity.badRequest().body(new ErrorResponse(
                        DTOError.INVALID_REQUEST.get(),
                        DTOErrorDescription.EXPIRED.get()));
            }
            loginLogoutService.loginUser(user.getEmail(), password);
        } else {
            user = userRepository.findById(byToken.get().getUserId()).get();
            tokenToUserRepository.delete(byToken.get());
        }
        user.setPassword(new BCryptPasswordEncoder(12).encode(password));
        userRepository.save(user);

        log.info("Password was change");

        return ResponseEntity.ok().body(new DTOSuccessfully(null,
                new Date().getTime() / 1000,
                new DTOMessage()));
    }
}
