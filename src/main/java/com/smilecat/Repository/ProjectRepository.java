package com.smilecat.Repository;

import com.smilecat.DTO.ProjectDTO;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.smilecat.entity.File;
import com.smilecat.entity.Project;
import com.smilecat.entity.SignInInfo;
import com.smilecat.DTO.ProjectDTO1;
import com.smilecat.DTO.ProjectDTO2;
import com.smilecat.DTO.ProjectDTO3;

import java.util.List;

public interface ProjectRepository extends ReactiveCrudRepository<Project, String> {

    @Query("INSERT INTO project (projectId, openId, date, endDate, startDate, projectName, organization, description, status, location) VALUES (:projectId, :openId, :date, :endDate, :startDate, :projectName, :organization, :description, :status, :location)")
    Mono<Void> applyVolunteerProject(Project p);

    @Query("INSERT INTO file (name, url, projectId) VALUES (:file.name, :file.url, :projectId)")
    Mono<Void> insertFile(File file, String projectId);

    @Query("UPDATE project SET status = 2 WHERE projectId = :projectId")
    Mono<Void> agreeProject(String projectId);

    @Query("UPDATE project SET status = 3 WHERE projectId = :projectId")
    Mono<Void> rejectProject(String projectId);

    @Query("SELECT projectId, openId, date, endDate, startDate, projectName, organization, status, location FROM project WHERE status = 1")
    Flux<Project> getPendingProjects();

    @Query("SELECT name, url FROM file WHERE projectId = :projectId")
    Flux<File> getFiles(String projectId);

    @Query("SELECT projectName, location, date, volunteers FROM project WHERE openId = :openId")
    Mono<ProjectDTO1> getProjectStatic(String openId);

    @Query("SELECT projectId FROM project WHERE openId = :openId")
    Mono<String> getProjectId(String openId);

    @Query("SELECT uuid, total, absenteeCount, rate, location, name FROM signininfo WHERE projectId = :projectId")
    Flux<SignInInfo> getSignInInfos(String projectId);

    @Query("SELECT projectName, date, location, volunteers, status, projectId, openId, description FROM project WHERE status = 4")
    Flux<ProjectDTO2> getOpenProjects();

    @Query("SELECT userId FROM volunteer WHERE openId = :openId")
    Mono<String> getAdmin(String openId);

    @Query("SELECT mail FROM volunteer WHERE openId = :openId")
    Mono<String> getMail(String openId);

    @Query("SELECT COUNT(*) FROM signin WHERE openId = :openId")
    Mono<Integer> getSignIns(String openId);

    @Query("SELECT IFNULL(SUM(workTime), 0) FROM vol_pro WHERE projectId = :projectId")
    Mono<Integer> getTotalDuration(String projectId);

    @Query("UPDATE project SET status = 4 WHERE openId = :openId")
    Mono<Void> publishProject(String openId);

    @Query("SELECT uuid FROM signin WHERE openId = :openId")
    Flux<String> getUUIDs(String openId);

    @Query("SELECT projectName, date, location, volunteers, status, projectId, openId FROM project")
    Flux<ProjectDTO3> getProjects();

    @Query("UPDATE project SET volunteers = volunteers + 1")
    Mono<Void> addVolunteers();

    @Query(" SELECT\n" +
            "            vp.projectId as projectId ,\n" +
            "            vp.checkIns as checkIns,\n" +
            "            vp.workTime as workTime,\n" +
            "            p.projectName as projectName,\n" +
            "            p.organization as organization,\n" +
            "            p.startDate as startDate,\n" +
            "            p.endDate as endDate,\n" +
            "            p.location as location,\n" +
            "            p.status as status,\n" +
            "            p.openId as openId,\n" +
            "            p.description as description,\n" +
            "            p.date as date\n" +
            "        FROM\n" +
            "            vol_pro vp\n" +
            "                INNER JOIN\n" +
            "            project p ON vp.projectId = p.projectId\n" +
            "        WHERE\n" +
            "            vp.openId = :openId;")
    Flux<ProjectDTO> getProjectAdd(String openId);
}
