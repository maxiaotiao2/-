package com.smilecat.DTO;

import com.smilecat.entity.Notice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDTO extends Notice {
    private Integer role;
    private String avatarUrl;
    private String userId;
}
