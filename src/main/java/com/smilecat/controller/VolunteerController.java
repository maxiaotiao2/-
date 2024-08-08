package com.smilecat.controller;

import com.smilecat.DTO.*;
import com.smilecat.entity.Result;
import com.smilecat.entity.Volunteer;
import com.smilecat.service.VolunteerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class VolunteerController {

    @Autowired
    private VolunteerService volService;


    @GetMapping("/isRegister")
    public Mono<Result> checkRegistration(String openId){
        log.info("查询用户" + openId + "是否已经注册");

        return volService.checkRegistration(openId)
                .flatMap(isRegistered -> {
                    if (isRegistered == 1) {
                        return volService.getVolunteer7(openId)
                                .map(Result::success);
                    } else {
                        Map<String, Integer> result = new HashMap<>();
                        result.put("role", 0);
                        return Mono.just(Result.success(result));
                    }
                });
    }

    @PostMapping("/register")
    public Mono<Result> registerVolunteer(@RequestBody Volunteer v){
        log.info(v.toString());
        return volService.registerVolunteer(v)
                .then(Mono.just(Result.success()));
    }

    @GetMapping("/getVolunteer_3")
    public Mono<Result> getVolunteer3(String openId)
    {
        log.info(openId);
        return Result.success(volService.getVolunteer3(openId));
    }

    @GetMapping("/deleteVolunteer")
    public Result deleteVolunteer(String managerOpenId, String volunteerOpenId)
    {
        log.info("deleteVolunteer");
        log.info("managerOpenId:{},volunteerOpenId:{}",managerOpenId,volunteerOpenId);
        volService.deleteVolunteer(managerOpenId,volunteerOpenId);
        return Result.success();
    }

    @GetMapping("/getVolunteer_1")
    public Mono<Result> getVolunteer1(String openId)
    {
        return Result.success(volService.getVolunteer1(openId));
    }

    @GetMapping("/getVolunteer_2")
    public Mono<Result> getVolunteer2(String openId)
    {
        log.info("openId:{}",openId);
        return Result.success(volService.getVolunteer2(openId));
    }

    @GetMapping ("/recruite")
    public Result recruitVolunteers(String managerOpenId, String volunteerOpenId)
    {
        volService.recruitVolunteers(managerOpenId, volunteerOpenId);
        log.info("招募志愿者");
        log.info("managerOpenId:{},volunteerOpenId:{}",managerOpenId,volunteerOpenId);
        return Result.success();
    }

    @GetMapping("/getVolunteer_4")
    public Mono<Result> getVolunteer4()
    {
        log.info("getVolunteer_4");
        return Result.success(volService.getVolunteer4());
    }

    //获取所有管理员
    @GetMapping("/getAllAdmins")
    public Mono<Result> getAllAdmins()
    {
        return  Result.success(volService.getAllAdmins());
    }

    //获取所有志愿者
    @GetMapping("/getAllVolunteers")
    public Mono<Result> getAllVolunteers()
    {
        return Result.success(volService.getAllVolunteers());
    }

    //志愿者数据导出
    @GetMapping("/volunteerDataDerive")
    public Mono<Void> volunteerDataDerive(@RequestParam String openId, ServerHttpResponse response) {
        return volService.volunteerDataDerive(openId)
                .flatMap(wb -> {
                    String fileName = "志愿者信息.xlsx";
                    try {
                        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
                    } catch (UnsupportedEncodingException e) {
                        return Mono.error(e);
                    }

                    response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName);
                    response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);

                    return Mono.fromSupplier(() -> {
                        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                            wb.write(baos);
                            byte[] bytes = baos.toByteArray();
                            DataBuffer buffer = new DefaultDataBufferFactory().wrap(bytes);
                            return buffer;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).flatMap(dataBuffer -> response.writeWith(Mono.just(dataBuffer)));
                });
    }

//    @GetMapping("/test")
//    public Result test(String param1, String param2)
//    {
//        log.info("test");
//        log.info(param1);
//        log.info(param2);
//        Test test = new Test(param1, param2);
//        return Result.success(test);
//    }

    @GetMapping("/getPersonal")
    public Mono<Result> getPersonal(String openId)
    {
        return Result.success(volService.getPersonalInfo(openId));
    }

}
