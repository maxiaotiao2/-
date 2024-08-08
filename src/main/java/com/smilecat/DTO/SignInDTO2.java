package com.smilecat.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SignInDTO2 {
    private String uuid;
    private String name;
    private String checkInTimeString;
    private String address;
    private String duration;
}
