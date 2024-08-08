package com.smilecat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("posts")
public class Post {
    @Id
    @Column("postId")
    private String postId;

    @Column("openId")
    private String openId;
    private String title;
    private String context;
    private List<File> imageList;
    private LocalDateTime date;
}
