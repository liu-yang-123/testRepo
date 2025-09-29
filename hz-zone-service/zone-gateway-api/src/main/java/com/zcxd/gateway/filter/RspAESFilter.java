package com.zcxd.gateway.filter;

import com.zcxd.common.util.AESUtils;
import com.zcxd.gateway.wrapper.BodyHandlerFunction;
import com.zcxd.gateway.wrapper.BodyHandlerServerHttpResponseDecorator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import javax.annotation.Resource;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 请求响应加解密过滤器
 * @author shijin
 * @date 2021/4/15 16:28
 */
@Slf4j
@Component
public class RspAESFilter implements GlobalFilter, Ordered {

    @Value("${encrypt.aes}")
    private int aesEncrypt;
    @Value("${encrypt.key}")
    private String aesKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {
        Route route = (Route)exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        URI routeUri = route.getUri();
        String routePath =routeUri.toString();
        String path = exchange.getRequest().getPath().toString();
        ServerHttpResponse response = exchange.getResponse();
        HttpStatus status = response.getStatusCode();

        if (!"lb://pda".equals(routePath)
                || "/pda/v2/api-docs".equals(path)
                || "/pda/app/check/version".equals(path)
                || aesEncrypt == 0
                || HttpStatus.OK.value() != status.value() ) {
            return chain.filter(exchange);
        } else {
            //构建响应拦截处理器
            BodyHandlerFunction bodyHandler = (resp, body) -> Flux.from(body)
                    .map(dataBuffer -> {
                        //响应信息转换为字符串
                        String reqBody = null;
                        try {
                            //dataBuffer 转换为String
                            reqBody = IOUtils
                                    .toString(dataBuffer.asInputStream(), "UTF-8")
                                    .replaceAll(">\\s{1,}<", "><");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return reqBody;
                    }).collect(Collectors.joining()).flatMap(orgBody -> {
                        //根据原有的响应信息加密构建新响应信息并写入到resp中
                        //此处可以根据业务进行组装新的响应信息，
                        //加密
                        String rbody = orgBody;
                        try {
//                            log.info("加密前：" + rbody);
                            Long start = System.currentTimeMillis();
                            rbody = AESUtils.encryptParams(rbody,aesKey);
//                            log.info("加密后：" + rbody);
//                            log.info("加密耗时：" + (System.currentTimeMillis()-start));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        HttpHeaders headers = resp.getHeaders();
                        headers.setContentLength(rbody.length());
                        return resp.writeWith(Flux.just(rbody)
                                .map(bx -> resp.bufferFactory()
                                        .wrap(bx.getBytes())));
                    }).then();

            //构建响应包装类
            BodyHandlerServerHttpResponseDecorator responseDecorator = new BodyHandlerServerHttpResponseDecorator(
                    bodyHandler, exchange.getResponse());

            return chain.filter(exchange.mutate().response(responseDecorator).build());
        }
    }

//    另一种可行方式
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpResponse originalResponse = exchange.getResponse();
//        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
//
//        Route route = (Route)exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
//        URI routeUri = route.getUri();
//        String routePath =routeUri.toString();
//        String path = exchange.getRequest().getPath().toString();
//        ServerHttpResponse response = exchange.getResponse();
//        HttpStatus status = response.getStatusCode();
//
//        if (!"lb://pda".equals(routePath)
//                || "/pda/v2/api-docs".equals(path)
//                || aesEncrypt == 0
//                || HttpStatus.OK.value() != status.value()) {
//            return chain.filter(exchange);
//        }
//
//        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
//            @Override
//            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
//                AtomicReference<String> bodyRef = new AtomicReference<>();
//                if (body instanceof Flux) {
//                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
//
//                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
//
//                        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
//                        DataBuffer join = dataBufferFactory.join(dataBuffers);
//
//                        byte[] content = new byte[join.readableByteCount()];
//
//                        join.read(content);
//                        // 释放掉内存
//                        DataBufferUtils.release(join);
//                        String str = new String(content, Charset.forName("UTF-8"));
//
//                        try {
//                            log.info("加密前：" + str);
//                            Long start = System.currentTimeMillis();
//                            str = AESUtils.encryptParams(str,aesKey);
//                            log.info("加密后：" + str);
//                            log.info("加密耗时：" + (System.currentTimeMillis()-start));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        originalResponse.getHeaders().setContentLength(str.getBytes().length);
////                        log.error("gateway catch service exception error:"+ str);
//
////                        JsonResult result = new JsonResult();
////                        result.setCode(ErrorCode.SYS_EXCEPTION.getCode());
////                        result.setMessage(ErrorCode.SYS_EXCEPTION.getMsg());
//
//                        return bufferFactory.wrap(str.getBytes());
//                    }));
//
//                }
//                // if body is not a flux. never got there.
//                return super.writeWith(body);
//            }
//        };
//        // replace response with decorator
//        return chain.filter(exchange.mutate().response(decoratedResponse).build());
//    }

    @Override
    public int getOrder() {
        //WRITE_RESPONSE_FILTER 之前执行
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1;
    }
}
