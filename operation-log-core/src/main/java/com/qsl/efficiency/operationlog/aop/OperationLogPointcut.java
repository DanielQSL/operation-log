package com.qsl.efficiency.operationlog.aop;

import com.qsl.efficiency.operationlog.annotation.OperationLog;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 操作日志 的拦截器的切点
 *
 * @author DanielQSL
 * @date 2022/5/25
 */
public class OperationLogPointcut extends StaticMethodMatcherPointcut {

    /**
     * @param method      目标方法
     * @param targetClass 目标类
     * @return 该目标方法是否由 @OperationLog 注解标记，若是，则返回 true
     */
    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        Annotation operationLogAnnotation = AnnotationUtils.findAnnotation(method, OperationLog.class);
        return Objects.nonNull(operationLogAnnotation);
    }

}
