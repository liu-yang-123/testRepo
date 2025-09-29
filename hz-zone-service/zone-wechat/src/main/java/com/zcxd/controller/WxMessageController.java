package com.zcxd.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zcxd.common.constant.AtmTaskTypeEnum;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.db.model.*;
import com.zcxd.db.utils.CacheKeysDef;
import com.zcxd.db.utils.RedisUtil;
import com.zcxd.dto.WxNoticeMessage;
import com.zcxd.dto.WxTaskNotice;
import com.zcxd.service.*;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 消息推送控制层
 * @Author: lilanglang
 * @Date: 2021/7/15 11:22
 **/
@Controller
@Slf4j
public class WxMessageController {

    private static final int ADD_TASK = 1;
    private static final int CANCEL_TASK = 2;
    @Autowired
    private SchdResultService schdResultService;
    @Autowired
    private SchdResultRecordService schdResultRecordService;
    @Autowired
    private EmployeeService employeeService;
    @Value("${wx.mp.configs.templateId}")
    private String template;
    @Value("${wx.mp.configs.opeatorTempId}")
    private String opeatorTempId;
    @Value("${wx.mp.configs.host}")
    private String host;
    @Value("${wx.mp.configs.appId}")
    private String appId;
    @Value("${wx.mp.configs.secret}")
    private String secret;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private RouteService routeService;
    @Resource
    private VehicleService vehicleService;
    @Resource
    private MessagePushService messagePushService;
    @Resource
    private AtmTaskService atmTaskService;
    @Resource
    private BankService bankService;
    @Resource
    private AtmDeviceService atmDeviceService;




    /**
     * @Description:每隔20秒钟去redis检查是否存在排班消息推送key
     * @Author: lilanglang
     * @Date: 2021/7/19 14:02
     **/
    @Scheduled(fixedRate = 10000)
    public void init() throws IOException, ParseException{
       //TODO 线路消息推送 人员变动消息推送
        routeNotice();
        operatorNotic();
        checkTskChangeNotice();
    }


    /**
     * @Description:排班线路推送
     * @Author: lilanglang
     * @Date: 2021/10/18 14:00
     **/
    public void routeNotice() throws IOException, ParseException{
        final String redisKey = CacheKeysDef.Scheduling;
        List<String> routeIdList=new ArrayList<>();
        if(redisUtil.hasKey(redisKey)) {
            //获取线路Id信息
            String str = redisUtil.lRightPop(redisKey); //左进右出
            while(str!=null){
                routeIdList.add(str);
                str=redisUtil.lRightPop(redisKey);
            }
        }
        if(routeIdList.size()>0){
            //根据线路Id获取线路信息并推送
            schd_message(routeIdList);
        }

    }

    /**
     * @Description:人员变动消息推送
     * @Author: lilanglang
     * @Date: 2021/10/18 14:15
     **/
    public void operatorNotic() throws IOException, ParseException{
        final String redisKey = CacheKeysDef.SchdChangeQueue;
        List<String> dataList = new ArrayList<>();
        if(redisUtil.hasKey(redisKey)){
            String str = redisUtil.lRightPop(redisKey); //左进右出
            while(str!=null){
                dataList.add(str);
                log.info("人员变动data"+str);
                str=redisUtil.lRightPop(redisKey);
            }
            List<WxNoticeMessage> list =dataList.stream().map(item->{
                JSONObject jsonObject = JSONObject.parseObject(item);
                WxNoticeMessage message=JSONObject.toJavaObject(jsonObject,WxNoticeMessage.class);
                return message;
              }
            ).collect(Collectors.toList());
            list.forEach(message -> {
                //保存推送消息
                MessagePush messagePush=saveMessagePush(message);
                //获取openId
                List<String> openIdList=schdResultService.listOpeatorOpenId(message.getEmpIds());
                openIdList=openIdList.stream().filter(item->!StringUtils.isEmpty(item)).collect(Collectors.toList());
                send_opeatorMsg(messagePush,openIdList);
            });

        }
    }

