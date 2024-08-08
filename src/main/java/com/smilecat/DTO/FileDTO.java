package com.smilecat.DTO;

import com.smilecat.entity.File;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO {
    String openId;
    List<File> fileList;
}
