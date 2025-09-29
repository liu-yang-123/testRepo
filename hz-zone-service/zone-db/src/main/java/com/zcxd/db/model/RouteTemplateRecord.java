package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

/**
 * @Description:排班结果记录表
 * @Author: lilanglang
 * @Date: 2021/7/19 9:39
 **/
@Data
public class RouteTemplateRecord extends Model<RouteTemplateRecord> {

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 部门ID
     */
    private Long departmentId;

    /**
     * 记录排班日期
     */
    private Long routeDate;

    /**
     * 类别 0-创建  1-更新
     */
    private Integer opType;

    /**
     * 用户ID  0-系统用户
     */
    private Long createUser;

    /**
     * 添加日期
     */
    private Long createTime;

    /**
     * 状态 0-失败 1-成功
     */
    private Integer state;

    /**
     * 修改内容
     */
    private String content;
}