package com.kaduchka.dao;

import com.kaduchka.common.Fields;
import com.kaduchka.common.Filter;
import com.kaduchka.common.Record;
import com.kaduchka.common.Sort;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.kaduchka.common.Sort.Direction.ASC;
import static java.util.stream.Collectors.toSet;

@Component
public class RecordRepositoryImpl implements RecordRepository {
  private Collection<Record> records;

  @PostConstruct
  public void init() {
    records = IntStream.range(0, 1_000_000).mapToObj(i -> new Record(i, "Record#" + i, new Date())).collect(toSet());
  }

  @Override
  public Collection<Record> getRecords(Filter fieldFilter, Sort sortField, int offset, int limit) {
    Stream<Record> recordStream = records.stream();

    if (fieldFilter != null) {
      if (fieldFilter.getId() != null) {
        recordStream = recordStream.filter(r -> fieldFilter.getId() == (r.getId()));
      }
      if (fieldFilter.getName() != null) {
        recordStream = recordStream.filter(r -> r.getName().contains((String) fieldFilter.getName()));
      }
      if (fieldFilter.getDate() != null) {
        recordStream = recordStream.filter(r -> r.getDate().equals(fieldFilter.getDate()));
      }
    }

    if (sortField != null) {
      switch (sortField.getField()) {
        case ID:
          if (sortField.getDirection().equals(ASC)) {
            recordStream = recordStream.sorted((e1, e2) -> Long.compare(e1.getId(), e2.getId()));
          } else {
            recordStream = recordStream.sorted((e1, e2) -> Long.compare(e2.getId(), e1.getId()));
          }
          break;
        case NAME:
          if (sortField.getDirection().equals(ASC)) {
            recordStream = recordStream.sorted((e1, e2) -> e1.getName().compareTo(e2.getName()));
          } else {
            recordStream = recordStream.sorted((e1, e2) -> e2.getName().compareTo(e1.getName()));
          }

          break;
        case DATE:
          if (sortField.getDirection().equals(ASC)) {
            recordStream = recordStream.sorted((e1, e2) -> e1.getDate().compareTo(e2.getDate()));
          } else {
            recordStream = recordStream.sorted((e1, e2) -> e2.getDate().compareTo(e1.getDate()));
          }
          break;
      }

    }

    return recordStream.skip(offset).limit(limit).collect(toSet());
  }
}
