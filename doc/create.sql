CREATE TABLE `t_operation_log_record`
(
    `id`             BIGINT ( 20 ) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `tenant`         VARCHAR(64)   NOT NULL DEFAULT '' COMMENT '租户标识',
    `type`           VARCHAR(64)   NOT NULL DEFAULT '' COMMENT '操作日志的类型',
    `sub_type`       VARCHAR(64)   NOT NULL DEFAULT '' COMMENT '操作日志的子类型',
    `biz_no`         VARCHAR(64)   NOT NULL DEFAULT '' COMMENT '业务唯一标识',
    `operatorId`     VARCHAR(64)   NOT NULL DEFAULT '' COMMENT '操作人ID',
    `operatorName`   VARCHAR(64)   NOT NULL DEFAULT '' COMMENT '操作人姓名',
    `content`        VARCHAR(1024) NOT NULL DEFAULT '' COMMENT '日志内容',
    `extra`          text          NOT NULL DEFAULT '' COMMENT '日志的额外信息',
    `fail`           TINYINT ( 1 ) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否为操作失败的日志 0-否;1-是',
    `operation_time` datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作日志发生时间',
    PRIMARY KEY (`id`),
    KEY              `idx_tenant_type` ( `tenant`,`type` ) COMMENT '日志业务标识索引'
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COMMENT = '操作日志表';