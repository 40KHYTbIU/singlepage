package com.kaduchka.dao;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.kaduchka.common.Fields;
import com.kaduchka.common.Filter;
import com.kaduchka.common.Record;
import com.kaduchka.common.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Component("redis")
public class RedisRepositoryImpl implements RecordRepository {

  @Autowired
  private RedisTemplate<byte[], Object> template;

  @Value("${redis.generate:false}")
  private boolean generate;

  private Random random = new Random();

  private static final ThreadLocal<Kryo> kryos = new ThreadLocal<Kryo>() {
    protected Kryo initialValue() {
      return new Kryo();
    };
  };


  public static byte[] serialize(Object o, Class clazz){
    Kryo kryo = kryos.get();
    try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
      try (Output output = new Output(b)) {
        kryo.writeObject(output, o);
      }
      return b.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new byte[0];
  }

  public static Object deserialize(byte[] bytes, Class clazz) {
    Kryo kryo = kryos.get();
    try (ByteArrayInputStream b = new ByteArrayInputStream(bytes)) {
      try (Input input = new Input(b)) {
        return kryo.readObject(input, clazz);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new Object();
  }

  @PostConstruct
  public void init() {
    //TODO: generate sets for filter and sort
    if (generate) {
      template.execute(connection -> {
        for (int i = 0; i < 1_000_000; i++) {
          String key = "record:" + i;
          Map<byte[], byte[]> fields = new HashMap<>();

          fields.put(serialize(Fields.ID, Fields.class), serialize(Long.valueOf(i), Long.class));
          fields.put(serialize(Fields.NUMBER, Fields.class), serialize("Record#" + i * random.nextInt(3), String.class));
          fields.put(serialize(Fields.DATE, Fields.class), serialize(new Date(new Date().getTime() - random.nextInt(1_000)), Date.class));
          fields.put(serialize(Fields.AMOUNT, Fields.class), serialize(new BigDecimal((((double) random.nextInt(1_000_000)) / 100D)), BigDecimal.class));
//          fields.put(serialize(Fields.LIST, Fields.class), serialize(Collections.singleton("fucNum"), LinkedList.class));
          connection.hMSet(serialize(key, String.class), fields);
        }
        return null;
      }, false, true);
    }
  }

  @Override
  public Collection<Record> getRecords(Filter filter, Sort sortField, int offset, int limit) {
    return template.execute((RedisCallback<Collection<Record>>) connection -> {

      Collection<Record> resultList = new LinkedList<>();
      for (int i = offset; i < offset + limit; i++) {
        String key = "record:" + i;
        Map<byte[], byte[]> resultMap = connection.hGetAll(serialize(key, String.class));
        if (!resultMap.isEmpty()) {
          resultList.add(mapToRecord(resultMap));
        }
      }
      return resultList;
    });
  }

  private Record mapToRecord(Map<byte[], byte[]> entries) {
    Record record = new Record();

    for (byte[] field : entries.keySet()) {
      switch ((Fields) deserialize(field, Fields.class)) {
        case ID:
          record.setId((Long) deserialize(entries.get(field), Long.class));
          break;
        case NUMBER:
          record.setNumber((String) deserialize(entries.get(field), String.class));
          break;
        case DATE:
          record.setDate((Date) deserialize(entries.get(field), Date.class));
          break;
        case AMOUNT:
          record.setAmount((BigDecimal) deserialize(entries.get(field), BigDecimal.class));
          break;
        case LIST:
//          record.setList((Collection<String>) deserialize(entries.get(field), LinkedList.class));
          break;
      }
    }
    return record;
  }
}
