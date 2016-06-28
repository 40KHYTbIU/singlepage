package com.kaduchka.dao;

import com.kaduchka.common.Filter;
import com.kaduchka.common.Record;
import com.kaduchka.common.Sort;

import java.util.Collection;

public interface RecordRepository {

  /**
   * Return filtered and sorted collection of records
   *
   * @param filter      - filter
   * @param sortField   - field for sort
   * @param offset      - offset for records
   * @param limit       - records limit
   * @return            - record collection
   */
  Collection<Record> getRecords(Filter filter, Sort sortField, int offset, int limit);
}
