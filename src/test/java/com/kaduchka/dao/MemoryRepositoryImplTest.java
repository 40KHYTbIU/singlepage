package com.kaduchka.dao;

import com.kaduchka.common.Fields;
import com.kaduchka.common.Filter;
import com.kaduchka.common.Record;
import com.kaduchka.common.Sort;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Collection;

import static com.kaduchka.common.Direction.DESC;

public class MemoryRepositoryImplTest extends TestCase {

  private RecordRepository repository = new MemoryRepositoryImpl();

  @Override
  public void setUp() throws Exception {
    ((MemoryRepositoryImpl) repository).init();
  }

  @Test
  public void testGetRecords() throws Exception {
    final Collection<Record> records = repository.getRecords(null, null, 0, 10);
    assertEquals("Records count should be equals 10", 10, records.size());
  }

  @Test
  public void testGetFilteredRecords() throws Exception {
    final Filter filter = new Filter();
    filter.setFilterNumber("999999");
    final Collection<Record> records = repository.getRecords(filter, null, 0, 10);
    assertEquals("Records count should be equals 1", 1, records.size());
  }

  @Test
  public void testGetSortedRecords() throws Exception {
    final Collection<Record> records = repository.getRecords(null, new Sort(Fields.ID, DESC), 0, 1);
    assertEquals("First record should be equals 999 999", new Long(999_999), records.stream().findFirst().get().getId());
  }


}