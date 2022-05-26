package com.qsl.efficiency.operationlog.service;

/**
 * 自定义函数解析
 *
 * @author DanielQSL
 * @date 2022/5/25
 */
public interface IParseFunction {

    /**
     * 自定义函数是否在业务代码执行之前解析
     * 为了查询修改之前的内容
     */
    default boolean executeBefore() {
        return false;
    }

    String functionName();

    String apply(String value);

}
