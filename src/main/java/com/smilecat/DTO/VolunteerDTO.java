package com.smilecat.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerDTO {
    private String userId;
    private String mail;
    private String phone;
    private String avatarUrl;
}
