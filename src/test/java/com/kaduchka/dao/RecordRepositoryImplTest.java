package com.kaduchka.dao;

import com.kaduchka.common.Filter;
import com.kaduchka.common.Record;
import com.kaduchka.common.Sort;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Collection;

import static com.kaduchka.common.Fields.NAME;
import static com.kaduchka.common.Sort.Direction.DESC;

public class RecordRepositoryImplTest extends TestCase {

  private RecordRepository repository = new RecordRepositoryImpl();

  @Override
  public void setUp() throws Exception {
    ((RecordRepositoryImpl) repository).init();
  }

  @Test
  public void testGetRecords() throws Exception {
    final Collection<Record> records = repository.getRecords(null, null, 0, 10);
    assertEquals("Records count should be equals 10", 10, records.size());
  }

  @Test
  public void testGetFilteredRecords() throws Exception {
    final Filter fieldFilter = new Filter();
    fieldFilter.setName("999999");
    final Collection<Record> records = repository.getRecords(fieldFilter, null, 0, 10);
    assertEquals("Records count should be equals 1", 1, records.size());
  }

  @Test
  public void testGetSortedRecords() throws Exception {
    final Filter fieldFilter = new Filter();
    final Collection<Record> records = repository.getRecords(fieldFilter, new Sort(NAME, DESC), 0, 1);
    assertEquals("First record should be equals 999 999", 999_999, records.stream().findFirst().get().getId());
  }


}