package com.ef.Parser.dao;

import com.ef.Parser.model.LogRecord;
import org.springframework.data.repository.CrudRepository;

public interface LogRecordRepository extends CrudRepository<LogRecord, Integer> {
}
