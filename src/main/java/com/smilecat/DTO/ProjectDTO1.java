package com.smilecat.DTO;

import com.smilecat.entity.SignInInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO1 {
    private String projectName;
    private String location;
    private String date;
    private int volunteers;
    private List<SignInInfo> signInInfos;
}
