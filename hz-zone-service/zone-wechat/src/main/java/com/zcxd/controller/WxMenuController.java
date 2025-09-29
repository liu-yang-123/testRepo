package com.zcxd.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.zcxd.common.constant.JobTypeEnum;
import com.zcxd.db.model.Employee;
import com.zcxd.db.model.EmployeeJob;
import com.zcxd.db.model.MessagePush;
import com.zcxd.db.model.SchdResult;
import com.zcxd.domain.Button;
import com.zcxd.domain.WechatMenu;
import com.zcxd.dto.SchdResultDTO;
import com.zcxd.dto.SelectBean;
import com.zcxd.service.EmployeeJobService;
import com.zcxd.service.EmployeeService;
import com.zcxd.service.MessagePushService;
import com.zcxd.service.SchdResultService;
import com.zcxd.utils.AuthUtil;
import com.zcxd.utils.Message;
import com.zcxd.utils.WechatUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单控制层
 */
@Controller
public class WxMenuController {

    @Value("${wx.mp.configs.appId}")
    private String appId;
    @Value("${wx.mp.configs.secret}")
    private String secret;
    @Value("${wx.mp.configs.notify_url}")
    private String notifyUrl;
    @Value("${wx.mp.configs.host}")
    private String host;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private EmployeeJobService employeeJobService;
    @Autowired
    private SchdResultService schdResultService;
    @Autowired
    private MessagePushService messagePushService;

    private static final String STATE_CASHOUT = "cashOut";


    /**
     * @Description:自定义菜单
     * @Author: lilanglang
     * @Date: 2021/7/15 17:09

     **/
    @RequestMapping("/createMenu")
    @ResponseBody
    public String createMenu() throws UnsupportedEncodingException {
        WechatMenu wechatMenu = new WechatMenu();
        List<Button> buttonList = new ArrayList<>();
        Button button1 = new Button();
        button1.setName("排班列表");
        List<Button> subButtonList = new ArrayList<>();
        Button subButton1 = new Button();
        subButton1.setName("排班信息");
        subButton1.setType("view");
        subButton1.setKey("key1");
        subButton1.setUrl(host+"redirecttocashout");
        subButtonList.add(subButton1);
        button1.setSub_button(subButtonList );
        buttonList.add(button1);

        Button button2 = new Button();
        subButtonList = new ArrayList<>();
        button2.setName("我的");
        Button subButton2 = new Button();
        subButton2.setName("帐号绑定");
        subButton2.setType("view");
        subButton2.setKey("key2");
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appId + "&redirect_uri="
                + URLEncoder.encode(notifyUrl, "UTF-8") + "&response_type=code" + "&scope=snsapi_userinfo"
                + "&state=STATE#wechat_redirect";
        subButton2.setUrl(url);
        subButtonList.add(subButton2);
        button2.setSub_button(subButtonList);
        buttonList.add(button2);

        wechatMenu.setButton(buttonList);
        System.out.println("菜单"+ JSON.toJSONString(wechatMenu));
        Integer res = WechatUtil.createMenu(appId, secret, wechatMenu);
        return JSON.toJSONString(Message.getSuccessfulRes(res));
    }

    /**
     * @Description:访问主菜单
     * @Author: lilanglang
     * @Date: 2021/10/20 15:09
     **/
    @RequestMapping("/menu")
    public String menu() {
        return "menu";
    }



    /**
     * @Description:排班信息跳转链接
     * @Author: lilanglang
     * @Date: 2021/7/16 9:49
     **/
    @RequestMapping("/redirecttocashout")
    public void redirectToCashout(HttpServletResponse response) throws IOException{
        response.setHeader("Access-Control-Allow-Origin", "*");
        String url= "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
                + appId + "&redirect_uri=" + host
                + "weixinoauth&response_type=code&scope=snsapi_base&state=" + STATE_CASHOUT + "#wechat_redirect";
        response.sendRedirect(url);
    }

    /**
     * @Description:获取用户opeinid
     * @Author: lilanglang
     * @Date: 2021/7/16 9:52
     * @param code:
     * @param state:
     **/
    @RequestMapping("/weixinoauth")
    public String weixinOauth(String code, String state, HttpServletRequest request) throws IOException {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                + appId + "&secret=" + secret + "&code=" + code + "&grant_type=authorization_code";
        JSONObject jsonObject = AuthUtil.doGetJson(url);
        String openid = jsonObject.getString("openid");
        String redirect = "";
        //1、查询该用户是否已绑定帐号
        List<Employee> empList= employeeService.listByOpenId(openid);
        if(empList.size()<1){
           return "un_binding";
        }

        switch (state){
            case STATE_CASHOUT:
                //获取该人员的岗位信息
                Map<Long,List<EmployeeJob>> jobMap=employeeJobService.list().stream().collect(Collectors.groupingBy(EmployeeJob::getId));
                Integer jobType=jobMap.get(empList.get(0).getJobIds()).get(0).getJobType();
                //TODO 获取有效排班数据（大于等于两天前的排班）
//                List<SchdResult> schdResultList=schdResultService.listSchdResult(jobType,empList.get(0).getId());
                  List<MessagePush> list=messagePushService.listMessagePush(jobType,MessagePush.ROUTE,empList.get(0).getId());
                //TODO 根据排班记录条数返回不同页面
                if(list.size()>1){
//                    List<SchdResultDTO> dtoList= schdResultService.getDtoList(null);
                    List<MessagePush> pushList = messagePushService.getByList(list,jobType);
                    List<SelectBean> strList=getStrList(pushList);
                    Gson gson = new Gson();
                    String resultStr=gson.toJson(strList);
                    request.getSession().setAttribute("schdResultList",gson.toJson(pushList));
                    request.getSession().setAttribute("selectList",resultStr);
                    request.getSession().setAttribute("defaultDate",strList.get(0).getValue());
                    return "schd_result_list";
                }else if(list.size()==1){
                    MessagePush messagePush=messagePushService.getByList(list,jobType).get(0);
                    request.getSession().setAttribute("messagePush", messagePush);
                    redirect="schdResultView";
                }else{
                    redirect="un_schdResult";
                }
                break;
        }
        return  redirect;
    }

    private List<SelectBean> getStrList(List<MessagePush> pushList) {
        List<SelectBean> strList=pushList.stream().map(item->{
            SelectBean selectBean=new SelectBean();
            selectBean.setTitle(item.getRouteDate());
            selectBean.setValue(item.getId().toString());
            return selectBean;
        }).collect(Collectors.toList());
        return strList;
    }


    @RequestMapping("/unBinding")
    public String unBinding(){
        return "un_binding";
    }

    @RequestMapping("/login")
    public String login(){
        return "login";
    }


}
