package com.smilecat.service;

import com.smilecat.DTO.*;
import com.smilecat.entity.Volunteer;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface VolunteerService {
    Mono<Integer> checkRegistration(String openId);

    Mono<Integer> getRole(String openId);

    Mono<Void> registerVolunteer(Volunteer v);

    Flux<VolunteerDTO3> getVolunteer3(String openId);

    Mono<Void> deleteVolunteer(String managerOpenId, String volunteerOpenId);

    Flux<VolunteerDTO1> getVolunteer1(String openId);

    Flux<VolunteerDTO2> getVolunteer2(String openId);

    Mono<Void> recruitVolunteers(String managerOpenId, String volunteerOpenId);

    Flux<VolunteerDTO4> getVolunteer4();

    Flux<Admin> getAllAdmins();

    Flux<VolunteerDTO5> getAllVolunteers();

    Mono<XSSFWorkbook> volunteerDataDerive(String openId);

    Mono<VolunteerDTO7> getVolunteer7(String openId);

    Mono<PersonalInfo> getPersonalInfo(String openId);
}
