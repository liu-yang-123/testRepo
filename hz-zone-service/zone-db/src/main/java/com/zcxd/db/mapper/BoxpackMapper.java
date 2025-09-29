package com.zcxd.db.mapper;

import com.zcxd.db.model.Boxpack;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 尾箱信息 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public interface BoxpackMapper extends BaseMapper<Boxpack> {

    /**
     * 机构、编号查询数据
     * @param boxNo 箱包编号
     * @param id 主键ID
     * @return
     */
    Boxpack getByNo(@Param("boxNo") String boxNo,
                        @Param("id") Long id);


    /**
     * RFID查询数据
     * @param rfid RFID编号
     * @param id 主键ID
     * @return
     */
    Boxpack getByRfid(@Param("rfid") String rfid,
                      @Param("id") Long id);

}
