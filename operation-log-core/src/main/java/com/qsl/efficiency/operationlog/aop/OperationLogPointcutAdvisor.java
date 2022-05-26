package com.qsl.efficiency.operationlog.aop;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

/**
 * 操作日志 切面
 *
 * @author DanielQSL
 * @date 2022/5/25
 */
public class OperationLogPointcutAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    private Pointcut pointcut;

    /**
     * @param pointcut 切入点
     * @param advice   通知，即切面中的拦截逻辑
     */
    public OperationLogPointcutAdvisor(Pointcut pointcut, Advice advice) {
        this.pointcut = pointcut;
        setAdvice(advice);
    }

    public void setPointcut(Pointcut pointcut) {
        this.pointcut = pointcut;
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

}
