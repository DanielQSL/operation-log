package com.qsl.efficiency.operationlog.service;

import com.qsl.efficiency.operationlog.model.Operator;

/**
 * 操作人 服务
 *
 * @author DanielQSL
 * @date 2022/5/25
 */
public interface IOperatorService {

    /**
     * 用于获取操作人信息，操作人信息一般会通过过滤器提前得到，然后将其保存在 {@link ThreadLocal} 中
     *
     * @return Operator
     */
    Operator getOperator();

}
