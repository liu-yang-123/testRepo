package com.zcxd.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.zcxd.common.util.Result;
import com.zcxd.gateway.provider.BaseProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * 用户请求base服务系统访问白名单过滤器
 * @author songanwei
 * @date 2021/4/15 16:27
 */
@Slf4j
@Order(2)
@Component
public class WhiteListFilter implements GlobalFilter {

    @Autowired
    private BaseProvider baseProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        //白名单过滤
        Route route = (Route)exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        URI routeUri = route.getUri();
        String routePath =routeUri.toString();
        if ("lb://base".equals(routePath)){
            String ip = getIpAddress(request);
            log.info("请求地址：" + ip);
            HttpHeaders headers = request.getHeaders();
            String macStr = headers.getFirst("X-mac");
            log.info("请求MAC："+ macStr);
            Result result = baseProvider.checkWhiteList(ip,macStr);

            /**
             * 修改过
             */
//            if (result.getCode() != 0){
//                return forbidden(exchange);
//            }
            //feign调用base服务系统接口
//            Result result = baseProvider.findWhiteList();
//            if (result.getCode() != 0){
//                return forbidden(exchange);
//            }
//            List<String> ipList = (List<String>) result.getData();
//            if (!ipList.contains(ip)){
//                return forbidden(exchange);
//            }
        }
        return chain.filter(exchange);
    }

    /**
     * 获取真实ip
     * @param request
     * @return
     */
    private String getIpAddress(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        String ip = headers.getFirst("x-forwarded-for");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip.indexOf(",") != -1) {
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddress().getAddress().getHostAddress();
        }
        return ip;
    }

    /**
     * 网关拒绝，返回401
     * @param
     */
    private Mono<Void> forbidden(ServerWebExchange serverWebExchange) {
        ServerHttpResponse response = serverWebExchange.getResponse();
        response.getHeaders().add("Content-type", "application/json;charset=UTF-8");
        DataBuffer buffer = response.bufferFactory().wrap(JSONObject.toJSONString(Result.fail("用户IP拒绝访问，请联系系统管理员")).getBytes());
        return serverWebExchange.getResponse().writeWith(Flux.just(buffer));
    }

}
