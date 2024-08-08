package com.smilecat.controller;

import com.smilecat.DTO.NoticeDTO;
import com.smilecat.DTO.NoticeDTO1;
import com.smilecat.entity.Notice;
import com.smilecat.entity.Result;
import com.smilecat.service.NoticeService;
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
public class NoticeControlller {

    @Autowired
    private NoticeService noticeService;

    @PostMapping("/publishNotice")
    public Result publishNotice(@RequestBody Notice notice)
    {
        noticeService.publishNotice(notice);
        return  Result.success();
    }

    @GetMapping("/getNotice")
    public Mono<Result> getNotice(String openId)
    {
        log.info("getNotice:");
        log.info("openId:{}",openId);
        return Result.success(noticeService.getNotice(openId));
    }

    @GetMapping("/read")
    public Result read(String openId, String uuid)
    {
        log.info("read");
        log.info("openId:{},uuid:{}",openId,uuid);
        noticeService.read(openId, uuid);
        return Result.success();
    }

    @PostMapping("/applyJoin")
    public Result applyJoin(@RequestBody NoticeDTO1 noticeDTO1)
    {
        log.info("applyJoin");
        log.info(noticeDTO1.toString());
        noticeService.applyJoin(noticeDTO1);
        return Result.success();
    }

    @GetMapping("/agree")
    public Result agree(String openId,String uuid,int role)
    {
        log.info("openid:{}",openId);
        log.info("uuid:{}",uuid);
        log.info("role:{}",role);
        noticeService.agree(openId,uuid,role);
        return Result.success();
    }

    @GetMapping("/reject")
    public Result reject(String openId,String uuid,int role)
    {
        log.info("rejct:");
        log.info("openid:{}",openId);
        log.info("uuid:{}",uuid);
        log.info("role:{}",role);
        noticeService.reject(uuid);
        return Result.success();
    }
}
