package com.qsl.efficiency.operationlog.service;

import com.qsl.efficiency.operationlog.model.LogRecord;

/**
 * 日志记录持久化服务
 * 可以将日志保存在任何存储介质上
 * tips:需要新开事务，失败日志不能因为事务回滚而丢失
 *
 * @author DanielQSL
 * @date 2022/5/25
 */
public interface ILogRecordPersistenceService {

    /**
     * 持久化操作日志
     *
     * @param logRecord 操作日志实体
     */
    void record(LogRecord logRecord);

}
