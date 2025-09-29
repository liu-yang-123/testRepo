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
public class SchdResultRecord extends Model<SchdResultRecord> {

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
    private Long planDay;

    /**
     * 类别 0-系统生成 1-手动触发
     */
    private Integer category;

    /**
     * 操作类型 0-添加 1-修改
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
     * 修改时间
     */
    private Long updateTime;

    /**
     * 状态 0/微推送 1已推送
     */
    private Integer state;

    /**
     * 修改内容
     */
    private String content;
}