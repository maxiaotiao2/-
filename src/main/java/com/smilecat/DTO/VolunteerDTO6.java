package com.smilecat.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerDTO6 {
    private String userId;
    private String phone;
    private String mail;
    private Integer checkIns;
    private Integer workTime;
    private List<SignInDTO2> signInDTO2s;
}
