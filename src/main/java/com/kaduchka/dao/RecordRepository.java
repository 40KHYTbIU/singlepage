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
   * @param sort        - field for sort
   * @param offset      - offset for records
   * @param limit       - records limit
   * @return            - record collection
   */
  Collection<Record> getRecords(Filter filter, Sort sort, long offset, long limit);

  /**
   * Return records count
   *
   * @param filter      - filter
   * @return            - record count
   */
  long getCount(Filter filter);
}
