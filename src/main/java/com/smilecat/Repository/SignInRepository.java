package com.smilecat.Repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.smilecat.entity.SignIn;
import com.smilecat.DTO.SignInDTO;
import com.smilecat.DTO.SignInDTO1;

public interface SignInRepository extends ReactiveCrudRepository<SignIn, String> {

    @Query("INSERT INTO signin (uuid, name, startTime, endTime, startTimeString, endTimeString, address, latitude, longitude, duration, openId, total) VALUES (:uuid, :name, :startTime, :endTime, :startTimeString, :endTimeString, :address, :latitude, :longitude, :duration, :openId, :total)")
    Mono<Void> publishSignIn(SignIn s);

    @Query("INSERT INTO sign_vol (openId, uuid, createTime) VALUES (:openId, :uuid, :createTime)")
    Mono<Void> insertSignVol(String uuid, long createTime, String openId);

    @Query("UPDATE sign_vol SET mark = 1, checkInTime = :currentTimeMillis WHERE openId = :openId AND uuid = :uuid")
    Mono<Void> signIn(String openId, String uuid, long currentTimeMillis);

    @Query("SELECT openId FROM signin WHERE uuid = :uuid")
    Mono<String> getAdminOpenId(String uuid);

    @Query("SELECT projectId FROM project WHERE openId = :adminOpenId")
    Mono<String> getProjectId(String adminOpenId);

    @Query("SELECT duration FROM signin WHERE uuid = :uuid")
    Mono<Float> getDuration(String uuid);

    @Query("UPDATE vol_pro SET workTime = workTime + :duration, checkIns = checkIns + 1 WHERE openId = :openId AND projectId = :projectId")
    Mono<Void> updateVolunteerDurationAndCheckIns(String openId, String projectId, float duration);

    @Query("SELECT uuid FROM sign_vol WHERE openId = :openId")
    Flux<String> getUUIDs(String openId);

    @Query("SELECT name, startTime, endTime, startTimeString, endTimeString, address, latitude, longitude, duration, uuid FROM signin WHERE uuid = :u")
    Mono<SignInDTO> getSignIn(String u);

    @Query("SELECT mark FROM sign_vol WHERE openId = :openId AND uuid = :uuid")
    Mono<Integer> getMark(String openId, String uuid);

    @Query("SELECT COUNT(*) FROM sign_vol WHERE openId = :openId AND mark = 1")
    Mono<Integer> getOnePeopleSignIns(String openId);

    @Query("SELECT uuid, name FROM signin WHERE openId = :openId AND :timestamp BETWEEN startTime AND endTime")
    Flux<SignInDTO1> getValidUUIDs(String openId, Long timestamp);

    @Query("SELECT COUNT(*) FROM sign_vol WHERE openId = :openId AND uuid = :uuid")
    Mono<Integer> getIsIn(String openId, String uuid);

    @Query("SELECT name FROM signin WHERE uuid = :uuid")
    Mono<String> getName(String uuid);
}
