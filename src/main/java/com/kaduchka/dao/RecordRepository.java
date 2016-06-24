package com.kaduchka.dao;

import com.kaduchka.common.Record;

import java.util.Collection;
import java.util.Map;

public interface RecordRepository {

  /**
   * Return filtered and sorted collection of records
   *
   * @param fieldFilter - map < fieldName, filterValue >
   * @param sortField   - field for sort (only ASC)
   * @param offset      - offset for records
   * @param limit       - records limit
   * @return            - record collection
   */
  Collection<Record> getRecords(Map<String, Object> fieldFilter, String sortField, int offset, int limit);
}
