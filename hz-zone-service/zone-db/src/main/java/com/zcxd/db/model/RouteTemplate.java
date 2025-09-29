package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

/**
 * <p>
 * 线路模板任务记录
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public class RouteTemplate extends Model<RouteTemplate>{
	
    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 线路编号
     */
    private String routeNo;

    /**
     * 线路编号
     */
    private String routeName;
    
    /**
     * 计划开始时间
     */
    private String planBeginTime;

    /**
     * 计划结束时间
     */
    private String planFinishTime;
    
    /**
     * 线路类型
     */
    private Integer routeType;
    
    /**
     * 线路生成规则
     */
    private Integer rule;
    
    /**
     * 线路生成标志
     */
    private Integer sign;
    
    /**
     * 线路排序
     */
    private Integer sort;

    /**
     * 所属顶级部门
     */
    private Long departmentId;
    
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

    /**
     * 更新人
     */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Long updateUser;

    /**
     * 更新时间
     */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Long updateTime;

    /**
     * 删除标志
     */
    private Integer deleted;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRouteNo() {
		return routeNo;
	}

	public void setRouteNo(String routeNo) {
		this.routeNo = routeNo;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
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

	public Long getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(Long updateUser) {
		this.updateUser = updateUser;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getDeleted() {
		return deleted;
	}

	public void setDeleted(Integer deleted) {
		this.deleted = deleted;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getPlanBeginTime() {
		return planBeginTime;
	}

	public void setPlanBeginTime(String planBeginTime) {
		this.planBeginTime = planBeginTime;
	}

	public String getPlanFinishTime() {
		return planFinishTime;
	}

	public void setPlanFinishTime(String planFinishTime) {
		this.planFinishTime = planFinishTime;
	}
	
	public Integer getRouteType() {
		return routeType;
	}

	public void setRouteType(Integer routeType) {
		this.routeType = routeType;
	}

	public Integer getRule() {
		return rule;
	}

	public void setRule(Integer rule) {
		this.rule = rule;
	}

	public Integer getSign() {
		return sign;
	}

	public void setSign(Integer sign) {
		this.sign = sign;
	}

	@Override
	public String toString() {
		return "RouteTemplate [id=" + id + ", routeNo=" + routeNo + ", routeName=" + routeName + ", planBeginTime="
				+ planBeginTime + ", planFinishTime=" + planFinishTime + ", routeType=" + routeType + ", rule=" + rule
				+ ", sign=" + sign + ", sort=" + sort + ", departmentId=" + departmentId + ", comments=" + comments
				+ ", createUser=" + createUser + ", createTime=" + createTime + ", updateUser=" + updateUser
				+ ", updateTime=" + updateTime + ", deleted=" + deleted + "]";
	}

}
