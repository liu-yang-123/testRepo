package com.zcxd.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zcxd.db.model.Employee;
import com.zcxd.domain.UserInfoWechat;
import com.zcxd.service.EmployeeService;
import com.zcxd.utils.AccessToken;
import com.zcxd.utils.Message;
import com.zcxd.utils.WechatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.zcxd.utils.AuthUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author lilanglang
 */
@Controller
public class WxRedirectController {

    @Autowired
    private EmployeeService employeeService;
    @Value("${wx.mp.configs.appId}")
    private String appId;
    @Value("${wx.mp.configs.secret}")
    private String secret;
    @Value("${wx.mp.configs.notify_url}")
    private String notifyUrl;
    @Value("${wx.mp.configs.host}")
    private String host;

    /**
     * @Description:授权询问接口
     * @Author: lilanglang
     * @Date: 2021/7/15 17:16
     * @param request:
     * @param response:
     **/
    @RequestMapping("/weixinAutoLogin")
    public void weixinAutoLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        /**
         * 这儿一定要注意！！首尾不能有多的空格（因为直接复制往往会多出空格），其次就是参数的顺序不能变动
         **/
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appId + "&redirect_uri="
                + URLEncoder.encode(notifyUrl, "UTF-8") + "&response_type=code" + "&scope=snsapi_userinfo"
                + "&state=STATE#wechat_redirect";
        response.sendRedirect(url);
    }



    /**
     * @Description:获取accessToken
     * @Author: lilanglang
     * @Date: 2021/7/15 17:16
     **/
    @RequestMapping("/getAccessToken")
    @ResponseBody
    public String getAccessToken(){
        AccessToken accessToken = WechatUtil.getAccessToken(appId, secret);
        return JSON.toJSONString(Message.getSuccessfulRes(accessToken.getAccessToken()));
    }

    /**
     * @Description:授权绑定回调事件
     * @Author: lilanglang
     * @Date: 2021/7/13 14:28
     * @param request:
     * @param response:
     * @param model:
     * @param map:
     **/
    @RequestMapping("/weixinComeback")
    public String eixinComeback(HttpServletRequest request, HttpServletResponse response, Model model,
                                HashMap<String, Object> map) throws IOException {

        response.setHeader("Access-Control-Allow-Origin", "*");

        String code = request.getParameter("code");
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appId +
                "&secret=" + secret + "&code=" + code +
                "&grant_type=authorization_code";
        JSONObject jsonObject = AuthUtil.doGetJson(url);
        String openid = jsonObject.getString("openid");
        String token = jsonObject.getString("access_token");
        System.out.println("token=" + token);
        String infoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + token + "&openid=" + openid
                + "&lang=zh_CN";
        JSONObject userInfo = AuthUtil.doGetJson(infoUrl);
        System.out.println("userInfo:" + userInfo);
        UserInfoWechat ui = new UserInfoWechat();
        ui.setUserAvatar(String.valueOf(userInfo.get("headimgurl")));
        ui.setUserCity(String.valueOf(userInfo.get("city")));
        ui.setUserCountry(String.valueOf(userInfo.get("country")));
        ui.setUserCreateTime(new Date());
        ui.setUserIsDistributor(0);
        ui.setUserNickname(String.valueOf(userInfo.get("nickname")));
        ui.setUserOpenid(openid);
        System.out.println("openid=" + openid);
        if (userInfo.get("sex") != null) {
            ui.setUserSex(Integer.parseInt(String.valueOf(userInfo.get("sex"))));
        }
        ui.setUserProvince(String.valueOf(userInfo.get("province")));

        map.put("headimgurl", ui.getUserAvatar());
        map.put("city", ui.getUserCity());
        map.put("country", ui.getUserCountry());
        map.put("nickname", ui.getUserNickname());
        map.put("sex", ui.getUserSex());
        map.put("province", ui.getUserProvince());
        map.put("openid", ui.getUserOpenid());

        //查询是否已绑定openid,如果是，返回已绑定信息
        List<Employee> employeeList=employeeService.listByOpenId(ui.getUserOpenid());
        if(employeeList.size()>0){
            return "already_login";
        }
        // 用户信息存放到session里面
        request.getSession().setAttribute("userInfo", ui);

        return "login";

    }




}
