package com.smilecat.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//管理员获取当前在打卡时间内的打卡任务
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignInDTO1 {
    private String uuid;
    //打卡名称
    private String name;

}
