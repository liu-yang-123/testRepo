package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

/**
 * 
 * @ClassName RouteEmpChange
 * @Description 线路人员变更记录
 * @author 秦江南
 * @Date 2021年9月2日上午11:34:27
 */
public class RouteEmpChange extends Model<RouteEmpChange>{
	private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 线路id
     */
    private Long routeId;

    /**
     * 岗位类型
     */
    private Integer jobType;
    
    /**
     * 变更前人员
     */
    private Long oldManId;
    
    /**
     * 变更后人员
     */
    private Long newManId;
    
    /**
     * 备注
     */
    private String comments;
    
    /**
     * 创建人
     */
    @TableField(fill= FieldFill.INSERT)
    private Long createUser;
    
    /**
     * 创建时间
     */
    @TableField(fill= FieldFill.INSERT)
    private Long createTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRouteId() {
		return routeId;
	}

	public void setRouteId(Long routeId) {
		this.routeId = routeId;
	}

	public Integer getJobType() {
		return jobType;
	}

	public void setJobType(Integer jobType) {
		this.jobType = jobType;
	}

	public Long getOldManId() {
		return oldManId;
	}

	public void setOldManId(Long oldManId) {
		this.oldManId = oldManId;
	}

	public Long getNewManId() {
		return newManId;
	}

	public void setNewManId(Long newManId) {
		this.newManId = newManId;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Long getCreateUser() {
		return createUser;
	}

	public void setCreateUser(Long createUser) {
		this.createUser = createUser;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "RouteEmpChange [id=" + id + ", routeId=" + routeId + ", jobType=" + jobType + ", oldManId=" + oldManId
				+ ", newManId=" + newManId + ", comments=" + comments + ", createUser=" + createUser + ", createTime="
				+ createTime + "]";
	}
    
}
