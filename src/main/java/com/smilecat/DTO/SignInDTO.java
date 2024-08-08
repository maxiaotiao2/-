package com.smilecat.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignInDTO {
    private String uuid;
    private String name;
    private Long startTime;
    private Long endTime;
    private String startTimeString;
    private String endTimeString;
    private String address;
    private Float latitude;
    private Float longitude;
    private Float duration;
//    private String openId;
    private Integer mark;
}
