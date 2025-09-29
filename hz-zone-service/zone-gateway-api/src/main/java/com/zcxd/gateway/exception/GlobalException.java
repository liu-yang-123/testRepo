package com.zcxd.gateway.exception;

import com.alibaba.fastjson.JSONObject;
import com.zcxd.common.util.Result;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关全局异常处理类
 * @author songanwei
 * @date 2021/4/15 16:34
 */
@Component
@Order(-1)
@Slf4j
public class GlobalException implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {
        ServerHttpResponse response = serverWebExchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(throwable);
        }
        String message = "";
        // response header头部设置
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        if (throwable instanceof ResponseStatusException) {
            response.setStatusCode(((ResponseStatusException) throwable).getStatus());
            message = "系统服务异常";
        }
        if (throwable instanceof FeignException){
            message = "服务调用异常";
        }
        // response写入数据流
      return getResponse(response,message);
    }

    private Mono<Void> getResponse(ServerHttpResponse response, String message){
        return response
                .writeWith(Mono.fromSupplier(() -> {
                    DataBufferFactory bufferFactory = response.bufferFactory();
                    return bufferFactory.wrap(JSONObject.toJSONString(Result.fail(message)).getBytes());
                }));
    }
}
