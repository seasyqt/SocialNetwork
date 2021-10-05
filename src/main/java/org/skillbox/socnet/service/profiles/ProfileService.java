package org.skillbox.socnet.service.profiles;

import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.skillbox.socnet.api.dto.DTOError;
import org.skillbox.socnet.api.dto.DTOErrorDescription;
import org.skillbox.socnet.api.dto.DTOMessage;
import org.skillbox.socnet.api.dto.DTOSuccessfully;
import org.skillbox.socnet.api.request.ProfileChangingRequest;
import org.skillbox.socnet.api.response.PageCommonResponseList;
import org.skillbox.socnet.api.response.error.ErrorResponse;
import org.skillbox.socnet.api.response.user.UserResponse;
import org.skillbox.socnet.model.entity.FileInfo;
import org.skillbox.socnet.model.entity.Town;
import org.skillbox.socnet.model.entity.User;
import org.skillbox.socnet.model.repository.FileInfoRepository;
import org.skillbox.socnet.model.repository.UserRepository;
import org.skillbox.socnet.service.UserService;
import org.skillbox.socnet.service.auth.LoginLogoutService;
import org.skillbox.socnet.service.platform.PlatformService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final Logger log = Logger.getLogger(ProfileService.class.getName());
    private final LoginLogoutService loginLogoutService;
    private final PlatformService platformService;
    private final FileInfoRepository fileInfoRepository;

    public ResponseEntity<?> createUsersSearchResponse(String firstName, String lastName, int ageFrom, int ageTo,
                                                       String city, String country, int offset, int itemPerPage) {
        User currentUser;
        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(401).body(
                    new ErrorResponse(DTOError.UNAUTHORIZED.get(), DTOErrorDescription.UNAUTHORIZED.get()));
        }
        final PageRequest page = PageRequest.of(offset, itemPerPage);

        Page<User> findUsers = userRepository.getUsersSearch(currentUser.getId(), firstName, lastName, city, country, ageTo, ageFrom,
                page);
        List<UserResponse> data = new ArrayList<>();
        findUsers.forEach(user -> data.add(new UserResponse(user)));

        return ResponseEntity.ok(
                new PageCommonResponseList<>("string", findUsers.getTotalElements(), offset, itemPerPage,
                        data));
    }

    public ResponseEntity<?> getUserById(int id) {
        User user;
        try {
            user = userService.getUserById(id);
        } catch (UsernameNotFoundException ex) {
            log.error(DTOErrorDescription.BAD_REQUEST.get());
            return ResponseEntity.status(400).body(new ErrorResponse(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        }
        return ResponseEntity.ok(new UserResponse(user));
    }

    public ResponseEntity<?> getCurrentUser() throws UsernameNotFoundException {
        User currentUser;
        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(401).body(
                    new ErrorResponse(DTOError.UNAUTHORIZED.get(), DTOErrorDescription.UNAUTHORIZED.get()));
        }
        return ResponseEntity.ok(new UserResponse(currentUser));
    }

    public ResponseEntity<?> deleteUser(HttpServletRequest request, HttpServletResponse response) {

        User currentUser;

        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOErrorDescription.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(new ErrorResponse(
                    DTOError.UNAUTHORIZED.get(),
                    DTOErrorDescription.UNAUTHORIZED.get()));
        }
        userRepository.delete(currentUser);

        return ResponseEntity.ok(new DTOSuccessfully(
                null,
                new Date().getTime() / 1000,
                new DTOMessage()));
    }

    public ResponseEntity<?> updateUserInformation(ProfileChangingRequest newInfoUser) {
        User currentUser;

        //Проверка авторизирвоан ли пользователь
        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOErrorDescription.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(new ErrorResponse(
                    DTOError.UNAUTHORIZED.get(),
                    DTOErrorDescription.UNAUTHORIZED.get()));
        }
        updateUser(currentUser, newInfoUser);
        return ResponseEntity.ok(new UserResponse(currentUser));
    }

    private void updateUser(User currentUser, ProfileChangingRequest updateInfo) {
        LocalDateTime newBirthDate = updateInfo.getBirthDate().equals("") ?
                null : LocalDateTime.parse(updateInfo.getBirthDate().substring(0, 19));
        currentUser.setAbout(updateInfo.getAbout());
        currentUser.setBirthDate(newBirthDate);
        Town newTown = updateInfo.getCity().equals("") || updateInfo.getCountry().equals("")
                ? null
                : platformService.createCityAndCountry(updateInfo.getCity(), updateInfo.getCountry());
        currentUser.setTown(newTown);
        currentUser.setFirstName(updateInfo.getFirstName());
        currentUser.setLastName(updateInfo.getLastName());
        currentUser.setPhone(updateInfo.getPhone());
        if (updateInfo.getPhotoId() != null) {
            FileInfo file = fileInfoRepository.getById(updateInfo.getPhotoId());
            //TODO Проверить корректность ссылки
            currentUser.setPhoto(file.getRelativeFilePath());
            userRepository.save(currentUser);
            return;
        }
        userRepository.save(currentUser);
    }
}
