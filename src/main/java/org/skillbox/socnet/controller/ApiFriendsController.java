package org.skillbox.socnet.controller;

import org.apache.log4j.Logger;
import org.skillbox.socnet.api.dto.DTOError;
import org.skillbox.socnet.api.response.error.ErrorResponse;
import org.skillbox.socnet.model.entity.enums.FriendshipStatus;
import org.skillbox.socnet.service.friends.FriendsService;
import org.skillbox.socnet.service.friends.RequestService;
import org.skillbox.socnet.service.friends.SetFriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/friends")
public class ApiFriendsController {

    private final FriendsService friendsService;
    private final SetFriendshipService setFriendshipService;
    private final RequestService requestService;
    private final Logger LOGGER = Logger.getLogger(ApiFriendsController.class.getName());

    @Autowired
    public ApiFriendsController(FriendsService friendsService, SetFriendshipService setFriendshipService, RequestService requestService) {
        this.friendsService = friendsService;
        this.setFriendshipService = setFriendshipService;
        this.requestService = requestService;
    }

    @GetMapping("")
    public ResponseEntity<?> getFriends(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer itemPerPage,
            Principal principal) {

        LOGGER.info("getFriends\nname: " + name + "\noffset: " + offset + "\nitemPerPage: " + itemPerPage);
        if (principal == null) {
            return new ResponseEntity<>(
                    new ErrorResponse(DTOError.INVALID_REQUEST.get(), DTOError.UNAUTHORIZED.get()), HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok(friendsService.getFriends(name, offset, itemPerPage));
    }

    @GetMapping("/request")
    public ResponseEntity<?> getRequest(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer itemPerPage,
            Principal principal) {
        System.out.println("/api/v1/friends/request name: " + name);
        System.out.println("/api/v1/friends/request offset: " + offset);
        System.out.println("/api/v1/friends/request itemPerPage: " + itemPerPage);

        if (principal == null) {
            return new ResponseEntity<>(
                    new ErrorResponse(DTOError.INVALID_REQUEST.get(), DTOError.UNAUTHORIZED.get()), HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok(friendsService.getRequests(name, offset, itemPerPage));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<?> getRequest(
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer itemPerPage,
            Principal principal) {
        System.out.println("/api/v1/friends/recommendations offset: " + offset);
        System.out.println("/api/v1/friends/recommendations itemPerPage: " + itemPerPage);

        if (principal == null) {
            return new ResponseEntity<>(
                    new ErrorResponse(DTOError.INVALID_REQUEST.get(), DTOError.UNAUTHORIZED.get()), HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok(friendsService.getRecommendations(offset, itemPerPage));
    }

    @DeleteMapping("/{ID}")
    public ResponseEntity<?> deleteFriend(@PathVariable Integer ID, Principal principal) {
        System.out.println("/api/v1/friends/" + ID);

        if (principal == null) {
            return new ResponseEntity<>(
                    new ErrorResponse(DTOError.INVALID_REQUEST.get(), DTOError.UNAUTHORIZED.get()), HttpStatus.UNAUTHORIZED);
        }

        return friendsService.deleteFriend(ID);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> friendRequest(@PathVariable Integer id) {
        return requestService.createResponse(id);
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> addRequestFriend(@PathVariable Integer id) {
        return setFriendshipService.createResponse(id, FriendshipStatus.REQUEST);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> declinedFriend(@PathVariable Integer id) {
        return setFriendshipService.createResponse(id, FriendshipStatus.DECLINED);
    }
}