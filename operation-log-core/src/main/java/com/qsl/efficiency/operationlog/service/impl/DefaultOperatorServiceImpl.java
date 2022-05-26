package com.qsl.efficiency.operationlog.service.impl;

import com.qsl.efficiency.operationlog.model.Operator;
import com.qsl.efficiency.operationlog.service.IOperatorService;

/**
 * 默认操作人 服务实现类
 *
 * @author DanielQSL
 * @date 2022/5/25
 */
public class DefaultOperatorServiceImpl implements IOperatorService {

    @Override
    public Operator getOperator() {
        // return Optional.ofNullable(UserUtils.getUser())
        //                .map(a -> new Operator(a.getName(), a.getLogin()))
        //                .orElseThrow(()->new IllegalArgumentException("user is null"));
        throw new IllegalArgumentException("请自行实现OperatorService接口，否则无法获取Operator信息");
    }

}
