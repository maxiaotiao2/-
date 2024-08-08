package com.smilecat.service.impl;

import com.smilecat.DTO.*;
import com.smilecat.Repository.NoticeRepository;
import com.smilecat.Repository.ProjectRepository;
import com.smilecat.Repository.VolunteerRepository;
import com.smilecat.entity.File;
import com.smilecat.entity.Project;
import com.smilecat.entity.SignInInfo;
import com.smilecat.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private VolunteerRepository volunteerRepository;

    @Autowired
    private NoticeRepository noticeRepository;


    public Mono<Void> applyVolunteerProject(Project p) {
        return projectRepository.applyVolunteerProject(p)
                .thenMany(Flux.fromIterable(p.getFileList())
                        .flatMap(f -> Mono.fromRunnable(() -> projectRepository.insertFile(f, p.getProjectId())))
                )
                .then(Mono.fromRunnable(() -> volunteerRepository.addVolPro(p.getOpenId(), p.getProjectId())))
                .onErrorResume(DataIntegrityViolationException.class, e -> {
                    log.error("插入重复主键异常: " + e.getMessage());
                    return Mono.empty();
                })
                .then();
    }

    @Override
    public Mono<Void> agreeProject(String projectId) {
        return projectRepository.agreeProject(projectId)
                .then();
    }

    @Override
    public Mono<Void> rejectProject(String projectId) {
        return projectRepository.rejectProject(projectId)
                .then();
    }

    @Override
    public Flux<Project> getPendingProjects() {
        return projectRepository.getPendingProjects();
    }

    @Override
    public Flux<File> getFiles(String projectId) {
        return projectRepository.getFiles(projectId);
    }

    @Override
    public Flux<ProjectDTO> getProjectAdd(String openId) {
        return projectRepository.getProjectAdd(openId);
    }

    @Override
    public Mono<ProjectDTO1> getProjectStatic(String openId) {
        return projectRepository.getProjectStatic(openId) // 假设这是一个返回 Mono<ProjectDTO1> 的方法
                .flatMap(pd1 -> {
                    return projectRepository.getUUIDs(openId) // 假设这是一个返回 Flux<String> 的方法
                            .flatMap(uuid -> {
                                SignInInfo signInInfo = new SignInInfo();
                                signInInfo.setUuid(uuid);
                                return Mono.zip(
                                        noticeRepository.getName(uuid), // 假设这是一个返回 Mono<String> 的方法
                                        noticeRepository.getAddress(uuid), // 假设这是一个返回 Mono<String> 的方法
                                        noticeRepository.getTotal(uuid), // 假设这是一个返回 Mono<Integer> 的方法
                                        noticeRepository.getNot(uuid) // 假设这是一个返回 Mono<Integer> 的方法
                                ).map(tuple -> {
                                    signInInfo.setName(tuple.getT1());
                                    signInInfo.setLocation(tuple.getT2());
                                    signInInfo.setTotal(tuple.getT3());
                                    signInInfo.setNot(tuple.getT4());
                                    signInInfo.setRate(1 - (double) signInInfo.getNot() / signInInfo.getTotal());
                                    return signInInfo;
                                });
                            })
                            .collectList()
                            .map(signInInfos -> {
                                pd1.setSignInInfos(signInInfos);
                                return pd1;
                            });
                });
    }

    @Override
    public Flux<ProjectDTO2> getOpenProjects() {
        return projectRepository.getOpenProjects()  // 假设这是一个返回 Flux<ProjectDTO2> 的方法
                .flatMap(p -> {
                    // 获取管理员userId
                    Mono<String> adminMono = projectRepository.getAdmin(p.getOpenId());
                    // 获取管理员邮箱
                    Mono<String> mailMono = projectRepository.getMail(p.getOpenId());
                    // 计算项目总打卡次数
                    Mono<Integer> signInsMono = projectRepository.getSignIns(p.getOpenId());
                    // 计算项目总志愿时长
                    Mono<Integer> totalDurationMono = projectRepository.getTotalDuration(p.getProjectId());
                    // 获取项目文件
                    Mono<List<File>> filesMono = projectRepository.getFiles(p.getProjectId()).collectList();

                    return Mono.zip(adminMono, mailMono, signInsMono, totalDurationMono, filesMono)
                            .map(tuple -> {
                                p.setAdmin(tuple.getT1());
                                p.setMail(tuple.getT2());
                                p.setSignIns(tuple.getT3());
                                p.setTotalDuration(tuple.getT4());
                                p.setFiles(tuple.getT5());
                                return p;
                            });
                });
    }

    @Override
    public Mono<Void> publishProject(String openId) {
        return projectRepository.publishProject(openId);
    }

    @Override
    public Mono<Void> publishDoc(FileDTO fileDTO) {
        return projectRepository.getProjectId(fileDTO.getOpenId())
                .flatMapMany(projectId -> Flux.fromIterable(fileDTO.getFileList())
                        .flatMap(file -> projectRepository.insertFile(file, projectId))
                )
                .then();
    }

    @Override
    public Mono<String> getProjectId(String senderId) {
        return null;
    }

    @Override
    public Flux<ProjectDTO3> getProjects() {
        return projectRepository.getProjects() // 假设此方法返回 Flux<ProjectDTO3>
                .flatMap(projectDTO3 ->
                        Mono.zip(
                                projectRepository.getAdmin(projectDTO3.getOpenId())
                                        .doOnNext(admin -> projectDTO3.setAdmin(admin)),
                                projectRepository.getMail(projectDTO3.getOpenId())
                                        .doOnNext(mail -> projectDTO3.setMail(mail)),
                                projectRepository.getSignIns(projectDTO3.getOpenId())
                                        .doOnNext(signIns -> projectDTO3.setSignIns(signIns)),
                                projectRepository.getTotalDuration(projectDTO3.getProjectId())
                                        .doOnNext(totalDuration -> projectDTO3.setTotalDuration(totalDuration)),
                                projectRepository.getFiles(projectDTO3.getProjectId())
//                                        .doOnNext(files -> projectDTO3.setFiles(files))
                                        .doOnNext(file -> projectDTO3.setFiles(null)).collectList()
                        ).thenReturn(projectDTO3)
                );
    }
}
