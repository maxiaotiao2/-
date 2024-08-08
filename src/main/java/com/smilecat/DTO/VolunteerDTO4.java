package com.smilecat.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerDTO4 {
    @JsonIgnore
    private String openId;
    private String userId;
    private String mail;
    private List<String> project;
    private int checkIns;
    private float workTime;
    private int shareTimes;
    private String phone;
}
