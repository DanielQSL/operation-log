package com.qsl.efficiency.operationlog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 操作人
 *
 * @author DanielQSL
 * @date 2022/5/25
 */
@Data
public class Operator {

    /**
     * 操作人唯一标识，一般为当前登录用户的用户ID
     */
    private String operatorId;

    /**
     * 操作人姓名，一般为当前登录用户的用户名
     */
    private String operatorName;

    public Operator() {
    }

    public Operator(String operatorId) {
        this.operatorId = operatorId;
    }

    public Operator(String operatorId, String operatorName) {
        this.operatorId = operatorId;
        this.operatorName = operatorName;
    }

}
