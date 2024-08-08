package com.smilecat.DTO;

import com.smilecat.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO extends Project {
    private int workTime;
    private int checkIns;
}
