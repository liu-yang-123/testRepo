package com.zcxd.base.controller;

import com.zcxd.base.dto.BoxpackDTO;
import com.zcxd.base.service.BoxpackService;
import com.zcxd.base.vo.BoxpackVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.Boxpack;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * 箱包操作控制类
 * @author songanwei
 * @date 2021-11-22
 */
@Api(tags = "箱包模块")
@RequestMapping("/boxpack")
@AllArgsConstructor
@RestController
public class BoxpackController {

    private BoxpackService boxpackService;

    /**
     * 列表数据
     * @param page 当前页
     * @param limit 分页大小
     * @param departmentId 区域ID
     * @param bankId 机构ID
     * @param boxNo 箱包编号
     * @return
     */
    @ApiOperation(value = "箱包列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer", dataTypeClass = Integer.class, defaultValue = "1"),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer", dataTypeClass = Integer.class, defaultValue = "10"),
            @ApiImplicitParam(name = "departmentId", value = "部门",required = true, dataType = "Long", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "bankId", value = "机构ID",dataType = "Long",dataTypeClass = Long.class),
            @ApiImplicitParam(name = "boxNo", value = "箱包编号", dataType = "String", dataTypeClass = String.class)
    })
    @GetMapping("/list")
    public Result list(@RequestParam Integer page,
                       @RequestParam Integer limit,
                       @RequestParam Long departmentId,
                       Long bankId,
                       String boxNo){
        Boxpack boxpack = new Boxpack();
        boxpack.setDepartmentId(departmentId);
        boxpack.setBankId(bankId);
        boxpack.setBoxNo(boxNo);
        Long total = boxpackService.getTotal(boxpack);
        List<BoxpackDTO> boxpackList = boxpackService.getList(page,limit,boxpack);
        ResultList resultList = ResultList.builder().total(total).list(boxpackList).build();
        return Result.success(resultList);
    }


    /**
     * 创建操作
     * @param boxpackVO
     * @return
     */
    @ApiOperation(value = "创建")
    @OperateLog(value = "创建箱包",type = OperateType.ADD)
    @PostMapping("/create")
    public Result create(@RequestBody BoxpackVO boxpackVO){
        boolean b = boxpackService.create(boxpackVO);
        return b ? Result.success("箱包创建成功") : Result.fail("箱包创建失败");
    }

    /**
     * 获取
     * @param id 箱包ID
     * @return 查询结果
     */
    @PostMapping("/findById")
    public Result<Boxpack> findById(@RequestParam("id") Long id){
        Boxpack boxpack = boxpackService.getById(id);
        return boxpack == null ? Result.fail("无指定箱包") : Result.success(boxpack);
    }

    /**
     * 修改箱包
     * @param boxpackVO
     * @return
     */
    @ApiOperation(value = "修改")
    @OperateLog(value = "修改箱包", type = OperateType.EDIT)
    @PostMapping("/update")
    public Result update(@RequestBody BoxpackVO boxpackVO){
        boolean b = boxpackService.edit(boxpackVO);
        return b ? Result.success("箱包修改成功") : Result.fail("箱包修改失败");
    }

    @ApiOperation(value = "删除箱包")
    @ApiImplicitParam(name = "id", value = "箱包id", required = true, dataType = "Long", dataTypeClass = Long.class)
    @OperateLog(value = "删除箱包",type = OperateType.DELETE)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
        boolean b = boxpackService.delete(id);
        return b ? Result.success("删除成功") : Result.fail("删除失败");
    }

}
