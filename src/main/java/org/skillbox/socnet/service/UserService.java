package org.skillbox.socnet.service;

import org.skillbox.socnet.model.entity.User;
import org.skillbox.socnet.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() throws UsernameNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        return getUser(currentPrincipalName);
    }

    private User getUser(String username) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("user "
                        + username + " " + "not found"));
    }

    public User getUserById(Integer userId) throws UsernameNotFoundException {
        return userRepository
                .findUserById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("user "
                        + userId + " " + "not found"));
    }

    public List<User> getUserByBirthDay(LocalDateTime birthDay) {
        return userRepository.getUsersByBirthDay(birthDay);
    }
}
