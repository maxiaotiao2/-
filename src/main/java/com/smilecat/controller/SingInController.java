package com.smilecat.controller;

import com.smilecat.DTO.SignInDTO;
import com.smilecat.DTO.SignInDTO1;
import com.smilecat.entity.Result;
import com.smilecat.entity.SignIn;
import com.smilecat.service.SignInService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
public class SingInController {
    @Autowired
    private SignInService signInService;

    @PostMapping("/publishSignIn")
    public Mono<Result> publishSignIn(@RequestBody SignIn s) {
        log.info(s.toString());
        return Result.success(signInService.publishSignIn(s));
    }

    @GetMapping("/signIn")
    public Mono<Result> signIn(String openId, String uuid) {
        log.info("openId:{}", openId);
        log.info("uuid:{}", uuid);
        return Result.success(signInService.signIn(openId, uuid));
    }

    @GetMapping("/getSignIn")
    public Mono<Result> getSignIn(String openId)
    {
        return Result.success(signInService.getSignIn(openId));
    }

    @GetMapping("/getSignInValid")
    public Mono<Result> getSignInValid(String openId)
    {
        log.info("openId:{}",openId);
        return Result.success(signInService.getValidUUIDs(openId).collectList());
    }

    @GetMapping("/isIn")
    public Mono<Result> isIn(String openId,String uuid)
    {
        log.info("isIn,openId{}",openId);
        log.info("uuid{}",uuid);
        return Result.success(signInService.getIsIn(openId,uuid));
    }

}
