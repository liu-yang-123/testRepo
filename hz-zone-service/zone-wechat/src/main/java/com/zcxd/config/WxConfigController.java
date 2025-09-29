package com.zcxd.config;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/")
@Slf4j
public class WxConfigController {


    @RequestMapping({ "MP_verify_7MyZxxxxxxv.txt" })
    private void returnConfigFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.debug("=============MP_verify_7Myxxxxxx.txt  begin================");
        // 将请求、响应的编码均设置为UTF-8（防止中文乱码）
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String echostr = "VJ5m2EsA0G7VuO0I";
        // 通过检验signature 对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        out.print(echostr);
        out.flush();
        out.close();
        out = null;
        log.debug("=============MP_verify_7MayZxxxxx.txt  end================");
    }
}

