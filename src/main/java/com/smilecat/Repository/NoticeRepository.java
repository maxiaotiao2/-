package com.smilecat.Repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.smilecat.DTO.NoticeDTO;
import com.smilecat.entity.File;
import com.smilecat.entity.Notice;

public interface NoticeRepository extends ReactiveCrudRepository<Notice, String> {

    @Query("INSERT INTO notice (uuid, openId, context, date, flag, title) VALUES (:uuid, :openId, :context, :date, :flag, :title)")
    Mono<Void> publishNotice(Notice notice);

    @Query("INSERT INTO notice_files (url, uuid, name) VALUES (:url, :uuid, :name)")
    Mono<Void> setFile(String url, String uuid, String name);

    @Query("INSERT INTO notice_receiverid (receiverId, uuid) VALUES (:receiverId, :uuid)")
    Mono<Void> setReceiverId(String receiverId, String uuid);

    @Query("SELECT role FROM volunteer WHERE openId = :openId")
    Mono<Integer> getRole(String openId);

    @Query("SELECT uuid FROM notice_receiverid WHERE receiverId = :openId")
    Flux<String> getUUID(String openId);

    @Query("SELECT uuid, openId, context, date, flag, title FROM notice WHERE uuid = :uuid")
    Mono<NoticeDTO> getNotice(String uuid);

    @Query("SELECT url, name FROM notice_files WHERE uuid = :uuid")
    Flux<File> getFiles(String uuid);

    @Query("SELECT receiverId FROM notice_receiverid WHERE uuid = :uuid")
    Flux<String> getReceiverId(String uuid);

    @Query("SELECT userId FROM volunteer WHERE openId = :openId")
    Mono<String> getUserId(String openId);

    @Query("SELECT avatarUrl FROM volunteer WHERE openId = :openId")
    Mono<String> getAvatarUrl(String openId);

    @Query("SELECT uuid FROM notice WHERE openId = :openId")
    Flux<String> getSenderUUID(String openId);

    @Query("UPDATE notice_receiverid SET mark = 1 WHERE receiverId = :openId AND uuid = :uuid")
    Mono<Void> read(String openId, String uuid);

    @Query("INSERT INTO volunteer_applications (volunteerOpenId, managerOpenId) VALUES (:volunteerOpenId, :managerOpenId)")
    Mono<Void> setVolunteerApplications(String volunteerOpenId, String managerOpenId);

    @Query("SELECT openId FROM notice WHERE uuid = :uuid")
    Mono<String> getSenderOpenId(String uuid);

    @Query("SELECT projectId FROM project WHERE openId = :senderId")
    Mono<String> getProjectId(String senderId);

    @Query("INSERT INTO vol_pro (openId, projectId) VALUES (:openId, :projectId)")
    Mono<Void> addVolunteerToProject(String openId, String projectId);

    @Query("SELECT name FROM signin WHERE uuid = :uuid")
    Mono<String> getName(String uuid);

    @Query("SELECT address FROM signin WHERE uuid = :uuid")
    Mono<String> getAddress(String uuid);

    @Query("SELECT total FROM signin WHERE uuid = :uuid")
    Mono<Integer> getTotal(String uuid);

    @Query("SELECT count(*) FROM sign_vol WHERE uuid = :uuid AND mark = 0")
    Mono<Integer> getNot(String uuid);

    @Query("SELECT mark FROM notice_receiverid WHERE receiverId = :openId AND uuid = :uuid")
    Mono<Integer> getMark(String openId, String uuid);

    @Query("DELETE FROM notice WHERE uuid = :uuid")
    Mono<Void> deleteNotice(String uuid);

    @Query("DELETE FROM notice_receiverid WHERE uuid = :uuid")
    Mono<Void> deleteReceiveNotice(String uuid);
}
