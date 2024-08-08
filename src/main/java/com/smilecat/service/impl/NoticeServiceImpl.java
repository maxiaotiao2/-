package com.smilecat.service.impl;

import com.smilecat.DTO.NoticeDTO;
import com.smilecat.DTO.NoticeDTO1;
import com.smilecat.Repository.NoticeRepository;
import com.smilecat.Repository.ProjectRepository;
import com.smilecat.entity.Notice;
import com.smilecat.service.NoticeService;
import com.smilecat.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;


    @Transactional
    @Override
    public Mono<Void> publishNotice(Notice notice) {
        String uuid = UUID.randomUUID().toString();
        notice.setUuid(uuid);

        // 异步插入基本属性到 notice 表
        Mono<Void> noticeInsert = noticeRepository.publishNotice(notice);

        // 处理 fileList
        Mono<Void> fileInsert = Flux.fromIterable(notice.getFileList())
                .flatMap(file -> noticeRepository.setFile(file.getUrl(), uuid, file.getName()))
                .then(); // 等待所有文件插入完成

        // 处理 receiverId
        Mono<Void> receiverInsert = Flux.fromIterable(notice.getReceiverId())
                .flatMap(id -> noticeRepository.setReceiverId(id, uuid))
                .then(); // 等待所有 receiverId 插入完成

        // 将所有异步操作合并为一个 Mono<Void>
        return Mono.when(noticeInsert, fileInsert, receiverInsert);
    }

    @Transactional
    @Override
    public Flux<NoticeDTO> getNotice(String openId) {
//        int role = noticeMapper.getRole(openId);
//        List<NoticeDTO> noticeDTOS = new ArrayList<>();
//
//        //根据志愿者openId找到所有收到的站内信的标识
//        List<String> uuids = noticeMapper.getUUID(openId);
//        for (String u : uuids) {
//            NoticeDTO notice = processNoticeDTO(u);
//            int mark = noticeMapper.getMark(openId,u);
//            notice.setMark(mark);
//            noticeDTOS.add(notice);
//        }
//        //如果该openId不是普通志愿者
//        if (role != 1){
//            List<String> senderUUIDs = noticeMapper.getSenderUUID(openId);
//            for (String u : senderUUIDs) {
//                NoticeDTO notice = processNoticeDTO(u);
//                notice.setMark(1);
//                notice.setFlag(3);
//                noticeDTOS.add(notice);
//            }
//
//
//        }
//        return noticeDTOS;
        return Flux.just(new NoticeDTO());
    }

    @Override
    public Mono<Void> read(String openId, String uuid) {
       return noticeRepository.read(openId, uuid);
    }

    @Transactional
    @Override
    public Mono<Void> applyJoin(NoticeDTO1 noticeDTO1) {
        // 生成站内信
        Notice notice = new Notice();
        // receiverId
        notice.setReceiverId(Collections.singletonList(noticeDTO1.getManagerOpenId()));
        // openId
        notice.setOpenId(noticeDTO1.getVolunteerOpenId());
        // text
        notice.setTitle("申请提醒");
        // context
        notice.setContext(noticeDTO1.getContext());
        // date
        notice.setDate(noticeDTO1.getDate());
        // mark
        notice.setMark(0);
        // flag
        notice.setFlag(1);

        return Mono.fromRunnable(() -> publishNotice(notice))
                .then(noticeRepository.setVolunteerApplications(noticeDTO1.getVolunteerOpenId(), noticeDTO1.getManagerOpenId()));
    }

    @Override
    public Mono<Void> agree(String openId, String uuid, int role) {
        return Mono.defer(() -> {
            // 获取发送者的openId
            Mono<String> senderIdMono = noticeRepository.getSenderOpenId(uuid);

            // 根据role处理不同的逻辑
            if (role == 1) {
                // 普通志愿者
                return senderIdMono.flatMap(senderId -> {
                    // 获取projectId
                    Mono<String> projectIdMono = noticeRepository.getProjectId(senderId);

                    // 插入vol_pro表并增加project表中的volunteers
                    return projectIdMono.flatMap(projectId -> {
                        Mono<Void> insertVolPro = noticeRepository.addVolunteerToProject(openId, projectId);
                        Mono<Void> updateVolunteers = projectRepository.addVolunteers();

                        return Mono.when(insertVolPro, updateVolunteers);
                    });
                });
            } else {
                // 管理员
                return Mono.fromCallable(() -> noticeRepository.getProjectId(openId))
                        .flatMap(projectId -> {
                            Flux<String> volunteerOpenIdsFlux = noticeRepository.getReceiverId(uuid);

                            // 插入vol_pro表并增加project表中的volunteers
                            Flux<Void> insertVolProFlux = volunteerOpenIdsFlux.flatMap(volunteerOpenId -> {
                                Mono<Void> insertVolPro = noticeRepository.addVolunteerToProject(volunteerOpenId, String.valueOf(projectId));
                                Mono<Void> updateVolunteers = projectRepository.addVolunteers();

                                return Mono.when(insertVolPro, updateVolunteers);
                            });

                            return insertVolProFlux.then();
                        });
            }
        }).then(noticeRepository.deleteNotice(uuid).then(noticeRepository.deleteReceiveNotice(uuid)));
    }

    @Override
    public Mono<Void> reject(String uuid) {
        return Mono.defer(() -> {
            Mono<Void> deleteNoticeMono = noticeRepository.deleteNotice(uuid);
            Mono<Void> deleteReceiveNoticeMono = noticeRepository.deleteReceiveNotice(uuid);

            return Mono.when(deleteNoticeMono, deleteReceiveNoticeMono);
        });
    }

//    private NoticeDTO processNoticeDTO(String u)
//    {
//        NoticeDTO notice = noticeMapper.getNotice(u);
//        log.info("notice:{}",notice);
//        //获取fileList
//        List<File> files = noticeMapper.getFiles(u);
//        notice.setFileList(files);
//        //获取receiverId
//        List<String> ids = noticeMapper.getReceiverId(u);
//        log.info("ids:{}",ids);
//        notice.setReceiverId(ids);
//        //获取发起者role
//        log.info("发起者openId：{}",notice.getOpenId());
//        int sendRole = noticeMapper.getRole(notice.getOpenId());
//        notice.setRole(sendRole);
//        String userId = noticeMapper.getUserId(notice.getOpenId());
//        notice.setUserId(userId);
//        String avatarUrl = noticeMapper.getAvatarUrl(notice.getOpenId());
//        notice.setAvatarUrl(avatarUrl);
//        return notice;
//    }
}

