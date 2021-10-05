package org.skillbox.socnet.service;

import org.skillbox.socnet.model.entity.Post;
import org.skillbox.socnet.model.entity.PostComment;
import org.skillbox.socnet.model.repository.PostCommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;

    public PostCommentService(PostCommentRepository postCommentRepository) {
        this.postCommentRepository = postCommentRepository;
    }

    public List<PostComment> searchByPost(Post post) {
        return postCommentRepository.searchByPost(post);
    }


}
