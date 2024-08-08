package com.smilecat.service.impl;

import com.smilecat.DTO.CommentDTO;
import com.smilecat.DTO.PostDTO;
import com.smilecat.Repository.PostRepository;
import com.smilecat.entity.Comment;
import com.smilecat.entity.Post;
import com.smilecat.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PostServiceImpl implements PostService {


    @Autowired
    PostRepository postRepository;

    @Override
    public Mono<Void> publishPost(Post p) {
        String postId = UUID.randomUUID().toString();
        p.setPostId(postId);

        // 将图片列表转换为 Flux<File>
        return Flux.fromIterable(p.getImageList())
                .flatMap(file -> postRepository.setImageList(file, postId)) // 异步插入图片
                .then(postRepository.publishPost(p)) // 异步发布帖子
                .then(); // 返回 Mono<Void>
    }

    @Override
    public Mono<Void> publishComment(Comment comment) {
        return postRepository.publishComment(comment)
                .then(); // 返回 Mono<Void>
    }

    @Override
    public Flux<PostDTO> getPost() {
        return postRepository.getPost() // 假设此方法返回 Flux<PostDTO>
                .flatMap(postDTO ->
                        Mono.zip(
                                Mono.just(postDTO), // 保持 PostDTO
                                postRepository.getImageList(postDTO.getPostId())
                                        .doOnNext(images -> postDTO.setImageList(Collections.singletonList(images))).collectList(),
                                postRepository.getCommentList(postDTO.getPostId())
                                        .doOnNext(commentList -> postDTO.setCommentList((List<CommentDTO>) commentList)).collectList(),
                                postRepository.getUserId(postDTO.getOpenId())
                                        .doOnNext(userId -> postDTO.setUserId(userId)),
                                postRepository.getAvatarUrl(postDTO.getOpenId())
                                        .doOnNext(avatarUrl -> postDTO.setAvatarUrl(avatarUrl))
                        ).map(tuple -> tuple.getT1()) // 返回更新后的 PostDTO
                );
    }
}
