package org.skillbox.socnet.service.account;

import org.apache.log4j.Logger;
import org.skillbox.socnet.api.dto.DTOError;
import org.skillbox.socnet.api.dto.DTOErrorDescription;
import org.skillbox.socnet.api.dto.DTOMessage;
import org.skillbox.socnet.api.dto.DTOSuccessfully;
import org.skillbox.socnet.api.response.error.ErrorResponse;
import org.skillbox.socnet.model.entity.User;
import org.skillbox.socnet.model.repository.UserRepository;
import org.skillbox.socnet.service.UserService;
import org.skillbox.socnet.service.auth.LoginLogoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Service
public class SetEmailService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final Logger log = Logger.getLogger(SetEmailService.class.getName());
    private final LoginLogoutService loginLogoutService;

    @Autowired
    public SetEmailService(UserRepository userRepository, UserService userService, LoginLogoutService loginLogoutService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.loginLogoutService = loginLogoutService;
    }

    public ResponseEntity<?> createResponse(String email, HttpServletRequest request, HttpServletResponse response) {
        User currentUser;

        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOErrorDescription.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(new ErrorResponse(
                    DTOError.UNAUTHORIZED.get(),
                    DTOErrorDescription.UNAUTHORIZED.get()));
        }

        if (userRepository.findByEmail(email).isPresent()) {
            log.error(DTOErrorDescription.EXIST.get());
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    DTOError.INVALID_REQUEST.get(),
                    DTOErrorDescription.EXIST.get()));
        }
        currentUser.setEmail(email);
        userRepository.save(currentUser);

        loginLogoutService.logoutUser(request, response);

        log.info("Email was change");

        return ResponseEntity.ok(new DTOSuccessfully(null,
                new Date().getTime() / 1000,
                new DTOMessage()));
    }
}
