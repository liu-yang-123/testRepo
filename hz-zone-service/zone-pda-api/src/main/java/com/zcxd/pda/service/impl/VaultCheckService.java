package com.zcxd.pda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.VaultCheckMapper;
import com.zcxd.db.mapper.VaultVolumMapper;
import com.zcxd.db.model.VaultCheck;
import com.zcxd.db.model.VaultVolum;
import com.zcxd.pda.dto.VaultVolumDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @ClassName VaultCheckService
 * @Description 金库盘点记录
 * @author shijin
 * @Date 2021年5月20日上午10:20:05
 */
@Service
public class VaultCheckService extends ServiceImpl<VaultCheckMapper, VaultCheck> {


}
