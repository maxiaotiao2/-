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
@Table("notice")
public class Notice {
    @Id
    private String uuid;

    @Column("openID")
    private String openId;
    private String title;
    private String context;
    private List<File> fileList;
    private List<String> receiverId;
    private LocalDateTime date;
    private Integer mark;
    private Integer flag;
}
