package com.qsl.efficiency.operationlog.service.impl;

import com.qsl.efficiency.operationlog.model.LogRecord;
import com.qsl.efficiency.operationlog.service.ILogRecordPersistenceService;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 操作日志持久化方案的默认实现：将操作日志输出到日志文件中
 *
 * @author DanielQSL
 * @date 2022/5/25
 */
@Slf4j
public class DefaultLogRecordPersistenceServiceImpl implements ILogRecordPersistenceService {

    @Override
    public void record(LogRecord logRecord) {
        if (Objects.nonNull(logRecord)) {
            log.info("[operation-log] {}", logRecord.toString());
        }
    }

}
