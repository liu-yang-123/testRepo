package com.zcxd.gun.controller;

import com.zcxd.gun.service.server.AttendanceServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zccc
 */
@RestController
@RequestMapping("/attendance")
public class AttendanceController {
    @Autowired
    AttendanceServer attendanceServer;

    /**
     * 连接打印机
     * @param address ip地址
     * @param port 端口
     * @return 结果
     */
    @RequestMapping("/connect")
    public Object connect(@RequestParam("address") String address, @RequestParam("port") Integer port) {
        return attendanceServer.connect(address, port);
    }

    /**
     * 断开打印机
     * @param address ip地址
     * @return 结果
     */
    @RequestMapping("/disconnect")
    public Object disconnect(@RequestParam("address") String address) {
        return attendanceServer.disconnect(address);
    }

    /**
     * 清空打卡数据
     * @param address ip地址
     * @return 结果
     */
    @RequestMapping("/clearLog")
    public Object clearLog(@RequestParam("address") String address, @RequestParam("port") Integer port) {
        return attendanceServer.clearLog(address, port);
    }

    /**
     * 获取员工扫脸数据
     * @param address ip地址
     * @param port 端口
     * @param machineNum 机器号
     * @return 结果
     */
    @RequestMapping("/getEmployeeData")
    public Object getEmployeeData(@RequestParam("address") String address,
                                  @RequestParam("port") Integer port,
                                  @RequestParam("machineNum") Integer machineNum) {
        return attendanceServer.getEmployeeData(address, port, machineNum);
    }
}
