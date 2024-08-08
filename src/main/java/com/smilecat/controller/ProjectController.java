package com.smilecat.controller;

import com.smilecat.DTO.*;
import com.smilecat.entity.File;
import com.smilecat.entity.Project;
import com.smilecat.entity.Result;
import com.smilecat.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    //志愿项目申请
    @PostMapping("/apply")
    public Result applyVolunteerProject(@RequestBody Project p) {
        log.info(p.toString());
        projectService.applyVolunteerProject(p);
        return Result.success();
    }

    @GetMapping("/agreeProject")
    public Result agreeProject(String projectId) {
        log.info("审核通过项目:{}", projectId);
        projectService.agreeProject(projectId);
        return Result.success();
    }

    @GetMapping("/rejectProject")
    public Result rejectProject(String projectId) {
        log.info("拒绝项目：{}", projectId);
        projectService.rejectProject(projectId);
        return Result.success();
    }

    //获取所有待审核项目
    @GetMapping("/getProject")
    public Mono<Result> getPendingProjects() {
        //先查project表中内容
        return Result.success(projectService.getPendingProjects()
                .flatMap(project ->
                        projectService.getFiles(project.getProjectId())
                                .collectList()
                                .map(files -> {
                                    project.setFileList(files);
                                    return project;
                                })
                ).collectList());
    }

    //获取志愿者参加的志愿项目
    @GetMapping("/getProjectAdd")
    public Mono<Result> getProjectAdd(String openId) {
        log.info("getProjectAdd");
        log.info("openid:{}", openId);
        return Result.success(projectService.getProjectAdd(openId).flatMap(
                project -> projectService.getFiles(project.getProjectId())
                        .collectList()
                        .map(file -> {
                            project.setFileList(file);
                            return project;
                        })
        ).collectList());
    }

    //项目管理员获取项目统计数据
    @GetMapping("/getProjectStatic")
    public Mono<Result> getProjectStatic(String openId) {
        log.info("getProjectStatic");
        log.info("openid:{}", openId);
        return Result.success(projectService.getProjectStatic(openId));

    }

    //获取所有公开项目
    @GetMapping("/getProjectOpen")
    public Mono<Result> getOpenProjects() {
        log.info("getProjectOpen");

        return Result.success(projectService.getOpenProjects());
    }

    //公益组织获取所有项目数据
    @GetMapping("/getProjects")
    public Mono<Result> getProjects()
    {
        log.info("getProjects");
        return Result.success(projectService.getProjects());
    }

    @GetMapping("/publish_project")
    public Result publishProject(String managerOpenId){
        log.info("公开招募志愿者");
        log.info("managerOpenId:{}",managerOpenId);
        projectService.publishProject(managerOpenId);
        return Result.success();
    }

    @PostMapping("/publishDoc")
    public Result publishDoc(@RequestBody FileDTO fileDTO)
    {
        log.info(fileDTO.toString());
        projectService.publishDoc(fileDTO);
        return Result.success();
    }


}
