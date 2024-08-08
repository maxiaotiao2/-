package com.smilecat.service;

import com.smilecat.DTO.SignInDTO;
import com.smilecat.DTO.SignInDTO1;
import com.smilecat.entity.SignIn;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SignInService {
    Mono<Void> publishSignIn(SignIn s);

    Mono<Void> signIn(String openId, String uuid);

    Flux<List<SignInDTO>> getSignIn(String openId);

    Flux<SignInDTO1> getValidUUIDs (String openId);

    Mono<String> getIsIn(String openId, String uuid);
}
