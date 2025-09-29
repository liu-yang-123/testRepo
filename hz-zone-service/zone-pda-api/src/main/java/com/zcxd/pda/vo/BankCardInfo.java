package com.zcxd.pda.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author shijin
 * @date 2021/5/18 10:17
 */
@ApiModel("吞卡信息表")
@Data
public class BankCardInfo implements Serializable {

    /**
     * 银行卡号
     */
    private String cardNo;

    /**
     * 发卡行
     */
    private String cardBank;
}
