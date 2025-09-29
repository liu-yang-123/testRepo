package com.zcxd.base.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import com.alibaba.fastjson.JSONObject;
import com.zcxd.base.service.SysUserService;
import com.zcxd.common.util.TokenUtils;
import com.zcxd.db.model.SysUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserInterceptor implements HandlerInterceptor {

    @Autowired
    private SysUserService userService;
	
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从网关获取并校验,通过校验就可信任x-client-token-user中的信息
        String token=request.getHeader("X-Token");
        if (token!=null) {
        	int userId = TokenUtils.getTokenUserId(token);
//        	SysUser user = userService.getById(userId);
        	SysUser user = userService.getUserById((long)userId);
            UserContextHolder.getInstance().setContext((JSONObject)JSONObject.toJSON(user));
        }
        return true;
    }

    private void checkToken(String token) {
        //TODO 从网关获取并校验,通过校验就可信任token中的信息
        log.debug("//TODO 校验token:{}", token);

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        UserContextHolder.getInstance().clear();
    }
}