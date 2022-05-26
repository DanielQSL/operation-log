package com.qsl.efficiency.operationlog.service.impl;


import com.qsl.efficiency.operationlog.service.IParseFunction;

/**
 * 默认自定义函数解析
 *
 * @author DanielQSL
 * @date 2022/5/25
 */
public class DefaultParseFunction implements IParseFunction {

    @Override
    public boolean executeBefore() {
        return true;
    }

    @Override
    public String functionName() {
        return null;
    }

    @Override
    public String apply(String value) {
        return null;
    }

}
