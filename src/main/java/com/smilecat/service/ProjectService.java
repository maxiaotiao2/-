package com.smilecat.service;

import com.smilecat.DTO.*;
import com.smilecat.entity.File;
import com.smilecat.entity.Project;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProjectService {
    Mono<Void> applyVolunteerProject(Project p);

    Mono<Void> agreeProject(String projectId);

    Mono<Void> rejectProject(String projectId);

    Flux<Project> getPendingProjects();

    Flux<File> getFiles(String projectId);

    Flux<ProjectDTO> getProjectAdd(String openId);

    Mono<ProjectDTO1> getProjectStatic(String openId);

    Flux<ProjectDTO2> getOpenProjects();

    Mono<Void> publishProject(String openId);

    Mono<Void> publishDoc(FileDTO fileDTO);

    Mono<String> getProjectId(String senderId);

    Flux<ProjectDTO3> getProjects();
}
