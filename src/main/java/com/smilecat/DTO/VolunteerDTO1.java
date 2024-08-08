package com.smilecat.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerDTO1 {
    private String userId;
    private String mail;
    private int workTime;
    private String openId;
}
