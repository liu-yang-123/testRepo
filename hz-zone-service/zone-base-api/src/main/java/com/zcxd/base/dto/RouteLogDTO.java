package com.zcxd.base.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 车长日志
 * </p>
 *
 * @author admin
 * @since 2021-10-11
 */
@ToString
@Data
public class RouteLogDTO {

    /**
     * 唯一标识
     */
    private Long id;

    /**
     * 日志详情
     */
    private List<RouteLogItem> details;

    /**
     * 备注
     */
    private String comments;

    /**
     * 总体日志结果（所有子项正常时填 0 - 存在异常填- 1）
     */
    private Integer result;

    /**
     * 车长
     */
    private String leaderName;

}
