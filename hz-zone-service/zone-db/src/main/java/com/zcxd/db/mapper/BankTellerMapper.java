package com.zcxd.db.mapper;

import com.zcxd.db.model.BankTeller;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 银行员工信息 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public interface BankTellerMapper extends BaseMapper<BankTeller> {

    /**
     * 编号查询用户数据
     * @param tellerNo
     * @param bankId 机构ID
     * @param id
     * @return
     */
    BankTeller getByTellerNo(@Param("tellerNo") String tellerNo,
                             @Param("bankId") Long bankId,
                             @Param("id") Long id);
}