    public MessagePush saveMessagePush(WxNoticeMessage message){
        MessagePush messagePush=new MessagePush();
        BeanUtils.copyProperties(message,messagePush);
        messagePush.setRouteName(message.getRouteNo());
        messagePush.setCreateTime(DateTimeUtil.convertDateToLong(message.getRouteDate()+" 00:00:00"));
        messagePush.setLpno(message.getVehicleNo());
        messagePush.setType(MessagePush.EMPLOYEE);
        messagePush.setChangeMan(message.getChangeText());
        messagePush.setName(message.getEmpNames());
        messagePushService.save(messagePush);
        return messagePush;
    }





    /**
     * @Description:定时任务推送消息
     * @Author: lilanglang
     * @Date: 2021/7/19 14:48
     * @param recordList:
     **/
    public void schd_message(List<String> recordList) throws IOException, ParseException{
        //TODO 批量获取线路信息
        List<Route> routeList=routeService.listByIds(recordList);
        for (Route item:routeList){
            String date=DateTimeUtil.timeStampMs2Date(item.getRouteDate(),"yyyy-MM-dd");
            Vehicle vehile=vehicleService.getById(item.getVehicleId());
            //TODO 保存要推送的消息
            MessagePush messagePush=new MessagePush();
            BeanUtils.copyProperties(item,messagePush);
            messagePush.setLpno(vehile.getLpno());
            messagePush.setRouteDate(date);
            messagePush.setType(MessagePush.ROUTE);
            messagePush.setCreateTime(item.getRouteDate());
            messagePush.setRouteName(item.getRouteName());
            List<MessagePush> list = new ArrayList<>();
            list.add(messagePush);
            messagePushService.saveOrUpdateBatch(list);
            //分别获取司机、业务员、护卫的openId
            List<String> driverList=schdResultService.getOPenIdlistByDriver(item,1);
            List<String> securityList=schdResultService.getOPenIdlistByDriver(item,2);
            List<String> operList=schdResultService.getOPenIdlistByDriver(item,3);
            //消息推送
            try {
                if(driverList.size()>0){
                    weixinTemplateMessage(driverList,messagePush, 1);
                }
                if(securityList.size()>0){
                    weixinTemplateMessage(securityList,messagePush, 2);
                }
                if(operList.size()>0){
                    weixinTemplateMessage(operList,messagePush, 3);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            log.info("排班消息推送------>成功");
        }

    }



    /**
     * @Description:排班结果消息推送
     * @Author: lilanglang
     * @Date: 2021/7/19 15:02
     * @param openIdList:
     * @param route:
     **/
    public void weixinTemplateMessage(List<String> openIdList,MessagePush route,Integer type)
            throws IOException, ParseException {

        long viewDate=DateTimeUtil.getCurrentTimeStampMs();
        openIdList.forEach(item->{
            if(!StringUtils.isEmpty(item)){
                WxMpInMemoryConfigStorage wxStorage = new WxMpInMemoryConfigStorage();
                wxStorage.setAppId(appId);
                wxStorage.setSecret(secret);
                WxMpService wxMpService = new WxMpServiceImpl();
                wxMpService.setWxMpConfigStorage(wxStorage);
                List<WxMpTemplateData> data = Arrays.asList(
                        new WxMpTemplateData("first", "您有一条排"+route.getRouteDate()+"排班结果提醒"),
                        new WxMpTemplateData("keyword1","排班消息推送"),
                        new WxMpTemplateData("keyword2","线路排班"),
                        new WxMpTemplateData("keyword3",""),
                        new WxMpTemplateData("keyword4",route.getRouteDate()),
                        new WxMpTemplateData("remark","请及时查看!"));
                WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder().toUser(item)//
                        .data(data)
                        .templateId(template)
                        .url(host+"schdResultView?id="+route.getId()+"&date="+viewDate+"&type="+type)
                        .build();
                // 发起推送
                try {
                    String msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
                    log.info("排班结果推送成功：" + msg);
                } catch (Exception e) {
                    log.info("排班结果推送失败：" + e.getMessage());
                    e.printStackTrace();
                }
            }

        });


    }

    /**
     * @Description:人员变动消息推送
     * @Author: lilanglang
     * @Date: 2021/10/18 15:02
     * @param message:
     * @param openIdList:
     **/
    public void send_opeatorMsg(MessagePush message, List<String> openIdList){
        long viewDate=DateTimeUtil.getCurrentTimeStampMs();
        openIdList.forEach(item->{
            WxMpInMemoryConfigStorage wxStorage = new WxMpInMemoryConfigStorage();
            wxStorage.setAppId(appId);
            wxStorage.setSecret(secret);
            WxMpService wxMpService = new WxMpServiceImpl();
            wxMpService.setWxMpConfigStorage(wxStorage);
            List<WxMpTemplateData> data = Arrays.asList(
                    new WxMpTemplateData("first", "您有一条排"+message.getRouteDate()+"人员变动提醒"),
                    new WxMpTemplateData("keyword1","人员变动推送"),
                    new WxMpTemplateData("keyword2","人员变动"),
                    new WxMpTemplateData("keyword3",""),
                    new WxMpTemplateData("keyword4",message.getRouteDate()),
                    new WxMpTemplateData("remark","请及时查看!"));
            WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder().toUser(item)//
                    .data(data)
                    .templateId(opeatorTempId)
                    .url(host+"operatorView?id="+message.getId()+"&date="+viewDate)
                    .build();
            // 发起推送
            try {
                String msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
                log.info("人员调整推送成功：" + msg);
            } catch (Exception e) {
                log.info("人员调整推送失败：" + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void checkTskChangeNotice() {
//        log.info("+++checkTskChangeNotice+++");
        final String redisKey = CacheKeysDef.TaskChangeQueue;
        if (redisUtil.hasKey(redisKey)) {
//            log.info("+++checkTskChangeNotice+++ exist ");
            List<WxTaskNotice> wxTaskNotices = new ArrayList<>();
            while(true) {
                String data = redisUtil.lRightPop(redisKey);
                if (null == data) {
                    break;
                }
                JSONObject jsonObject = JSONObject.parseObject(data);
                WxTaskNotice notice =JSONObject.toJavaObject(jsonObject,WxTaskNotice.class);
                wxTaskNotices.add(notice);
                if (wxTaskNotices.size() == 100) { //一次性最大取100
                    break;
                }
            }

            if(wxTaskNotices.size() > 0) {
                wxTaskNotices.forEach(wxTaskNotice -> {
                    log.info("notice：" + wxTaskNotice.toString());
                });
                processTaskNotice(wxTaskNotices);
            }
        }
    }

    /**
     * @Description:任务变动消息推送
     * @Author: lilanglang
     * @Date: 2021/10/18 15:02
     * @param wxTaskNotices:
     * @param
     **/
    public void processTaskNotice(List<WxTaskNotice> wxTaskNotices){
        if (wxTaskNotices == null || wxTaskNotices.size() == 0) {
            return;
        }
        Set<Long> routeIds = wxTaskNotices.stream().map(WxTaskNotice::getRouteId).collect(Collectors.toSet());
        Set<Long> taskIds = wxTaskNotices.stream().map(WxTaskNotice::getTaskId).collect(Collectors.toSet());
        List<AtmTask> atmTaskList = atmTaskService.listByIds(taskIds);
        Set<Long> bankIds = atmTaskList.stream().map(AtmTask::getSubBankId).collect(Collectors.toSet());
        Map<Long,AtmTask> atmTaskMap = atmTaskList.stream().collect(Collectors.toMap(AtmTask::getId, Function.identity(),(key1,key2)->key2));

        List<Route> routeList = routeService.listByIds(routeIds);
        Map<Long,Route> routeMap = routeList.stream().collect(Collectors.toMap(Route::getId, Function.identity(),(key1,key2)->key2));
        Set<Long> empIds = new HashSet<>();
        for (Route route : routeList) {
            empIds.add(route.getRouteOperMan());
            empIds.add(route.getRouteKeyMan());
        }
        List<Employee> employeeList = employeeService.listByIds(empIds);
        Map<Long,Employee> employeeMap = employeeList.stream().collect(Collectors.toMap(Employee::getId, Function.identity(),(key1,key2)->key2));
        List<Bank> bankList = bankService.listByIds(bankIds);
        Map<Long,Bank> bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId, Function.identity(),(key1,key2)->key2));
        List<AtmDevice> deviceList = atmDeviceService.list();
        Map<Long,AtmDevice> deviceMap = deviceList.stream().collect(Collectors.toMap(AtmDevice::getId, Function.identity(),(key1,key2)->key2));

        for (WxTaskNotice notice : wxTaskNotices) {
            List<String> openIdList = new ArrayList<>();
            Route route = routeMap.get(notice.getRouteId());
            if (route != null) {
                Employee employee = employeeMap.get(route.getRouteOperMan());
                if (employee != null) {
                    openIdList.add(employee.getWxOpenid());
                }
                employee = employeeMap.get(route.getRouteKeyMan());
                if (employee != null) {
                    openIdList.add(employee.getWxOpenid());
                }
            }
            if (openIdList.size() > 0) {
                AtmTask atmTask = atmTaskMap.get(notice.getTaskId());
                if (atmTask != null) {
                    String sTaskType = AtmTaskTypeEnum.getText(atmTask.getTaskType());
                    //TODO 消息入库
                    /**
                     * 查询详情消息格式
                     * 变更类型：新增任务/撤销任务
                     * 任务日期：xxx
                     * 任务网点：
                     * 任务设备：
                     * 加钞金额：xx万元
                     * 备注：
                     */
                    MessagePush messagePush=new MessagePush();
                    messagePush.setRouteDate(notice.getTaskDate());
                    messagePush.setType(MessagePush.TASK);
                    messagePush.setChangeType(notice.getOpType()==ADD_TASK?"新增任务":"撤销任务");
                    messagePush.setOpType(sTaskType);
                    messagePush.setComments(atmTask.getComments());
                    messagePush.setAmount(atmTask.getAmount().divide(new BigDecimal("10000"),BigDecimal.ROUND_CEILING));
                    Bank bank=bankMap.get(atmTask.getSubBankId());
                    if(bank!=null){
                        messagePush.setBank(bank.getFullName());
                    }
                    AtmDevice device = deviceMap.get(atmTask.getAtmId());
                    if(device!=null){
                        messagePush.setDevice(device.getTerNo());
                    }
                    messagePush.setCreateTime(route.getRouteDate());
                    messagePushService.save(messagePush);
                    send_taskChangeMsg(notice.getTaskDate(),notice.getOpType(),sTaskType,openIdList,messagePush.getId());
                }
            }
        }
    }

    /**
     * 开始循环推送
     * 模板：
     * 您有一条xxxx年xx月xx日任务通知
     * 项目名称：撤销任务/新增任务
     * 工作类型: 清机/加钞/维护
     * 工作内容:
     * 日期时间: xxxx年xx月xx日
     * 备注： 清登录app查看
     */
    private void send_taskChangeMsg(String taskDate,int opType,String taskType, List<String> openIdList,Long id){
        long viewDate=DateTimeUtil.getCurrentTimeStampMs();
        openIdList.forEach(item->{
            WxMpInMemoryConfigStorage wxStorage = new WxMpInMemoryConfigStorage();
            wxStorage.setAppId(appId);
            wxStorage.setSecret(secret);
            WxMpService wxMpService = new WxMpServiceImpl();
            wxMpService.setWxMpConfigStorage(wxStorage);
            List<WxMpTemplateData> data = Arrays.asList(
                    new WxMpTemplateData("first", "您有一条"+taskDate+"任务通知"),
                    new WxMpTemplateData("keyword1",opType == ADD_TASK? "新增任务" : "撤销任务"),
                    new WxMpTemplateData("keyword2",taskType),
                    new WxMpTemplateData("keyword3",""),
                    new WxMpTemplateData("keyword4", taskDate),
                    new WxMpTemplateData("remark","请及时登录app处理"));
            WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder().toUser(item)//
                    .data(data)
                    .templateId(opeatorTempId)
                    .url(host+"taskView?id="+id+"&date="+viewDate)
                    .build();
            // 发起推送
            try {
                String msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
                log.info("任务通知推送成功：" + msg);
            } catch (Exception e) {
                log.info("任务通知推送失败：" + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * @Description:排班消息推送详情信息
     * @Author: lilanglang
     * @Date: 2021/10/20 16:01
     * @param id:
     * @param date:
     **/
    @RequestMapping("/schdResultView")
    public String schdResultView(Long id, Long date,Integer type, HttpServletRequest request){
        if(!timeOutFlag(date)){
            return "timesout";
        }
        List<Employee> employeeList=employeeService.list();
        Map employeeMap=employeeList.stream().collect(Collectors.toMap(Employee::getId,Employee::getEmpName));
        MessagePush messagePush=messagePushService.getById(id);
        messagePush.setName(type==1?employeeMap.getOrDefault(messagePush.getDriver(),"").toString():
                (type==2?employeeMap.getOrDefault(messagePush.getSecurityA(),"").toString()+"、"+employeeMap.getOrDefault(messagePush.getSecurityB(),"").toString()
                        :employeeMap.getOrDefault(messagePush.getRouteKeyMan(),"").toString()+"、"+employeeMap.getOrDefault(messagePush.getRouteOperMan(),"").toString()));
        request.getSession().setAttribute("messagePush", messagePush);
        return "schdResultView";
    }

    /**
     * @Description:查看详细消息推送信息
     * @Author: lilanglang
     * @Date: 2021/7/15 15:17
     * @param request:
     * @param id:
     **/
    @RequestMapping("/getSchdResult")
    public String getSchdResult(HttpServletRequest request,Long id,Long date){
        if(timeOutFlag(date)) {
            return "timesout";
        }
        SchdResult schdResult=schdResultService.getById(id);
        List<SchdResult> schdResultList=new ArrayList<>();
        schdResultList.add(schdResult);
        Map<Long,List<Employee>> employeeMap=employeeService.getEmployeeMap();
        request.getSession().setAttribute("schdResult", schdResultService.getDtoList(schdResultList).get(0));
        return "schdResult";
    }

    /**
     * @Description:人员变动详情信息
     * @Author: lilanglang
     * @Date: 2021/10/21 10:19
     * @param id:
     * @param date:
     **/
    @RequestMapping("/operatorView")
    public String operatorView(Long id,long date,HttpServletRequest request){
        if(!timeOutFlag(date)){
            return "timesout";
        }
        MessagePush messagePush=messagePushService.getById(id);
        request.getSession().setAttribute("messagePush", messagePush);
        return "operatorView";
    }

    /**
     * @Description:任务变动详情
     * @Author: lilanglang
     * @Date: 2021/11/9 11:20
     * @param id:
     * @param date:
     * @param request:
     **/
    @RequestMapping("/taskView")
    public String taskView(Long id,long date,HttpServletRequest request){
        if(!timeOutFlag(date)){
            return "timesout";
        }
        MessagePush messagePush=messagePushService.getById(id);
        request.getSession().setAttribute("messagePush", messagePush);
        return "taskView";
    }

    /**
     * @Description:失效时间判断
     * @Author: lilanglang
     * @Date: 2021/10/21 10:22
     * @param date:
     **/
    public boolean timeOutFlag(long date){
        boolean flag=true;
        //超过48小时失效
        Long nowDate =DateTimeUtil.getCurrentTimeStampMs();
        //得到秒
        long seconds=(nowDate-date)/1000/60;
        //超过5小时失效
        if(seconds>=60*48) {
            flag=false;
        }
        return flag;
    }
}