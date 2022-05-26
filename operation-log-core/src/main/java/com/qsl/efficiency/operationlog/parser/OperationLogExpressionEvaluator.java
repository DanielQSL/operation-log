package com.qsl.efficiency.operationlog.parser;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 操作日志 表达式评估
 *
 * @author DanielQSL
 * @date 2022/5/25
 */
public class OperationLogExpressionEvaluator extends CachedExpressionEvaluator {

    /**
     * 为了缓存方法、表达式和 SpEL 的 Expression 的对应关系，让方法注解上添加的 SpEL 表达式只解析一次
     */
    private final Map<ExpressionKey, Expression> expressionCache = new ConcurrentHashMap<>(64);

    /**
     * 为了缓存传入到 Expression 表达式的 Object
     */
    private final Map<AnnotatedElementKey, Method> targetMethodCache = new ConcurrentHashMap<>(64);

    /**
     * 解析表达式
     */
    public String parseExpression(String conditionExpression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
        Object value = getExpression(this.expressionCache, methodKey, conditionExpression).getValue(evalContext, Object.class);
        return value == null ? "" : value.toString();
    }

    public EvaluationContext createEvaluationContext(Method method, Object[] args, Class<?> targetClass,
                                                     Object result, String errorMsg, BeanFactory beanFactory) {
        Method targetMethod = getTargetMethod(targetClass, method);
        OperationLogEvaluationContext evaluationContext = new OperationLogEvaluationContext(
                null, targetMethod, args, getParameterNameDiscoverer(), result, errorMsg);
        if (beanFactory != null) {
            evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }
        return evaluationContext;
    }

    private Method getTargetMethod(Class<?> targetClass, Method method) {
        AnnotatedElementKey methodKey = new AnnotatedElementKey(method, targetClass);
        Method targetMethod = this.targetMethodCache.get(methodKey);
        if (targetMethod == null) {
            targetMethod = AopUtils.getMostSpecificMethod(method, targetClass);
            this.targetMethodCache.put(methodKey, targetMethod);
        }
        return targetMethod;
    }

}
