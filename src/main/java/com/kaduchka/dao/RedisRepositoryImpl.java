package com.kaduchka.dao;

import com.kaduchka.common.Fields;
import com.kaduchka.common.Filter;
import com.kaduchka.common.Record;
import com.kaduchka.common.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;

@Component("redis")
public class RedisRepositoryImpl implements RecordRepository {

    @Autowired
    private RedisTemplate<String, Object> template;

    private Random random = new Random();

    @PostConstruct
    public void init() {
        //TODO: generate sets for filter and sort
        for (int i = 0; i < 100; i++) {
            String key = "record:" + i;
            template.opsForHash().putIfAbsent(key, Fields.ID.toString(), Long.valueOf(i));
            template.opsForHash().putIfAbsent(key, Fields.NUMBER.toString(), "Record#" + i * random.nextInt(3));
            template.opsForHash().putIfAbsent(key, Fields.DATE.toString(), new Date(new Date().getTime() - random.nextInt(1_000)));
            template.opsForHash().putIfAbsent(key, Fields.AMOUNT.toString(), new BigDecimal((((double) random.nextInt(1_000_000)) / 100D)));
            template.opsForHash().putIfAbsent(key, Fields.LIST.toString(), Collections.singleton("fucNum"));
        }
    }

    @Override
    public Collection<Record> getRecords(Filter filter, Sort sortField, int offset, int limit) {
        Collection<Record> resultList = new LinkedList<>();
        for (int i = offset; i < limit; i++) {
            String key = "record:" + i;
           resultList.add(mapToRecord(template.opsForHash().entries(key)));
        }
        return resultList;
    }

    private Record mapToRecord(Map entries) {
        Record record = new Record();

        for (Object field: entries.keySet())
        {
            switch ((String) field){
                case "ID":
                    record.setId((Long) entries.get(field));
                    break;
                case "NUMBER":
                    record.setNumber((String) entries.get(field));
                    break;
                case "DATE":
                    record.setDate((Date) entries.get(field));
                    break;
                case "AMOUNT":
                    record.setAmount((BigDecimal) entries.get(field));
                    break;
                case "LIST":
                    record.setList((Collection<String>) entries.get(field));
                    break;
            }
        }
        return record;
    }
}
