package org.skillbox.socnet.api.response.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.skillbox.socnet.api.response.user.UserResponse;
import org.skillbox.socnet.model.entity.Post;
import org.skillbox.socnet.model.entity.PostLike;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private int id;

    @JsonProperty("time")
    private Long timestamp;

    @JsonProperty("author")
    private UserResponse userResponse;

    private String title;

    @JsonProperty("post_text")
    private String postText;

    @JsonProperty("is_blocked")
    private boolean isBlocked;

    private long likes;

    @JsonProperty("my_like")
    private Boolean myLike;

    @JsonProperty("comments")
    private List<CommentResponse> commentResponseList;

    private List<String> tags;

    public PostResponse(Post post, List<CommentResponse> commentResponseList, PostLike postMyLike, List<String> tags) {
        this.id = post.getId();
        this.timestamp = post.getTimestamp();
        this.userResponse = new UserResponse(post.getAuthor());
        this.title = post.getTitle();
        this.postText = post.getPostText();
        this.isBlocked = post.getIsBlocked();
        this.likes = post.getLikes();
        this.myLike = postMyLike != null;
        this.commentResponseList = commentResponseList;
        long currentTime = System.currentTimeMillis();
        this.tags = tags;
    }

//    public PostResponse(Post post, List<CommentResponse> commentResponseList) {
//        this.id = post.getId();
//        this.timestamp = post.getTimestamp();
//        this.userResponse = new UserResponse(post.getAuthor());
//        this.title = post.getTitle();
//        this.postText = post.getPostText();
//        this.isBlocked = post.getIsBlocked();
//        this.likes = post.getLikes();
//        this.commentResponseList = commentResponseList;
//        long currentTime = System.currentTimeMillis();
//    }
}
