package com.qsl.efficiency.spring.boot.autoconfigure;

import com.qsl.efficiency.operationlog.aop.LogRecordOperationSource;
import com.qsl.efficiency.operationlog.aop.OperationLogInterceptor;
import com.qsl.efficiency.operationlog.aop.OperationLogPointcut;
import com.qsl.efficiency.operationlog.aop.OperationLogPointcutAdvisor;
import com.qsl.efficiency.operationlog.service.IFunctionService;
import com.qsl.efficiency.operationlog.service.ILogRecordPersistenceService;
import com.qsl.efficiency.operationlog.service.IOperatorService;
import com.qsl.efficiency.operationlog.service.IParseFunction;
import com.qsl.efficiency.operationlog.service.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;
import java.util.Objects;

/**
 * 装配各组件
 *
 * @author DanielQSL
 * @date 2022/5/25
 */
@Configuration
public class OperationLogAutoConfiguration implements ImportAware {

    protected AnnotationAttributes enableOperationLog;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableOperationLog = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableOperationLog.class.getName(), false));
        if (this.enableOperationLog == null) {
            // 引入依赖但并无启用@EnableOperationLog
            throw new IllegalArgumentException(
                    "[operation-log] @EnableOperationLog is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Bean
    @ConditionalOnMissingBean(IParseFunction.class)
    @Role(BeanDefinition.ROLE_APPLICATION)
    public DefaultParseFunction parseFunction() {
        return new DefaultParseFunction();
    }

    @Bean
    public ParseFunctionFactory parseFunctionFactory(@Autowired List<IParseFunction> parseFunctions) {
        return new ParseFunctionFactory(parseFunctions);
    }

    @Bean
    @ConditionalOnMissingBean(IFunctionService.class)
    public IFunctionService functionService(ParseFunctionFactory parseFunctionFactory) {
        return new DefaultFunctionServiceImpl(parseFunctionFactory);
    }

    @Bean
    @ConditionalOnMissingBean(IOperatorService.class)
    @Role(BeanDefinition.ROLE_APPLICATION)
    public IOperatorService operatorService() {
        return new DefaultOperatorServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(ILogRecordPersistenceService.class)
    @Role(BeanDefinition.ROLE_APPLICATION)
    public ILogRecordPersistenceService logRecordPersistenceService() {
        return new DefaultLogRecordPersistenceServiceImpl();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public OperationLogPointcutAdvisor operationLogPointcutAdvisor(@Autowired OperationLogInterceptor operationLogInterceptor) {
        OperationLogPointcut pointcut = new OperationLogPointcut();

        OperationLogPointcutAdvisor operationLogPointcutAdvisor = new OperationLogPointcutAdvisor(pointcut, operationLogInterceptor);
        if (Objects.nonNull(enableOperationLog)) {
            operationLogPointcutAdvisor.setOrder(enableOperationLog.<Integer>getNumber("order"));
        }
        return operationLogPointcutAdvisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public OperationLogInterceptor operationLogInterceptor(@Autowired IFunctionService functionService) {
        OperationLogInterceptor interceptor = new OperationLogInterceptor();
        interceptor.setLogRecordOperationSource(new LogRecordOperationSource());
        interceptor.setTenantId(enableOperationLog.getString("tenant"));
        interceptor.setFunctionService(functionService);
        return interceptor;
    }

}
