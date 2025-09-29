package com.zcxd.db.model.result;

import com.zcxd.db.model.AtmTaskCard;
import lombok.Data;

/**
 * 扩展类
 * @author songanwei
 * @date 2021-10
 */
@Data
public class AtmTaskCardResult extends AtmTaskCard {

    /**
     * ATM设备编号
     */
    private String terNo;
}
