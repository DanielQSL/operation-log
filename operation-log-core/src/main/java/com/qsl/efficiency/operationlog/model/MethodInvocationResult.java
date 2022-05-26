package com.qsl.efficiency.operationlog.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 方法调用结果
 *
 * @author DanielQSL
 * @date 2022/5/25
 */
@AllArgsConstructor
@Getter
@Setter
public class MethodInvocationResult {

    /**
     * 方法调用
     */
    private MethodInvocation methodInvocation;

    /**
     * 调用结果
     */
    private Object result;

    /**
     * 异常抛错
     */
    private Throwable throwable;

    /**
     * 错误信息
     */
    private String errMsg;

}
