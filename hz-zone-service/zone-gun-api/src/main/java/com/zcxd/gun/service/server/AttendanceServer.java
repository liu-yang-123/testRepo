package com.zcxd.gun.service.server;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.zcxd.common.util.Result;
import com.zcxd.gun.db.model.AttendanceMachine;
import com.zcxd.gun.dto.AttendanceRecordDTO;
import com.zcxd.gun.service.AttendanceMachineService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zccc
 */
@Service
public class AttendanceServer {
    private Map<Integer, AttendanceMachine> machineMap;
    private Map<String, ActiveXComponent> zkems;
    private AttendanceMachineService machineService;

    @Autowired
    public AttendanceServer(AttendanceMachineService machineService) {
        this.machineService = machineService;
        this.machineMap = new HashMap<>();
        this.zkems = new HashMap<>();
    }

    /**
     * 连接考勤机
     * @param address ip地址
     * @param port 端口
     * @return 结果
     */
    public Result connect(String address, Integer port) {
        if (StringUtils.isBlank(address) || port == null || port == 0) {
            return Result.fail("请设置好IP地址或端口");
        }

        // 获取ActiveXComponent
        ActiveXComponent zkem;
        if (zkems.containsKey(address)) {
            zkem = zkems.get(address);
        } else {
            zkem = new ActiveXComponent("zkemkeeper.ZKEM.1");
        }

        // 连接
        boolean result = zkem.invoke("Connect_NET", address, port).getBoolean();
        if (result) {
            zkems.put(address, zkem);
        }
        return result ? Result.success() :Result.fail("连接失败，请检查IP、端口或设备状态");
    }

    /**
     * 断开连接
     * @param address ip地址
     * @return 结果
     */
    public Result disconnect(String address) {
        ActiveXComponent zkem = zkems.get(address);
        if (zkem == null) {
            return Result.fail("请确认IP地址或当前设备已断开连接");
        }
        zkem.invoke("Disconnect");
        zkems.remove(address);
        return Result.success();
    }

    /**
     * 清理考勤记录
     * @param address ip
     * @return 结果
     */
    public Result clearLog(String address, Integer port) {
        Result connectRes = connect(address, port);
        if (connectRes.isFailed()) {
            return connectRes;
        }
        ActiveXComponent zkem = zkems.get(address);
        if (zkem == null) {
            return Result.fail("请确认IP地址或当前设备已断开连接");
        }
        boolean result = zkem.invoke("ClearGLog", 2).getBoolean();
        // 断开打印机
        disconnect(address);
        return result ? Result.success() :Result.fail("清空记录失败");
    }

    /**
     * 获取考勤数据
     * @param address ip地址
     * @param port 端口
     * @param machineNum 机器号
     * @return 结果
     */
    private Result<List<AttendanceRecordDTO>> getLogData(String address, Integer port, Integer machineNum) {
        // 连接打印机
        Result connectRes = connect(address, port);
        if (connectRes.isFailed()) {
            return connectRes;
        }
        ActiveXComponent zkem = zkems.get(address);
        // 读入内存
        Result readNewLogDataRes = readNewLogData(zkem, machineNum);
        if (readNewLogDataRes.isFailed()) {
            return readNewLogDataRes;
        }

        // 解析数据
        List<AttendanceRecordDTO> generalLogData = getGeneralLogData(zkem, machineNum);

        // 断开打印机
        disconnect(address);
        return Result.success(generalLogData);
    }

    /**
     * 筛选刷脸人员
     * @param address ip地址
     * @param port 端口
     * @param machineNum 机器号
     * @return 结果
     */
    public Result getEmployeeData(String address, Integer port, Integer machineNum) {
        Result<List<AttendanceRecordDTO>> result = getLogData(address, port, machineNum);
        if (result.isFailed()) {
            return result;
        }

        List<AttendanceRecordDTO> data = result.getData();
        List<String> enrollNumbers = data.stream().map(AttendanceRecordDTO::getEnrollNumber).distinct().collect(Collectors.toList());
        return Result.success(enrollNumbers);
    }

    /**
     * 读取考勤记录到内存
     * @param zkem 考勤机连接
     * @return 结果
     */
    private Result readNewLogData(ActiveXComponent zkem, Integer machineNum) {
        boolean result = zkem.invoke("ReadNewGLogData", machineNum).getBoolean();
        return result ? Result.success() :Result.fail("未查找到数据");
    }

    /**
     * 读取考勤记录
     * @param zkem 考勤机连接
     * @param machineNum 机器号
     * @return 结果
     */
    private List<AttendanceRecordDTO> getGeneralLogData(ActiveXComponent zkem, Integer machineNum) {
        //机器号
        Variant dwMachineNumber = new Variant(machineNum, true);

        Variant dwEnrollNumber = new Variant("", true);
        Variant dwVerifyMode = new Variant(0, true);
        Variant dwInOutMode = new Variant(0, true);
        Variant dwYear = new Variant(0, true);
        Variant dwMonth = new Variant(0, true);
        Variant dwDay = new Variant(0, true);
        Variant dwHour = new Variant(0, true);
        Variant dwMinute = new Variant(0, true);
        Variant dwSecond = new Variant(0, true);
        Variant dwWorkCode = new Variant(0, true);
        List<AttendanceRecordDTO> records = new ArrayList<>();
        boolean newresult = false;
        do {
            Variant vResult = Dispatch.call(zkem, "SSR_GetGeneralLogData", dwMachineNumber,
                    dwEnrollNumber, dwVerifyMode, dwInOutMode, dwYear, dwMonth, dwDay, dwHour, dwMinute, dwSecond, dwWorkCode);
            newresult = vResult.getBoolean();
            if (newresult) {
                String enrollNumber = dwEnrollNumber.getStringRef();
                //如果没有编号，则跳过，因为会出现很多虚假数据。
                if (enrollNumber == null || enrollNumber.trim().length() == 0) {
                    continue;
                }

                String time = dwYear.getIntRef() + "-" + dwMonth.getIntRef() + "-" + dwDay.getIntRef() + " " + dwHour.getIntRef()
                        + ":" + dwMinute.getIntRef() + ":" + dwSecond.getIntRef();

                AttendanceRecordDTO recordDTO = new AttendanceRecordDTO(enrollNumber, time,
                        dwVerifyMode.getIntRef(), dwInOutMode.getIntRef());
                records.add(recordDTO);
            }
        } while (newresult);
        return records;
    }
}
