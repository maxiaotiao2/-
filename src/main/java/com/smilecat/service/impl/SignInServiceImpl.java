package com.smilecat.service.impl;

import com.smilecat.DTO.SignInDTO;
import com.smilecat.DTO.SignInDTO1;
import com.smilecat.Repository.SignInRepository;
import com.smilecat.entity.SignIn;
import com.smilecat.service.SignInService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SignInServiceImpl implements SignInService {

    @Autowired
    private SignInRepository signInRepository;

    @Override
    @Transactional
    public Mono<Void> publishSignIn(SignIn s) {
        String uuid = UUID.randomUUID().toString();
        s.setUuid(uuid);
        int total = s.getVolunteers().size();
        s.setTotal(total);
        long currentTimeMillis = System.currentTimeMillis();

        // 发布签到信息
        Mono<Void> publishSignInMono = signInRepository.publishSignIn(s);

        // 插入每个志愿者的签到记录
        List<Mono<Void>> insertSignVolMonos = s.getVolunteers().stream()
                .map(openId -> {
                    log.info(uuid);
                    log.info("time:{}", currentTimeMillis);
                    log.info(openId);
                    return signInRepository.insertSignVol(uuid, currentTimeMillis, openId);
                })
                .collect(Collectors.toList());

        // 使用 Mono.when 来等待所有操作完成
        return publishSignInMono.then(Mono.when(insertSignVolMonos));
    }

    @Override
    @Transactional
    public Mono<Void> signIn(String openId, String uuid) {
        long currentTimeMillis = System.currentTimeMillis();

        // 修改 mark 为 1 和打卡时间
        Mono<Void> signInMono = signInRepository.signIn(openId, uuid, currentTimeMillis);

        // 根据 uuid 获取管理员 openId
        Mono<String> adminOpenIdMono = signInRepository.getAdminOpenId(uuid);

        // 获取 projectId 和 duration
        Mono<String> projectIdMono = adminOpenIdMono.flatMap(adminOpenId -> signInRepository.getProjectId(adminOpenId));
        Mono<Float> durationMono = signInRepository.getDuration(uuid);

        // 更新志愿者时长和签到次数
        Mono<Void> updateMono = projectIdMono.zipWith(durationMono)
                .flatMap(tuple -> {
                    String projectId = tuple.getT1();
                    float duration = tuple.getT2();
                    return signInRepository.updateVolunteerDurationAndCheckIns(openId, projectId, duration);
                });

        // 返回一个组合的 Mono，等待所有操作完成
        return signInMono.then(updateMono);
    }

    @Override
    @Transactional
    public Flux<List<SignInDTO>> getSignIn(String openId) {
        return signInRepository.getUUIDs(openId)
                .collectList()
                .flatMapMany(uuids -> {
                    // 初始化两个列表来存储 SignInDTO
                    List<SignInDTO> currentSignIns = new ArrayList<>();
                    List<SignInDTO> pastSignIns = new ArrayList<>();

                    // 获取当前时间戳
                    long currentTimeMillis = System.currentTimeMillis();

                    // 异步获取所有 SignInDTO 并按时间分类
                    return Flux.fromIterable(uuids)
                            .flatMap(uuid -> signInRepository.getSignIn(uuid)
                                    .doOnNext(signInDTO -> {
                                        if (currentTimeMillis <= signInDTO.getEndTime()) {
                                            synchronized (currentSignIns) {
                                                currentSignIns.add(signInDTO);
                                            }
                                        } else {
                                            synchronized (pastSignIns) {
                                                pastSignIns.add(signInDTO);
                                            }
                                        }
                                    })
                            )
                            .thenMany(Flux.just(currentSignIns, pastSignIns));
                })
                .flatMap(list -> Flux.fromIterable(list)
                        .flatMap(signInDTO -> signInRepository.getMark(openId, signInDTO.getUuid())
                                .doOnNext(signInDTO::setMark)
                                .thenReturn(signInDTO)
                        )
                        .collectList()
                );
    }


    @Override
        public Flux<SignInDTO1> getValidUUIDs(String openId) {
            Long timestamp = System.currentTimeMillis();
            log.info("当前时间戳：{}", timestamp);
            return signInRepository.getValidUUIDs(openId, timestamp);
        }

    @Override
    public Mono<String> getIsIn(String openId, String uuid) {
        return signInRepository.getIsIn(openId, uuid)
                .flatMap(isIn -> {
                    if (isIn == 1) {
                        return signInRepository.getMark(openId, uuid)
                                .flatMap(mark -> {
                                    if (mark == 0) {
                                        long currentTimeMillis = System.currentTimeMillis();
                                        return signInRepository.signIn(openId, uuid, currentTimeMillis)
                                                .then(signInRepository.getAdminOpenId(uuid)
                                                        .flatMap(adminOpenId -> signInRepository.getProjectId(adminOpenId)
                                                                .flatMap(projectId -> signInRepository.getDuration(uuid)
                                                                        .flatMap(duration -> signInRepository.updateVolunteerDurationAndCheckIns(openId, projectId, duration))
                                                                )
                                                        )
                                                )
                                                .then(signInRepository.getName(uuid));
                                    } else {
                                        return signInRepository.getName(uuid);
                                    }
                                });
                    } else {
                        return Mono.just("wrong");
                    }
                });
    }



}
