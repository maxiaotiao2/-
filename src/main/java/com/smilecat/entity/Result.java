package com.smilecat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private Integer code;//响应码，1 代表成功; 0 代表失败
    private String msg;  //响应信息 描述字符串
    private Object data; //返回的数据

    //增删改 成功响应
    public static Result success(){
        return new Result(1,"success",null);
    }
    //查询 成功响应
    public static Result success(Object data){
        return new Result(1,"success",data);
    }

    public static <T> Mono<Result> success(Flux<T> dataFlux) {
        return dataFlux
                .collectList()
                .map(data -> new Result(1, "Success", data));
    }

    public static <T> Mono<Result> success(Mono<T> dataFlux) {
        return dataFlux
                .map(data -> new Result(1, "Success", data));
    }


    //失败响应
    public static Result error(String msg){
        return new Result(0,msg,null);
    }
}
