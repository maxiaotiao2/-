package com.smilecat.service;

import com.smilecat.DTO.PostDTO;
import com.smilecat.entity.Comment;
import com.smilecat.entity.Post;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PostService {
    Mono<Void> publishPost(Post p);

    Mono<Void> publishComment(Comment comment);

    Flux<PostDTO> getPost();
}
