package com.qsl.efficiency.operationlog.aop;

import com.qsl.efficiency.operationlog.annotation.OperationLog;
import com.qsl.efficiency.operationlog.model.LogRecordOps;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 操作日志 辅助类
 *
 * @author DanielQSL
 * @date 2022/5/25
 */
public class LogRecordOperationSource {

    public Collection<LogRecordOps> computeLogRecordOperations(Method method, Class<?> targetClass) {
        // Don't allow no-public methods as required.
        if (!Modifier.isPublic(method.getModifiers())) {
            return null;
        }

        // The method may be on an interface, but we need attributes from the target class.
        // If the target class is null, the method will be unchanged.
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        // If we are dealing with method with generic parameters, find the original method.
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);

        // First try is the method in the target class.
        return parseLogRecordAnnotations(specificMethod);
    }

    private Collection<LogRecordOps> parseLogRecordAnnotations(AnnotatedElement ae) {
        Collection<OperationLog> logRecordAnnotationAnnotations = AnnotatedElementUtils.findAllMergedAnnotations(ae, OperationLog.class);
        Collection<LogRecordOps> list = null;
        if (!logRecordAnnotationAnnotations.isEmpty()) {
            list = lazyInit(list);
            for (OperationLog recordAnnotation : logRecordAnnotationAnnotations) {
                list.add(parseLogRecordAnnotation(ae, recordAnnotation));
            }
        }
        return list;
    }

    private LogRecordOps parseLogRecordAnnotation(AnnotatedElement ae, OperationLog recordAnnotation) {
        LogRecordOps recordOps = LogRecordOps.builder()
                .successLogTemplate(recordAnnotation.success())
                .failLogTemplate(recordAnnotation.fail())
                .type(recordAnnotation.type())
                .subType(recordAnnotation.subType())
                .bizNo(recordAnnotation.bizNo())
                .operatorId(recordAnnotation.operator())
                .extra(recordAnnotation.extra())
                .condition(recordAnnotation.condition())
                .build();
        validateLogRecordOperation(ae, recordOps);
        return recordOps;
    }

    private void validateLogRecordOperation(AnnotatedElement ae, LogRecordOps recordOps) {
        if (!StringUtils.hasText(recordOps.getSuccessLogTemplate()) && !StringUtils.hasText(recordOps.getFailLogTemplate())) {
            throw new IllegalStateException("Invalid logRecord annotation configuration on '" +
                    ae.toString() + "'. 'one of successTemplate and failLogTemplate' attribute must be set.");
        }
    }

    private Collection<LogRecordOps> lazyInit(Collection<LogRecordOps> ops) {
        return (ops != null ? ops : new ArrayList<>(1));
    }

}
