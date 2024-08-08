package com.smilecat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("project")
public class Project {
    @Id
    @Column("projectId")
    private String projectId;

    @Column("openId")
    private String openId;
    private String date;

    @Column("endDate")
    private LocalDate endDate;

    @Column("startDate")
    private LocalDate startDate;

    @Column("projectName")
    private String projectName;
    private String organization;
    private String description;
    private String location;
    private int status = 1;//1:申请 2：通过 3：拒绝

    private List<File> fileList;
}
