package com.smilecat.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
//志愿者在志愿大厅申请加入某个项目
public class NoticeDTO1 {
    private String volunteerOpenId;
    private String managerOpenId;
    private String context;
    private LocalDateTime date;
}
