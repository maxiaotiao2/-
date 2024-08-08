package com.smilecat.controller;


import com.smilecat.DTO.PostDTO;
import com.smilecat.entity.Comment;
import com.smilecat.entity.Post;
import com.smilecat.entity.Result;
import com.smilecat.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
public class PostController {
    @Autowired
    private PostService postService;

    @PostMapping("/publishPost")
    public Result publishPost(@RequestBody Post p)
    {
        log.info("发布帖子");
        log.info("post:{}",p);
        postService.publishPost(p);
        return Result.success();
    }

    @PostMapping("/publishComment")
    public Result publishComment(@RequestBody Comment comment)
    {
        log.info("comment");
        log.info("comment:{}",comment);
        postService.publishComment(comment);
        return Result.success();
    }

    @GetMapping("/getPost")
    public Mono<Result> getPost()
    {
        return Result.success(postService.getPost());
    }
}
