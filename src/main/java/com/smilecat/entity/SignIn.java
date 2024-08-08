package com.smilecat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("signin")
public class SignIn {
    @Id
    private String uuid;
    private String name;

    @Column("startTime")
    private Long startTime;

    @Column("endTime")
    private Long endTime;

    @Column("startTimeString")
    private String startTimeString;

    @Column("endTimeString")
    private String endTimeString;
    private String address;
    private float latitude;
    private float longitude;
    private float duration;
    private List<String> volunteers;

    @Column("openId")
    private String openId;
    @JsonIgnore
    private Integer total;
}
