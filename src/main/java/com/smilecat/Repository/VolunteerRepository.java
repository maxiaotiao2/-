package com.smilecat.Repository;


import com.smilecat.DTO.*;
import com.smilecat.entity.Volunteer;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface VolunteerRepository extends R2dbcRepository<Volunteer, String> {

    @Query("INSERT INTO volunteer (openId, userId, phone, mail, role, avatarUrl, shareTimes) VALUES (:#{#volunteer.openId}, :#{#volunteer.userId}, :#{#volunteer.phone}, :#{#volunteer.mail}, :#{#volunteer.role}, :#{#volunteer.avatarUrl}, :#{#volunteer.shareTimes})")
    Mono<Void> insertVolunteer(Volunteer volunteer);

    @Query("SELECT COUNT(*) FROM volunteer WHERE openId = :openId")
    Mono<Integer> checkRegistration(String openId);

    @Query("SELECT role FROM volunteer WHERE openId = :openId")
    Mono<Integer> getRole(String openId);

    @Query("INSERT INTO volunteer (openId, userId, phone, mail, role, avatarUrl) VALUES (:#{#v.openId}, :#{#v.userId}, :#{#v.phone}, :#{#v.mail}, :#{#v.role}, :#{#v.avatarUrl})")
    Mono<Void> registerVolunteer(Volunteer v);

    @Query("SELECT projectId FROM project WHERE openId = :openId")
    Mono<String> getProjectId(String openId);

    @Query("SELECT openId FROM vol_pro WHERE projectId = :projectId")
    Flux<String> getOpenIds(String projectId);

    @Query("SELECT userId FROM volunteer WHERE openId = :id")
    Mono<String> getUserId(String id);

    @Query("DELETE FROM vol_pro WHERE openId = :volunteerOpenId AND projectId = :projectId")
    Mono<Void> deleteVolunteer(String projectId, String volunteerOpenId);

    @Query("SELECT openId FROM volunteer WHERE openId NOT IN (SELECT openId FROM vol_pro WHERE projectId = :projectId) AND role != 3 AND role != 2")
    Flux<VolunteerDTO1> getVolunteer1(String projectId);

    @Query("SELECT mail FROM volunteer WHERE openId = :openId")
    Mono<String> getMail(String openId);

    @Query("SELECT IFNULL(SUM(workTime), 0) FROM vol_pro WHERE openId = :openId")
    Mono<Integer> getWorkTime(String openId);

    @Query("SELECT DISTINCT vp.openId FROM vol_pro vp JOIN volunteer v ON vp.openId = v.openId WHERE vp.projectId = :projectId AND v.role <> 3")
    Flux<VolunteerDTO2> getVolunteer2(String projectId);

    @Query("SELECT userId, mail, phone, avatarUrl FROM volunteer WHERE openId = :openId")
    Mono<VolunteerDTO> getVolunteer2Part(String openId);

    @Query("SELECT IFNULL(SUM(checkIns), 0) FROM vol_pro WHERE openId = :openId")
    Mono<Integer> getCheckIns(String openId);

    @Query("INSERT INTO volunteer_applications (managerOpenId, volunteerOpenId) VALUES (:managerOpenId, :volunteerOpenId)")
    Mono<Void> recruitVolunteers(String managerOpenId, String volunteerOpenId);

    @Query("SELECT userId, mail, shareTimes, openId, phone FROM volunteer WHERE role != 3")
    Flux<VolunteerDTO4> getVolunteer4();

    @Query("SELECT p.projectName FROM project p LEFT JOIN vol_pro vp ON p.projectId = vp.projectId WHERE vp.openId = :openId")
    Flux<String> getProject(String openId);

    @Query("SELECT openId, projectName FROM project")
    Flux<Admin> getAllAdmins();

    @Query("SELECT openId, userId FROM volunteer WHERE role != 3")
    Flux<VolunteerDTO5> getAllVolunteers();

    @Query("SELECT description FROM project WHERE projectId = :projectId")
    Mono<String> getDescription(String projectId);

    @Query("SELECT DISTINCT openId FROM vol_pro WHERE projectId = :projectId")
    Flux<String> getVolunteerOpenIds6(String projectId);

    @Query("SELECT userId, phone, mail FROM volunteer WHERE openId = :id")
    Mono<VolunteerDTO6> getVolunteer6(String id);

    @Query("INSERT INTO vol_pro (openId, projectId) VALUES (:openId, :projectId)")
    Mono<Void> addVolPro(String openId, String projectId);

    @Query("SELECT projectId FROM vol_pro WHERE openId = :openId")
    Flux<String> getProjectIds(String openId);

    @Query("SELECT role, userId, mail, phone, avatarUrl FROM volunteer WHERE openId = :openId")
    Mono<VolunteerDTO7> getVolunteer7(String openId);

    @Query("SELECT userId, mail, shareTimes, phone, avatarUrl FROM volunteer WHERE openId = :openId")
    Mono<PersonalInfo> getPersonalInfo(String openId);

    @Query("SELECT uuid FROM sign_vol WHERE openId = :openId AND mark = 1")
    Flux<String> getUUIDs(String id);

    @Query("SELECT name FROM signin WHERE uuid = :uuid")
    Mono<String> getSignInName(String uuid);

    @Query("SELECT name, address, duration FROM signin WHERE uuid = :uuid")
    Mono<SignInDTO2> getSignInDTO2(String uuid);

    @Query("SELECT checkInTime FROM sign_vol WHERE uuid = :uuid")
    Mono<Long> getTimeStamp(String uuid);
}
