package com.qsl.efficiency.operationlog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志记录实体
 *
 * @author DanielQSL
 * @date 2022/5/25
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LogRecord {

    /**
     * id
     */
    private Serializable id;

    /**
     * 租户
     */
    private String tenant;

    /**
     * 保存的操作日志的类型，比如：订单类型、商品类型
     */
    private String type;

    /**
     * 日志的子类型，比如订单的C端日志，和订单的B端日志，type都是订单类型，但是子类型不一样
     */
    private String subType;

    /**
     * 被操作对象的业务唯一标识，比如：用户ID、商品ID等
     */
    private String bizNo;

    /**
     * 操作用户ID
     */
    private String operatorId;

    /**
     * 操作用户名
     */
    private String operatorName;

    /**
     * 日志内容
     */
    private String content;

    /**
     * 日志的额外信息
     */
    private String extra;

    /**
     * 是否为操作失败的日志
     */
    private Boolean fail;

    /**
     * 操作日志发生时间
     */
    private LocalDateTime operationTime;

}
