package com.smilecat.entity;

import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("volunteer")
public class Volunteer {
    @Id
    @Column("openId")
    private String openId;

    @Column("userId")
    private String userId;
    private String phone;
    private String mail;
    private int role;

    @Column("avatarUrl")
    private String avatarUrl;

    @Column("shareTimes")
    private int shareTimes;
}
