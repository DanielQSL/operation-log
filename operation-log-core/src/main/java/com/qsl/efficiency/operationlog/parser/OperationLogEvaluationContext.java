package com.qsl.efficiency.operationlog.parser;

import com.qsl.efficiency.operationlog.context.OperationLogContext;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 操作日志评估上下文
 *
 * @author DanielQSL
 * @date 2022/5/25
 */
public class OperationLogEvaluationContext extends MethodBasedEvaluationContext {

    public OperationLogEvaluationContext(Object rootObject, Method method, Object[] arguments,
                                         ParameterNameDiscoverer parameterNameDiscoverer, Object ret, String errorMsg) {
        // 把方法的参数都放到 SpEL 解析的 RootObject 中
        super(rootObject, method, arguments, parameterNameDiscoverer);
        // 把 LogRecordContext 中的变量都放到 RootObject 中
        Map<String, Object> variables = OperationLogContext.getVariables();
        if (variables != null && variables.size() > 0) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                setVariable(entry.getKey(), entry.getValue());
            }
        }
        //把方法的返回值和 ErrorMsg 都放到 RootObject 中
        setVariable("_ret", ret);
        setVariable("_errorMsg", errorMsg);
    }

}