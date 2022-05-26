package com.qsl.efficiency.operationlog.aop;

import com.qsl.efficiency.operationlog.context.OperationLogContext;
import com.qsl.efficiency.operationlog.model.LogRecord;
import com.qsl.efficiency.operationlog.model.LogRecordOps;
import com.qsl.efficiency.operationlog.model.MethodInvocationResult;
import com.qsl.efficiency.operationlog.parser.OperationLogValueParser;
import com.qsl.efficiency.operationlog.service.ILogRecordPersistenceService;
import com.qsl.efficiency.operationlog.service.IOperatorService;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 操作日志 方法拦截器
 *
 * @author DanielQSL
 * @date 2022/5/25
 */
@Slf4j
public class OperationLogInterceptor extends OperationLogValueParser implements MethodInterceptor, InitializingBean {

    private String tenantId;

    private LogRecordOperationSource logRecordOperationSource;

    private IOperatorService operatorService;

    private ILogRecordPersistenceService logRecordPersistenceService;

    /**
     * @param invocation 连接点，Spring AOP 在连接点周围维护了拦截器链
     * @return 返回目标方法执行的结果
     * @throws Throwable 目标方法执行过程中所抛出的异常
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object result = null;
        Throwable throwable = null;
        String errMsg = null;
        OperationLogContext.putEmptySpan();

        Class<?> targetClass = getTargetClass(invocation.getThis());
        Collection<LogRecordOps> operations = new ArrayList<>();
        Map<String, String> functionNameAndReturnMap = new HashMap<>();
        try {
            operations = logRecordOperationSource.computeLogRecordOperations(invocation.getMethod(), targetClass);
            List<String> spElTemplates = getBeforeExecuteFunctionTemplate(operations);
            functionNameAndReturnMap = processBeforeExecuteFunctionTemplate(spElTemplates, targetClass, invocation.getMethod(), invocation.getArguments());
        } catch (Exception e) {
            log.error("[operation-log] log record parse before function exception", e);
        }

        try {
            result = invocation.proceed();
        } catch (Throwable e) {
            throwable = e;
            errMsg = e.getMessage();
        }

        try {
            if (!CollectionUtils.isEmpty(operations)) {
                persistOperationLog(new MethodInvocationResult(invocation, result, throwable, errMsg), operations, functionNameAndReturnMap);
            }
        } catch (Exception e) {
            log.error("[operation-log] log record persist exception", e);
        } finally {
            OperationLogContext.clear();
        }

        // 如果目标方法执行过程中抛出了异常，那么一定要重新抛出
        if (Objects.nonNull(throwable)) {
            throw throwable;
        }
        return result;
    }

    private Class<?> getTargetClass(Object target) {
        return AopProxyUtils.ultimateTargetClass(target);
    }

    private List<String> getBeforeExecuteFunctionTemplate(Collection<LogRecordOps> operations) {
        List<String> spElTemplates = new ArrayList<>();
        for (LogRecordOps operation : operations) {
            // 执行之前的函数，失败模版不解析
            List<String> templates = getSpElTemplates(operation, operation.getSuccessLogTemplate());
            if (!CollectionUtils.isEmpty(templates)) {
                spElTemplates.addAll(templates);
            }
        }
        return spElTemplates;
    }

    private List<String> getSpElTemplates(LogRecordOps operation, String action) {
        List<String> spElTemplates = new ArrayList<>();
        spElTemplates.add(operation.getType());
        spElTemplates.add(operation.getBizNo());
        spElTemplates.add(action);
        spElTemplates.add(operation.getExtra());
        if (!StringUtils.isEmpty(operation.getCondition())) {
            spElTemplates.add(operation.getCondition());
        }
        return spElTemplates;
    }

    /**
     * 持久化操作记录
     *
     * @param methodInvocationResult 方法调用结果
     */
    private void persistOperationLog(MethodInvocationResult methodInvocationResult, Collection<LogRecordOps> operations, Map<String, String> functionNameAndReturnMap) {
        MethodInvocation methodInvocation = methodInvocationResult.getMethodInvocation();
        Object result = methodInvocationResult.getResult();
        Method method = methodInvocation.getMethod();
        Class<?> targetClass = getTargetClass(methodInvocation.getThis());
        Object[] args = methodInvocation.getArguments();

        for (LogRecordOps operation : operations) {
            try {
                // 获取成功/失败日志的模板
                String action = getContent(Objects.isNull(methodInvocationResult.getThrowable()), operation);
                if (StringUtils.isEmpty(action)) {
                    // 没有日志模板则忽略
                    continue;
                }

                // 获取需要解析的表达式
                List<String> spElTemplates = getSpElTemplates(operation, action);
                // 获取操作人ID
                String operatorId = getOperatorIdFromServiceAndPutTemplate(operation, spElTemplates);

                Map<String, String> expressionValues = processTemplate(spElTemplates, result, targetClass, method, args,
                        methodInvocationResult.getErrMsg(), functionNameAndReturnMap);
                if (logConditionPassed(operation.getCondition(), expressionValues)) {
                    LogRecord logRecord = LogRecord.builder()
                            .tenant(tenantId)
                            .type(expressionValues.get(operation.getType()))
                            .bizNo(expressionValues.get(operation.getBizNo()))
                            .operatorId(getRealOperatorId(operation, operatorId, expressionValues))
                            .content(expressionValues.get(action))
                            .extra(expressionValues.get(operation.getExtra()))
                            .fail(Objects.nonNull(methodInvocationResult.getThrowable()))
                            .operationTime(LocalDateTime.now())
                            .build();

                    // 如果 action 为空，不记录日志
                    if (StringUtils.isEmpty(logRecord.getContent())) {
                        continue;
                    }
                    // 保存
                    logRecordPersistenceService.record(logRecord);
                }
            } catch (Exception t) {
                log.error("[operation-log] log record execute exception", t);
            }
        }
    }

    private String getContent(boolean success, LogRecordOps operation) {
        if (success) {
            return operation.getSuccessLogTemplate();
        } else {
            return operation.getFailLogTemplate();
        }
    }

    private String getOperatorIdFromServiceAndPutTemplate(LogRecordOps operation, List<String> spElTemplates) {
        String realOperatorId = "";
        if (StringUtils.isEmpty(operation.getOperatorId())) {
            realOperatorId = operatorService.getOperator().getOperatorId();
            if (StringUtils.isEmpty(realOperatorId)) {
                throw new IllegalArgumentException("[operation-log] operator is null");
            }
        } else {
            spElTemplates.add(operation.getOperatorId());
        }
        return realOperatorId;
    }

    private boolean logConditionPassed(String condition, Map<String, String> expressionValues) {
        return StringUtils.isEmpty(condition) || StringUtils.endsWithIgnoreCase(expressionValues.get(condition), "true");
    }

    private String getRealOperatorId(LogRecordOps operation, String operatorIdFromService, Map<String, String> expressionValues) {
        return !StringUtils.isEmpty(operatorIdFromService) ? operatorIdFromService : expressionValues.get(operation.getOperatorId());
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public void setLogRecordOperationSource(LogRecordOperationSource logRecordOperationSource) {
        this.logRecordOperationSource = logRecordOperationSource;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        operatorService = beanFactory.getBean(IOperatorService.class);
        logRecordPersistenceService = beanFactory.getBean(ILogRecordPersistenceService.class);
    }

}
