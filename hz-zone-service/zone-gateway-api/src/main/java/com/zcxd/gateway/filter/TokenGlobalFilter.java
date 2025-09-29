package com.zcxd.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.TokenUtils;
import com.zcxd.gateway.provider.BaseProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

/**
 * token验证过滤器类
 * @author songanwei
 * @date 2021/4/15 16:19
 */
@Order(3)
@Component
public class TokenGlobalFilter implements GlobalFilter {

    @Autowired
    private BaseProvider baseProvider;

    /**
     * Http header鉴权字段
     */
    private static final String X_CLIENT_TOKEN = "X-Token";

    /**
     * 无需鉴权请求url
     */
    private static final List<String> NO_AUTH_URL_LIST = Arrays.asList("/pda/login","/base/auth/login","/base/auth/info",
            "/base/v2/api-docs","/pda/v2/api-docs","/pda/user/login/account","/pda/user/login/print",
            "/pda/user/print/list","/pda/user/login/esort","/pda/user/print/lasttime","/pda/app/check/version","/pda/collectSend/bank/info");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authentication = request.getHeaders().getFirst(X_CLIENT_TOKEN);
        //获取请求URL
        LinkedHashSet<URI> uris = (LinkedHashSet)exchange.getRequiredAttribute(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
        Optional<URI> target = uris.stream().filter(r -> r.toString().startsWith("lb:/")).findFirst();
        String oldPath = request.getURI().getPath();
        String url = target.isPresent() ? "/" + target.get().getHost() + target.get().getPath() : oldPath;
        if(url.contains("/wechat/")){
            //微信端不验证token
            return chain.filter(exchange);
        }
        if (NO_AUTH_URL_LIST.contains(url)) {
            return chain.filter(exchange);
        }
        if(authentication != null){
            //验证token是否有效、过期
            int userId = 0;
            try {
                userId = TokenUtils.getTokenUserId(authentication);
            }catch (Exception e){
                return unauthorized(exchange);
            }
            if (userId == 0){
                return unauthorized(exchange);
            }
            Route route = (Route)exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
            URI routeUri = route.getUri();
            String routePath =routeUri.toString();
            if ("lb://base".equals(routePath)) {
                //调用base服务系统验证用户权限数据并且将/base/user/deleted/123 转化为 /base/user/deleted格式
                String formatUrl = url != null ? url.replaceAll("/\\d+", "") : "";
                Result result = baseProvider.checkPermission(userId, formatUrl);
                if (result.getCode() != 0){
                    return noPermission(exchange);
                }
            }
            return chain.filter(exchange);
        }
        return unauthorized(exchange);
    }

    /**
     * 网关拒绝，返回401
     * @param
     */
    private Mono<Void> unauthorized(ServerWebExchange serverWebExchange) {
        ServerHttpResponse response = serverWebExchange.getResponse();
        response.getHeaders().add("Content-type", "application/json;charset=UTF-8");
        DataBuffer buffer = response.bufferFactory().wrap(JSONObject.toJSONString(Result.Tokenfail()).getBytes());
        return serverWebExchange.getResponse().writeWith(Flux.just(buffer));
    }

    /**
     * 无权限访问数据
     * @param serverWebExchange
     * @return
     */
    private Mono<Void> noPermission(ServerWebExchange serverWebExchange){
        ServerHttpResponse response = serverWebExchange.getResponse();
        response.getHeaders().add("Content-type", "application/json;charset=UTF-8");
        DataBuffer buffer = response.bufferFactory().wrap(JSONObject.toJSONString(Result.fail("没有权限访问数据")).getBytes());
        return serverWebExchange.getResponse().writeWith(Flux.just(buffer));
    }
}
