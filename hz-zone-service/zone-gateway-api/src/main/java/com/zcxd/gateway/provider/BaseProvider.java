package com.zcxd.gateway.provider;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zcxd.common.util.Result;

@Component
@FeignClient(name = "base", fallback = BaseProvider.BaseProviderFallback.class)
public interface BaseProvider {

    /**
     * 获取用户权限
     * @param userId 用户ID
     * @param url  访问资源URL，带服务节点名
     * @return
     */
    @PostMapping(value = "/auth/checkPermission")
    Result checkPermission(@RequestParam("userId") Integer userId, @RequestParam("url") String url);

    /**
     * 查询白名单信息
     * @param
     */
    @PostMapping(value = "/whiteList/all")
    Result findWhiteList();

    /**
     * 校验白名单
     * @param
     */
    @PostMapping(value = "/whiteList/check")
    Result checkWhiteList(@RequestParam("ip") String ip, @RequestParam(value = "macs",required = false) String macs);

    @Component
    class BaseProviderFallback implements BaseProvider {

        @Override
        public Result checkPermission(Integer userId, String url) {
            return Result.fail();
        }

        @Override
        public Result findWhiteList() {
            return Result.fail();
        }

        @Override
        public Result checkWhiteList(String ip,String macs) {
            return Result.fail();
        }
    }
}
