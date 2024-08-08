package com.smilecat.Repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.smilecat.DTO.CommentDTO;
import com.smilecat.DTO.PostDTO;
import com.smilecat.entity.Comment;
import com.smilecat.entity.File;
import com.smilecat.entity.Post;

public interface PostRepository extends ReactiveCrudRepository<Post, String> {

    @Query("INSERT INTO posts (openId, title, context, date, postId) VALUES (:openId, :title, :context, :date, :postId)")
    Mono<Void> publishPost(Post post);

    @Query("INSERT INTO comments (openId, postId, context) VALUES (:openId, :postId, :context)")
    Mono<Void> publishComment(Comment comment);

    @Query("INSERT INTO post_image_list (postId, url, name) VALUES (:postId, :file.url, :file.name)")
    Mono<Void> setImageList(File file, String postId);

    @Query("SELECT postId, openId, title, date, context FROM posts")
    Flux<PostDTO> getPost();

    @Query("SELECT url FROM post_image_list WHERE postId = :postId")
    Flux<String> getImageList(String postId);

    @Query("SELECT v.userId, c.context FROM comments c INNER JOIN volunteer v ON c.openId = v.openId WHERE c.postId = :postId")
    Flux<CommentDTO> getCommentList(String postId);

    @Query("SELECT userId FROM volunteer WHERE openId = :openId")
    Mono<String> getUserId(String openId);

    @Query("SELECT avatarUrl FROM volunteer WHERE openId = :openId")
    Mono<String> getAvatarUrl(String openId);
}
