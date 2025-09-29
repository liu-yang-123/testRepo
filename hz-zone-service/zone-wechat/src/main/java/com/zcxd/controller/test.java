package com.zcxd.controller;

import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author lilanglang
 * @date 2021-10-14 11:19
 */
@RestController
@RequestMapping("/test")
public class test {
    //微信配置服务器 验证
    @RequestMapping(value="/wxserver",method={RequestMethod.GET})
    public  void check(HttpServletRequest request, HttpServletResponse response)  {
        //微信服务器get传递的参数
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");

        //微信工具服务类
        WxMpService wxService=new WxMpServiceImpl();
        //注入token的配置参数
        /**
         * 生产环境 建议将WxMpInMemoryConfigStorage持久化
         */
        WxMpInMemoryConfigStorage wxConfigProvider=new WxMpInMemoryConfigStorage();
        //注入token值
        wxConfigProvider.setToken("finservice");
        wxService.setWxMpConfigStorage(wxConfigProvider);
        boolean flag=wxService.checkSignature(timestamp, nonce, signature); //验证token跟微信配置的是否一样
        PrintWriter out= null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(flag){
            out.print(echostr);
        }
        out.close();
    }
}