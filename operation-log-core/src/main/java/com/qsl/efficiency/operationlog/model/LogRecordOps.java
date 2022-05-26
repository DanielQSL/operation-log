package com.qsl.efficiency.operationlog.model;

import lombok.Builder;
import lombok.Data;

/**
 * OperationLog注解中的属性映射
 *
 * @author DanielQSL
 * @date 2022/5/25
 */
@Data
@Builder
public class LogRecordOps {
    private String successLogTemplate;
    private String failLogTemplate;
    private String operatorId;
    private String type;
    private String bizNo;
    private String subType;
    private String extra;
    private String condition;
    private boolean isBatch;
}
