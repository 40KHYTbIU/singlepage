package com.kaduchka.dao;

import com.kaduchka.common.Filter;
import com.kaduchka.common.Record;
import com.kaduchka.common.Sort;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.kaduchka.common.Direction.ASC;

@Component("inMemory")
public class MemoryRepositoryImpl implements RecordRepository {
    private Collection<Record> records;
    private Random random = new Random();

    @PostConstruct
    public void init() {
        records = IntStream.range(0, 1_000_000).parallel().mapToObj(i ->
                new Record(Long.valueOf(i),
                        "Record#" + i * random.nextInt(3),
                        new Date(new Date().getTime() - random.nextInt(1_000)),
                        (((double) random.nextInt(1_000_000)) / 100D), Collections.singleton("fucNum")))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Record> getRecords(Filter filter, Sort sortField, long offset, long limit) {
        Stream<Record> recordStream = getRecordStream(filter);
        if (sortField != null) {
            switch (sortField.getSortField()) {
                case ID:
                    if (sortField.getSortDirection().equals(ASC)) {
                        recordStream = recordStream.sorted((e1, e2) -> e1.getId().compareTo(e2.getId()));
                    } else {
                        recordStream = recordStream.sorted((e1, e2) -> e2.getId().compareTo(e1.getId()));
                    }
                    break;
                case NUMBER:
                    if (sortField.getSortDirection().equals(ASC)) {
                        recordStream = recordStream.sorted((e1, e2) -> e1.getNumber().compareTo(e2.getNumber()));
                    } else {
                        recordStream = recordStream.sorted((e1, e2) -> e2.getNumber().compareTo(e1.getNumber()));
                    }
                    break;
                case AMOUNT:
                    if (sortField.getSortDirection().equals(ASC)) {
                        recordStream = recordStream.sorted((e1, e2) -> e1.getAmount().compareTo(e2.getAmount()));
                    } else {
                        recordStream = recordStream.sorted((e1, e2) -> e2.getAmount().compareTo(e1.getAmount()));
                    }
                    break;
                case DATE:
                    if (sortField.getSortDirection().equals(ASC)) {
                        recordStream = recordStream.sorted((e1, e2) -> e1.getDate().compareTo(e2.getDate()));
                    } else {
                        recordStream = recordStream.sorted((e1, e2) -> e2.getDate().compareTo(e1.getDate()));
                    }
                    break;
            }

        }
        return recordStream.skip(offset).limit(limit).collect(Collectors.toList());
    }

    private Stream<Record> getRecordStream(Filter filter) {
        Stream<Record> recordStream = records.stream().parallel();

        if (filter != null) {
                if (filter.getFilterId() != null) {
                    recordStream = recordStream.filter(r -> r.getId().equals(filter.getFilterId()));
                }
                if (filter.getFilterNumber()  != null) {
                    recordStream = recordStream.filter(r -> r.getNumber().contains(filter.getFilterNumber()));
                }

                if (filter.getFilterAmount()  != null) {
                    recordStream = recordStream.filter(r -> r.getAmount().equals(filter.getFilterAmount()));
                }
                if (filter.getFilterDate()  != null) {
                    recordStream = recordStream.filter(r -> r.getDate().equals(filter.getFilterDate()));
                }

        }
        return recordStream;
    }

    @Override
    public long getCount(Filter filter) {
        Stream<Record> recordStream = getRecordStream(filter);
        return recordStream.count();
    }
}
