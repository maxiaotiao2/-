package com.smilecat.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private String openId;
    private String userId;
    private String avatarUrl;
    private String title;
    private String dateString;
    private LocalDateTime date;
    private String context;
    private List<String> imageList;
    private List<CommentDTO> commentList;

    private String postId;
}
