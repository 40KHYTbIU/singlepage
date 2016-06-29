package com.kaduchka.dao;

import com.kaduchka.common.Fields;
import com.kaduchka.common.Filter;
import com.kaduchka.common.Record;
import com.kaduchka.common.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Component("redis")
public class RedisRepositoryImpl implements RecordRepository {

    @Value("${redis.generate.batchsize:1000}")
    private int BATCH_SIZE;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${redis.generate:false}")
    private boolean generate;

    private Random random = new Random();

    private AtomicLong recordsCount = new AtomicLong(0);

    @PostConstruct
    public void init() {

        //TODO: generate sets for filter and sort
        if (generate) {
            long count = 100;
            for (long step = 0; step < count; step++) {
                System.out.print("Inserting for step " + step + "... ");
                redisTemplate.executePipelined(new SessionCallback<Void>() {
                    public Void execute(RedisOperations operations) throws DataAccessException {
                        insertData(operations);
                        return null;
                    }
                });
                System.out.println("FINISHED");
            }
        }
    }

    private void insertData(RedisOperations operations) {
        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
        ZSetOperations<String, String> opsForZSet = redisTemplate.opsForZSet();

        Long startRecord = recordsCount.get();

        for (long i = startRecord; i < startRecord + BATCH_SIZE; i++) {
            String key = "record:" + i;
            String currentNumber = "Record#" + i * random.nextInt(3);
            long currentTime = System.currentTimeMillis() - random.nextInt(1_000);
            double currentAmount = ((double) random.nextInt(1_000_000)) / 100D;

            //Put a record to hashmap
            opsForHash.put(key, Fields.ID.toString(), "" + i);
            opsForHash.put(key, Fields.NUMBER.toString(), currentNumber);
            opsForHash.put(key, Fields.DATE.toString(), Long.toString(currentTime));
            opsForHash.put(key, Fields.AMOUNT.toString(), Double.toString(currentAmount));

            //Put fields values to different sorted sets for filtering and sorting and typeahead
            opsForZSet.add("ids", "" + i, 0);
            opsForZSet.add("numbers", currentNumber, 0);
        }

        recordsCount.addAndGet(BATCH_SIZE);
    }

    @Override
    public Collection<Record> getRecords(Filter filter, Sort sortField, int offset, int limit) {

/*        if (filter != null) {
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

        }*/


        return this.redisTemplate.execute((RedisCallback<Collection<Record>>) connection -> {

            Collection<Record> resultList = new LinkedList<>();
            for (int i = offset; i < offset + limit; i++) {
                String key = "record:" + i;
                Map<String, String> resultMap = ((StringRedisConnection) connection).hGetAll(key);
                if (!resultMap.isEmpty()) {
                    resultList.add(mapToRecord(resultMap));
                }
            }
            return resultList;
        });
    }

    private Record mapToRecord(Map<String, String> entries) {
        Record record = new Record();

        for (String field : entries.keySet()) {
            switch (Fields.valueOf(field)) {
                case ID:
                    record.setId(Long.parseLong(entries.get(field)));
                    break;
                case NUMBER:
                    record.setNumber(entries.get(field));
                    break;
                case DATE:
                    record.setDate(new Date(Long.parseLong(entries.get(field))));
                    break;
                case AMOUNT:
                    record.setAmount(Double.parseDouble(entries.get(field)));
                    break;
                case LIST:
//          record.setList((Collection<String>) deserialize(entries.get(field), LinkedList.class));
                    break;
            }
        }
        return record;
    }
}
