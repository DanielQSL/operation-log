package com.qsl.efficiency.operationlog.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 业务未解析的信息
 *
 * @author DanielQSL
 * @date 2022/5/25
 */
@AllArgsConstructor
@Getter
@Setter
public class BizNoParseInfo {

    /**
     * 方法调用
     */
    private MethodInvocation methodInvocation;

    /**
     * 执行结果
     */
    private Object result;

    /**
     * 原始业务标识
     */
    private String originBizNo;

}
