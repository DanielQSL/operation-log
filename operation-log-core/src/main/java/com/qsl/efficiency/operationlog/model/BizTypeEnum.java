package com.qsl.efficiency.operationlog.model;

/**
 * 业务类型
 *
 * @author DanielQSL
 * @date 2022/5/25
 */
public enum BizTypeEnum {

    /**
     * 新增
     */
    CREATE("新增"),

    /**
     * 更新
     */
    UPDATE("更新"),

    /**
     * 删除
     */
    DELETE("删除"),

    /**
     * 暂停
     */
    PAUSE("暂停"),

    /**
     * 恢复
     */
    RESUME("恢复"),

    /**
     * 绑定
     */
    BIND("绑定"),

    /**
     * 解绑
     */
    UNBIND("解绑"),

    /**
     * 资源变更
     */
    ADJUST_RESOURCE("资源变更"),
    ;

    private final String description;

    BizTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
