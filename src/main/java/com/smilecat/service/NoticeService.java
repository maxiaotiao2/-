package com.smilecat.service;

import com.smilecat.DTO.NoticeDTO;
import com.smilecat.DTO.NoticeDTO1;
import com.smilecat.entity.Notice;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface NoticeService {
    Mono<Void> publishNotice(Notice notice);

    Flux<NoticeDTO> getNotice(String openId);

    Mono<Void> read(String openId, String uuid);

    Mono<Void> applyJoin(NoticeDTO1 noticeDTO1);

    Mono<Void> agree(String openId, String uuid, int role);

    Mono<Void> reject(String uuid);
}
