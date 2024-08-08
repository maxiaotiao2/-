package com.smilecat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("signininfo")
public class SignInInfo {
    @Id
    private String uuid;
    private int total;
    private double rate;
    private String location;
    private String name;
    private int not;
}
