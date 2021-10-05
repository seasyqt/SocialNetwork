package org.skillbox.socnet.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skillbox.socnet.model.entity.Country;
import org.skillbox.socnet.model.entity.Town;
import org.skillbox.socnet.model.entity.User;
import org.skillbox.socnet.model.entity.enums.MessagesPermission;
import org.skillbox.socnet.model.entity.enums.UserType;
import org.skillbox.socnet.model.repository.FriendshipRepository;
import org.skillbox.socnet.model.repository.NotificationRepository;
import org.skillbox.socnet.model.repository.UserRepository;
import org.skillbox.socnet.service.friends.FriendsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Testing friends")
@ActiveProfiles("test_config")
public class FriendsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    FriendsService friendsService;

    @MockBean
    FriendshipRepository friendshipRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    private NotificationRepository notificationRepository;

    @BeforeEach
    public void setUp() throws Exception {

        Country country = new Country();
        country.setId(1);
        country.setName("Russia");
        Town town = new Town(1, "Moscow", country, List.of());
        User user = new User(1, "Анна", "Чернов", LocalDateTime.now(), LocalDateTime.now().minusYears(23), "user@mail.ru",
                "", "password", "", "", town, "", (byte) 1, MessagesPermission.ALL,
                LocalDateTime.now(), (byte) 1, (byte) 1, UserType.USER, List.of(), List.of(), List.of(), List.of(), List.of(), List.of());

        doReturn(new PageImpl<>(List.of(user))).when(userRepository).getFriendRequestsByName(any(), anyString(), any());
        doReturn(new PageImpl<>(List.of(user))).when(userRepository).getFriendsRecommendations(any(), any());
        doReturn(List.of(user)).when(userRepository).getFriendsByName(any(), anyString(), any());
        doReturn(Optional.of(user)).when(userRepository).findByEmail(anyString());
        doReturn(List.of()).when(notificationRepository).getNotificationsWithDelay(any());

    }

    @Test
    @DisplayName("Testing run Spring Boot")
    public void contextLoads() throws Exception {
    }

    @Test
    @DisplayName("Checking the error in the response")
    @WithMockUser("me")
    public void checkErrorsInResponse() throws Exception {

        this.mockMvc.perform(get("/api/v1/friends/recommendations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("error\":\"string")));


        this.mockMvc.perform(get("/api/v1/friends/request?name=Анна"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("error\":\"string")));


        this.mockMvc.perform(get("/api/v1/friends?name=Анна"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("error\":\"string")));
    }

}