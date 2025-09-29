package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 吐卡记录
 * </p>
 *
 * @author admin
 * @since 2021-07-23
 */
public class AtmTaskCard extends Model<AtmTaskCard> {

    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 取卡线路
     */
    private Long routeId;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 设备id
     */
    private Long atmId;
    /**
     * 所属机构
     */
    private Long bankId;

    /**
     * 吞卡卡号
     */
    private String cardNo;

    /**
     * 发卡行
     */
    private String cardBank;

    /**
     * 卡类型（ 0 - 实物卡，1 - 现场拿卡单）
     */
    private Integer category;

    /**
     * 交接人a
     */
    private Long collectManA;

    /**
     * 交接人b
     */
    private Long collectManB;

    /**
     * 交接时间
     */
    private Long collectTime;

    /**
     * 配卡出库人
     */
    private Long dispatchManA;

    /**
     * 配卡人2
     */
    private Long dispatchManB;

    /**
     * 配卡时间
     */
    private Long dispatchTime;

    /**
     * 送卡线路
     */
    private Long deliverRouteId;

    /**
     * 交卡网点
     */
    private Long deliverBankId;

    /**
     * 上交日期
     */
    private String deliverDay;

    /**
     * 派送方式（0 - 上缴银行，1 - 自取）
     */
    private Integer deliverType;

    /**
     * 移交人证件号码
     */
    private String receiverIdno;

    /**
     * 移交人姓名
     */
    private String receiverName;

    /**
     * 移交时间
     */
    private Long receiveTime;

    /**
     * 状态（0 - 取回，1-入库，2 - 派送, 3 - 领取
     */
    private Integer statusT;

    /**
     * 备注
     */
    private String comments;

    /**
     * 创建时间
     */
    @TableField(fill= FieldFill.INSERT)
    private Long createTime;
    
    /**
     * 所属顶级部门
     */
    private Long departmentId;
    
    /**
     * 取卡线路编号
     */
    private String routeNo;
    
    /**
     * 送卡线路编号
     */
    private String deliverRouteNo;


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

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getAtmId() {
        return atmId;
    }

    public void setAtmId(Long atmId) {
        this.atmId = atmId;
    }

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCardBank() {
        return cardBank;
    }

    public void setCardBank(String cardBank) {
        this.cardBank = cardBank;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Long getCollectManA() {
        return collectManA;
    }

    public void setCollectManA(Long collectManA) {
        this.collectManA = collectManA;
    }

    public Long getCollectManB() {
        return collectManB;
    }

    public void setCollectManB(Long collectManB) {
        this.collectManB = collectManB;
    }

    public Long getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Long collectTime) {
        this.collectTime = collectTime;
    }

    public Long getDispatchManA() {
        return dispatchManA;
    }

    public void setDispatchManA(Long dispatchManA) {
        this.dispatchManA = dispatchManA;
    }

    public Long getDispatchManB() {
        return dispatchManB;
    }

    public void setDispatchManB(Long dispatchManB) {
        this.dispatchManB = dispatchManB;
    }

    public Long getDispatchTime() {
        return dispatchTime;
    }

    public void setDispatchTime(Long dispatchTime) {
        this.dispatchTime = dispatchTime;
    }

    public Long getDeliverRouteId() {
        return deliverRouteId;
    }

    public void setDeliverRouteId(Long deliverRouteId) {
        this.deliverRouteId = deliverRouteId;
    }

    public Long getDeliverBankId() {
        return deliverBankId;
    }

    public void setDeliverBankId(Long deliverBankId) {
        this.deliverBankId = deliverBankId;
    }

    public String getDeliverDay() {
        return deliverDay;
    }

    public void setDeliverDay(String deliverDay) {
        this.deliverDay = deliverDay;
    }

    public Integer getDeliverType() {
        return deliverType;
    }

    public void setDeliverType(Integer deliverType) {
        this.deliverType = deliverType;
    }

    public String getReceiverIdno() {
        return receiverIdno;
    }

    public void setReceiverIdno(String receiverIdno) {
        this.receiverIdno = receiverIdno;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public Long getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Long receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Integer getStatusT() {
        return statusT;
    }

    public void setStatusT(Integer statusT) {
        this.statusT = statusT;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public String getRouteNo() {
		return routeNo;
	}

	public void setRouteNo(String routeNo) {
		this.routeNo = routeNo;
	}

	public String getDeliverRouteNo() {
		return deliverRouteNo;
	}

	public void setDeliverRouteNo(String deliverRouteNo) {
		this.deliverRouteNo = deliverRouteNo;
	}

	@Override
	public String toString() {
		return "AtmTaskCard [id=" + id + ", routeId=" + routeId + ", taskId=" + taskId + ", atmId=" + atmId
				+ ", cardNo=" + cardNo + ", cardBank=" + cardBank + ", collectManA=" + collectManA + ", collectManB="
				+ collectManB + ", collectTime=" + collectTime + ", dispatchManA=" + dispatchManA + ", dispatchManB="
				+ dispatchManB + ", dispatchTime=" + dispatchTime + ", deliverRouteId=" + deliverRouteId
				+ ", deliverBankId=" + deliverBankId + ", deliverDay=" + deliverDay + ", deliverType=" + deliverType
				+ ", receiverIdno=" + receiverIdno + ", receiverName=" + receiverName + ", receiveTime=" + receiveTime
				+ ", statusT=" + statusT + ", comments=" + comments + ", createTime=" + createTime + ", departmentId="
				+ departmentId + ", routeNo=" + routeNo + ", deliverRouteNo=" + deliverRouteNo + "]";
	}

}
