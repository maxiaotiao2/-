package com.smilecat.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VolunteerDTO2 {
    private String openId;
    private String userId;
    private String mail;
    private String phone;
    private int checkIns;
    private int workTime;
    private String avatarUrl;
}
