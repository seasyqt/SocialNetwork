package org.skillbox.socnet.controller;

import org.skillbox.socnet.api.request.UserRegistrationRequest;
import org.skillbox.socnet.api.request.account.DTONotification;
import org.skillbox.socnet.api.request.account.Email;
import org.skillbox.socnet.api.request.account.RegisterConfirm;
import org.skillbox.socnet.api.request.account.SetPassword;
import org.skillbox.socnet.service.account.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/account")
public class ApiAccountController {

    private final RecoveryService recoveryService;
    private final SetPasswordService setPasswordService;
    private final SetEmailService setEmailService;
    private final NotificationsService notificationsService;
    private final RegistrationService registrationService;
    private final RegisterConfirmService registerConfirmService;

    @Autowired
    public ApiAccountController(RecoveryService recoveryService, SetPasswordService setPasswordService, SetEmailService setEmailService, NotificationsService notificationsService, RegistrationService registrationService, RegisterConfirmService registerConfirmService) {
        this.recoveryService = recoveryService;
        this.setPasswordService = setPasswordService;
        this.setEmailService = setEmailService;
        this.notificationsService = notificationsService;
        this.registrationService = registrationService;
        this.registerConfirmService = registerConfirmService;
    }

    @PostMapping("/register")
    private ResponseEntity<?> register(@RequestBody UserRegistrationRequest userRegistrationRequest, HttpServletRequest servletRequest) {
        return registrationService.registrationUser(userRegistrationRequest,
                servletRequest.getHeader("Origin"));
    }


    @PutMapping("/password/recovery")
    private ResponseEntity<?> recovery(@RequestBody Email email, HttpServletRequest servletRequest) {
        return recoveryService.createResponse(
                email.getEmail(),
                servletRequest.getHeader("Origin"));
    }

    @PutMapping("/password/set")
    private ResponseEntity<?> setPassword(@RequestBody SetPassword setPassword) {
        return setPasswordService.createResponse(setPassword.getToken(), setPassword.getPassword());
    }

    @PutMapping("/email")
    private ResponseEntity<?> email(@RequestBody Email email, HttpServletRequest request, HttpServletResponse response) {
        return setEmailService.createResponse(email.getEmail(), request, response);
    }

    @PutMapping("/notifications")
    private ResponseEntity<?> notifications(@RequestBody DTONotification notification) {
        return notificationsService.createResponse(
                notification.getNotificationType(),
                notification.isEnable());
    }

    @GetMapping("/notifications")
    private ResponseEntity<?> getNotifications() {
        return notificationsService.getNotifications();
    }

    @PostMapping("/register/confirm")
    private ResponseEntity<?> registerConfirm(@RequestBody RegisterConfirm registerConfirm) {
        return registerConfirmService.createResponse(
                registerConfirm.getUserId(),
                registerConfirm.getToken());
    }
}
