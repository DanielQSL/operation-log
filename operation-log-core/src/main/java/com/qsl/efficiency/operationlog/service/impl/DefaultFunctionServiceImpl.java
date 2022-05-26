package com.qsl.efficiency.operationlog.service.impl;

import com.qsl.efficiency.operationlog.service.IFunctionService;
import com.qsl.efficiency.operationlog.service.IParseFunction;

/**
 * 默认自定义函数 实现类
 * <p>
 * 根据传入的函数名称 functionName 找到对应的 IParseFunction，然后把参数传入到 IParseFunction 的 apply 方法上最后返回函数的值
 *
 * @author DanielQSL
 * @date 2022/5/25
 */
public class DefaultFunctionServiceImpl implements IFunctionService {

    private final ParseFunctionFactory parseFunctionFactory;

    public DefaultFunctionServiceImpl(ParseFunctionFactory parseFunctionFactory) {
        this.parseFunctionFactory = parseFunctionFactory;
    }

    @Override
    public String apply(String functionName, String value) {
        IParseFunction function = parseFunctionFactory.getFunction(functionName);
        if (function == null) {
            return value;
        }
        return function.apply(value);
    }

    @Override
    public boolean beforeFunction(String functionName) {
        return parseFunctionFactory.isBeforeFunction(functionName);
    }

}
