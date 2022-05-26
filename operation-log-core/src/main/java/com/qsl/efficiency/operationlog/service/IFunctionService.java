package com.qsl.efficiency.operationlog.service;

/**
 * 自定义函数接口
 *
 * @author DanielQSL
 * @date 2022/5/25
 */
public interface IFunctionService {

    String apply(String functionName, String value);

    boolean beforeFunction(String functionName);
}
