package org.skillbox.socnet.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skillbox.socnet.model.entity.*;
import org.skillbox.socnet.model.entity.enums.MessagesPermission;
import org.skillbox.socnet.model.entity.enums.UserType;
import org.skillbox.socnet.model.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@DisplayName("Testing feeds")
@ActiveProfiles("test_config")
public class FeedsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private PostCommentRepository postCommentRepository;

    @MockBean
    private NotificationRepository notificationRepository;

    @MockBean
    private PostLikeRepository postLikeRepository;

    @MockBean
    private TagRepository tagRepository;

    @LocalServerPort
    private String port;

    @BeforeEach
    public void setUp() throws Exception {

        Country country = new Country();
        country.setId(1);
        country.setName("Russia");
        Town town = new Town(1, "Moscow", country, List.of());
        User user = new User(1, "Анна", "Чернов", LocalDateTime.now(), LocalDateTime.now().minusYears(23), "user@mail.ru",
                "", "password", "", "", town, "", (byte) 1, MessagesPermission.ALL,
                LocalDateTime.now(), (byte) 1, (byte) 1, UserType.USER, List.of(), List.of(), List.of(), List.of(), List.of(), List.of());

        Post post = new Post(1, (long) 100, user, "title", "postText", (byte) 1, (byte) 0, 10);
        PostComment comment = new PostComment(1, LocalDateTime.now(), post, 1, null, user, "Коммент", (byte) 1, false, List.of());
        PostLike postLike = new PostLike(1, LocalDateTime.now(), user, post, comment);
        Tag tag = new Tag();
        tag.setName("test");
        tag.setId(1);
        tag.setPosts(List.of(post));
        post.setTags(List.of(tag));

        doReturn(Optional.of(user)).when(userRepository).findByEmail(anyString());
        doReturn(new PageImpl<>(List.of(post))).when(postRepository).search(any(), any(), anyString(), any(), any());
        doReturn(new PageImpl<>(List.of(post))).when(postRepository).getAllPostByUsers(any(), any(), any());
        doReturn(List.of(comment)).when(postCommentRepository).searchByPost(any());
        doReturn(List.of()).when(notificationRepository).getNotificationsWithDelay(any());
        doReturn(Optional.of(postLike)).when(postLikeRepository).findMyLikeInComment(anyInt(), anyInt());
        doReturn(List.of(tag.getName())).when(tagRepository).getNameByPost(anyInt());

    }

    @Test
    @DisplayName("Testing run Spring Boot")
    public void contextLoads() throws Exception {
    }

    @Test
    @DisplayName("Checking the error noAuthentication in the response")
    public void checkErrorsInResponse() throws Exception {
        doThrow(UsernameNotFoundException.class).when(userRepository).findByEmail(anyString());
        //noAuthentication
        this.mockMvc.perform(get(port + "/api/v1/feeds"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Checking the error authentication in the response")
    @WithMockUser
    public void givenUser_whenGetPost_thenOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(port + "/api/v1/feeds")
                .accept(MediaType.ALL))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
