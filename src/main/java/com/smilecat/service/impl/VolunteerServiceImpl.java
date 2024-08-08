package com.smilecat.service.impl;

import com.smilecat.DTO.*;
import com.smilecat.Repository.SignInRepository;
import com.smilecat.Repository.VolunteerRepository;
import com.smilecat.entity.File;
import com.smilecat.entity.Notice;
import com.smilecat.entity.Volunteer;
import com.smilecat.service.NoticeService;
import com.smilecat.service.ProjectService;
import com.smilecat.service.VolunteerService;
import com.smilecat.util.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class VolunteerServiceImpl implements VolunteerService {

    @Autowired
    private VolunteerRepository volunteerRepo;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private SignInRepository signInRepository;

    @Autowired
    private EmailUtil emailUtil;

    //查询用户是否已经注册
    @Override
    public Mono<Integer> checkRegistration(String openId) {
        return volunteerRepo.checkRegistration(openId);
    }

    @Override
    public Mono<Integer> getRole(String openId) {
        return volunteerRepo.getRole(openId);
    }

    @Override
    public Mono<Void> registerVolunteer(Volunteer v) {
        return volunteerRepo.insertVolunteer(v);
    }

    @Override
    public Flux<VolunteerDTO3> getVolunteer3(String openId) {
        Mono<String> projectIdMono = volunteerRepo.getProjectId(openId);

        return projectIdMono
                // 将 Mono<String> 转换为 Flux<String>，并执行下一步操作
                .flatMapMany(projectId -> volunteerRepo.getOpenIds(projectId)
                        // 对每个 openId 执行操作
                        .flatMap(newOpenId -> {
                            // 获取 userId 并构造 VolunteerDTO3 对象
                            Mono<String> userIdMono = volunteerRepo.getUserId(newOpenId);
                            return userIdMono.map(userId -> {
                                VolunteerDTO3 volunteerDTO3 = new VolunteerDTO3();
                                volunteerDTO3.setOpenId(openId);
                                volunteerDTO3.setUserId(userId);
                                return volunteerDTO3;
                            });
                        })
                );

    }

    @Override
    public Mono<Void> deleteVolunteer(String managerOpenId, String volunteerOpenId) {
        return volunteerRepo.getProjectId(managerOpenId)
                .flatMap(projectId -> {
                    // 删除 volunteer
                    return volunteerRepo.deleteVolunteer(projectId, volunteerOpenId);
                });
    }

    @Override
    public Flux<VolunteerDTO1> getVolunteer1(String openId) {
        return volunteerRepo.getProjectId(openId)  // 获取 projectId
                .flatMapMany(projectId -> volunteerRepo.getVolunteer1(projectId)  // 获取志愿者列表
                        .flatMap(v -> {
                            // 对每个志愿者进行异步操作
                            Mono<String> mailMono = volunteerRepo.getMail(v.getOpenId());
                            Mono<String> userIdMono = volunteerRepo.getUserId(v.getOpenId());
                            Mono<Integer> workTimeMono = volunteerRepo.getWorkTime(v.getOpenId());

                            return Mono.zip(mailMono, userIdMono, workTimeMono)
                                    .map(tuple -> {
                                        // 将获取到的值设置到 VolunteerDTO1 对象中
                                        v.setMail(tuple.getT1());
                                        v.setUserId(tuple.getT2());
                                        v.setWorkTime(tuple.getT3());
                                        return v;
                                    });
                        }));
    }

    @Override
    public Flux<VolunteerDTO2> getVolunteer2(String openId) {
        return volunteerRepo.getProjectId(openId)  // 获取 projectId
                .flatMapMany(projectId -> volunteerRepo.getVolunteer2(projectId)  // 获取志愿者列表
                        .flatMap(v -> {
                            // 获取志愿者的其他信息
                            Mono<VolunteerDTO> volunteerDTOMono = volunteerRepo.getVolunteer2Part(v.getOpenId());
                            Mono<Integer> workTimeMono = volunteerRepo.getWorkTime(v.getOpenId());
                            Mono<Integer> checkInsMono = volunteerRepo.getCheckIns(v.getOpenId());

                            return Mono.zip(volunteerDTOMono, workTimeMono, checkInsMono)
                                    .map(tuple -> {
                                        VolunteerDTO volunteerDTO = tuple.getT1();
                                        v.setMail(volunteerDTO.getMail());
                                        v.setUserId(volunteerDTO.getUserId());
                                        v.setPhone(volunteerDTO.getPhone());
                                        v.setAvatarUrl(volunteerDTO.getAvatarUrl());
                                        v.setWorkTime(tuple.getT2());
                                        v.setCheckIns(tuple.getT3());
                                        return v;
                                    });
                        }));

    }

    @Override
    @Transactional
    public Mono<Void> recruitVolunteers(String managerOpenId, String volunteerOpenId) {
        return volunteerRepo.getProjectId(managerOpenId)
                .flatMap(projectId -> {
                    // 将招募信息插入 volunteerApplications
                    return volunteerRepo.recruitVolunteers(managerOpenId, volunteerOpenId)
                            .then(Mono.zip(
                                    Mono.just(projectId),
                                    volunteerRepo.getDescription(projectId),
                                    projectService.getFiles(projectId).collectList()
                            ));
                })
                .flatMap(tuple -> {
                    String projectId = tuple.getT1();
                    String context = tuple.getT2();
                    List<File> files = tuple.getT3();

                    // 创建站内信
                    Notice notice = new Notice();
                    notice.setReceiverId(Collections.singletonList(volunteerOpenId));
                    notice.setOpenId(managerOpenId);
                    notice.setTitle("招募提醒");
                    notice.setContext(context);
                    notice.setDate(LocalDateTime.now());
                    notice.setFileList(files);
                    notice.setMark(0);
                    notice.setFlag(2);

                    // 发布站内信
                    return noticeService.publishNotice(notice);
                });

    }


    @Override
    public Flux<VolunteerDTO4> getVolunteer4() {
        return volunteerRepo.getVolunteer4()
                .flatMap(volunteerDTO4 ->
                        // 处理每个志愿者的工作时间、签到次数和参与项目
                        Mono.zip(
                                Mono.just(volunteerDTO4),
                                volunteerRepo.getWorkTime(volunteerDTO4.getOpenId()).defaultIfEmpty(0),
                                volunteerRepo.getCheckIns(volunteerDTO4.getOpenId()).defaultIfEmpty(0),
                                volunteerRepo.getProject(volunteerDTO4.getOpenId()).collectList().defaultIfEmpty(Collections.emptyList())
                        )
                )
                .map(tuple -> {
                    VolunteerDTO4 v = tuple.getT1();
                    v.setWorkTime(tuple.getT2());
                    v.setCheckIns(tuple.getT3());
                    v.setProject(tuple.getT4());
                    return v;
                });
    }


    @Override
    public Flux<Admin> getAllAdmins() {
        return volunteerRepo.getAllAdmins()
                .flatMap(admin ->
                        // 对每个管理员获取 userId
                        Mono.zip(
                                Mono.just(admin),
                                volunteerRepo.getUserId(admin.getOpenId()).defaultIfEmpty(null)
                        )
                )
                .map(tuple -> {
                    Admin admin = tuple.getT1();
                    admin.setUserId(tuple.getT2());
                    return admin;
                });
    }

    @Override
    public Flux<VolunteerDTO5> getAllVolunteers() {
        return volunteerRepo.getAllVolunteers();
    }

    @Override
    public Mono<XSSFWorkbook> volunteerDataDerive(String openId) {
        return volunteerRepo.getMail(openId)
                .flatMap(mail -> volunteerRepo.getProjectId(openId)
                        .flatMapMany(projectId -> volunteerRepo.getVolunteerOpenIds6(projectId))
                        .flatMap(id -> {
                            Mono<VolunteerDTO6> volunteer6 = volunteerRepo.getVolunteer6(id)
                                    .flatMap(volunteer -> {
                                        Mono<VolunteerDTO6> volunteerWithCheck = volunteerRepo.getCheckIns(id)
                                                .map(checkIn -> {
                                                    volunteer.setCheckIns(checkIn);
                                                    return volunteer;
                                                });

                                        Mono<VolunteerDTO6> volunteerWithWorkTime = volunteerRepo.getWorkTime(id)
                                                .map(workTime -> {
                                                    volunteer.setWorkTime(workTime);
                                                    return volunteer;
                                                });

                                        Mono<VolunteerDTO6> volunteerWithUUIDS = volunteerRepo.getUUIDs(id)
                                                .flatMap(uuid -> volunteerRepo.getSignInDTO2(uuid)
                                                        .flatMap(signIn -> volunteerRepo.getTimeStamp(uuid)
                                                                .map(timeStamp -> LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), ZoneId.systemDefault()))
                                                                .map(dateTime -> dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                                                                .map(format -> {
                                                                    signIn.setCheckInTimeString(format);
                                                                    return signIn;
                                                                }))
                                                        .map(signIn -> {
                                                            volunteer.getSignInDTO2s().add(signIn);
                                                            return volunteer;
                                                        }))
                                                .then(Mono.just(volunteer));
                                        return Mono.zip(volunteerWithCheck, volunteerWithWorkTime, volunteerWithUUIDS)
                                                .thenReturn(volunteer);
                                    });
                            return volunteer6;
                        })
                        .collectList()
                        .map(volunteerDTO6s -> {
                            XSSFWorkbook wb = new XSSFWorkbook();
                            Sheet sheet = wb.createSheet("Volunteers");
                            Row titleRow = sheet.createRow(0);
                            titleRow.createCell(0).setCellValue("用户名");
                            titleRow.createCell(1).setCellValue("电话号码");
                            titleRow.createCell(2).setCellValue("邮箱");
                            titleRow.createCell(3).setCellValue("打卡次数");
                            titleRow.createCell(4).setCellValue("志愿时长");

                            int cell = 1;
                            for (VolunteerDTO6 volunteerDTO6 : volunteerDTO6s) {
                                Row row = sheet.createRow(cell);
                                row.createCell(0).setCellValue(volunteerDTO6.getUserId());
                                row.createCell(1).setCellValue(volunteerDTO6.getPhone());
                                row.createCell(2).setCellValue(volunteerDTO6.getMail());
                                row.createCell(3).setCellValue(volunteerDTO6.getCheckIns());
                                row.createCell(4).setCellValue(volunteerDTO6.getWorkTime());
                                int subCell = cell + 1;
                                if (!volunteerDTO6.getSignInDTO2s().isEmpty()) {
                                    for (SignInDTO2 signInDTO2 : volunteerDTO6.getSignInDTO2s()) {
                                        Row subRow = sheet.createRow(subCell);
                                        subRow.createCell(1).setCellValue(signInDTO2.getName());
                                        subRow.createCell(2).setCellValue(signInDTO2.getCheckInTimeString());
                                        subRow.createCell(3).setCellValue(signInDTO2.getAddress());
                                        subRow.createCell(4).setCellValue(signInDTO2.getDuration());
                                        subCell++;
                                    }
                                    cell = subCell;
                                } else {
                                    cell++;
                                }
                            }
                            return wb;
                        })
                        .doOnSuccess(wb -> emailUtil.sendMessageCarryFile(mail, "绿染家园，邮你助行", "志愿者信息", wb))
                );
    }


    @Override
    public Mono<VolunteerDTO7> getVolunteer7(String openId) {
        // 获取志愿者的基本信息
        Mono<VolunteerDTO7> volunteerDTO7Mono = volunteerRepo.getVolunteer7(openId);

        // 获取该志愿者的签到次数
        Mono<Integer> signInsMono = signInRepository.getOnePeopleSignIns(openId);

        // 使用 zip 合并这两个 Mono
        return Mono.zip(volunteerDTO7Mono, signInsMono)
                .map(tuple -> {
                    VolunteerDTO7 volunteerDTO7 = tuple.getT1();
                    int signIns = tuple.getT2();
                    volunteerDTO7.setSignIns(signIns);
                    return volunteerDTO7;
                });
    }

    @Override
    public Mono<PersonalInfo> getPersonalInfo(String openId) {
        Mono<PersonalInfo> personalInfoMono = volunteerRepo.getPersonalInfo(openId);
        Mono<Integer> workTimeMono = volunteerRepo.getWorkTime(openId);
        Mono<Integer> checkInsMono = volunteerRepo.getCheckIns(openId);
        Mono<List<String>> projectMono = volunteerRepo.getProject(openId).collectList();

        return Mono.zip(personalInfoMono, workTimeMono, checkInsMono, projectMono)
                .map(tuple -> {
                    PersonalInfo personalInfo = tuple.getT1();
                    personalInfo.setWorkTime(tuple.getT2());
                    personalInfo.setCheckIns(tuple.getT3());
                    personalInfo.setProject(tuple.getT4());
                    return personalInfo;
                });
    }
}
