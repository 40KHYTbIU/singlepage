package com.kaduchka.dao;

import com.kaduchka.common.Record;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;

@Component
public class RecordRepositoryImpl implements RecordRepository {

  @Override
  public Collection<Record> getRecords(Map<String, Object> fieldFilter, String sortField, int offset, int limit) {
    Collection<Record> records = IntStream.range(offset, offset + limit).mapToObj(i -> new Record(i, "Record#" + i, new Date())).collect(toSet());
    return records;
  }
}
