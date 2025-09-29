package com.zcxd.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.BankMapper;
import com.zcxd.db.model.Bank;
import org.springframework.stereotype.Service;

/**
 * @author lilanglang
 * @date 2021-10-18 9:15
 */
@Service
public class BankService extends ServiceImpl<BankMapper, Bank> {
}