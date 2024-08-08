package com.smilecat.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//查询是否注册new
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerDTO7 {
    private Integer role;
    private String userId;
    private Integer signIns;
    private String mail;
    private String phone;
    private String avatarUrl;
}
