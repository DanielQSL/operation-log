package com.qsl.efficiency.operationlog.annotation;

import java.lang.annotation.*;

/**
 * 操作日志记录注解
 *
 * @author DanielQSL
 * @date 2022/5/25
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface OperationLog {

    /**
     * 方法执行成功后的日志模版
     */
    String success();

    /**
     * 方法执行失败后的日志模版
     */
    String fail() default "";

    /**
     * 日志的操作人
     */
    String operator() default "";

    /**
     * 操作日志的类型
     */
    String type();

    /**
     * 日志的子类型
     */
    String subType() default "";

    /**
     * 日志绑定的业务标识
     */
    String bizNo();

    /**
     * 日志的额外信息
     */
    String extra() default "";

    /**
     * 记录日志的条件
     */
    String condition() default "";

}
