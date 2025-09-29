package com.zcxd.gun.controller;

import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.gun.db.model.AttendanceMachine;
import com.zcxd.gun.service.AttendanceMachineService;
import com.zcxd.gun.vo.AttendanceMachineQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author zccc
 */
@RestController
@RequestMapping("/attendance")
public class AttendanceMachineController {
    @Autowired
    AttendanceMachineService machineService;

    @OperateLog(value = "新增考勤机", type = OperateType.ADD)
    @PostMapping("/save")
    public Object save(@Validated @RequestBody AttendanceMachine attendanceMachine) {
        return machineService.save(attendanceMachine) ? Result.success() :Result.fail("新增失败");
    }

    @OperateLog(value = "修改考勤机", type=OperateType.EDIT)
    @PostMapping("/update")
    public Object update(@RequestBody AttendanceMachine attendanceMachine){
        return machineService.updateById(attendanceMachine) ? Result.success() :Result.fail("更新失败");
    }

    @PostMapping("/findListByPage")
    public Object findListByPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                   @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                 @Validated @RequestBody AttendanceMachineQueryVO vo) throws IllegalAccessException {
        return machineService.findListByPage(page, limit, vo);
    }
}
