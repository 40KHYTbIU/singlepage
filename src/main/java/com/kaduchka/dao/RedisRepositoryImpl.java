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
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.kaduchka.common.Direction.ASC;

@Component("redis")
public class RedisRepositoryImpl implements RecordRepository {

    private static final String SCRIPT_TEXT = "local charSet = redis.call('ZRANGEBYSCORE', KEYS[1], ARGV[1], ARGV[2]); " +
            "redis.call('del', KEYS[2]); " +
            "for k,v in pairs(charSet) do redis.call('zadd', KEYS[2], 0, v) end;";
    private static final String IDS = "ids";
    private static final String NUMBERS = "numbers";
    private static final String NUMBERS_IDS = "numbersIds";
    private static final String IDS_DATE = "idsDate";
    private static final String IDS_AMOUNT = "idsAmount";
    private static final String RECORD = "record";
    private static final String SORTED_TAG = ":sorted";

    @Value("${redis.generate.batch.size:1000}")
    private int BATCH_SIZE;

    @Value("${redis.generate.batch.count:1000}")
    private int BATCH_COUNT;

    @Value("${redis.generate.batch.count.skip:0}")
    private int BATCH_COUNT_SKIP;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${redis.generate:false}")
    private boolean generate;

    private Random random = new Random();

    private AtomicLong recordsCount = new AtomicLong(0);


    private DefaultRedisScript<Void> redisScript;


    @PostConstruct
    public void init() {
        if (generate) {
            for (long step = 0 + BATCH_COUNT_SKIP; step < BATCH_COUNT + BATCH_COUNT_SKIP; step++) {
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

        redisScript = new DefaultRedisScript<Void>();
        redisScript.setScriptText(SCRIPT_TEXT);
        redisScript.setResultType(Void.class);
    }

    private void insertData(RedisOperations operations) {
        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
        ZSetOperations<String, String> opsForZSet = redisTemplate.opsForZSet();

        Long startRecord = recordsCount.get();

        for (long i = startRecord; i < startRecord + BATCH_SIZE; i++) {
            final String key = RECORD + ":" + i;
            final String id = "" + i;
            final String currentNumber = "Record#" + i * random.nextInt(3);
            final long currentTime = System.currentTimeMillis() - random.nextInt(1_000);
            final double currentAmount = ((double) random.nextInt(1_000_000)) / 100D;

            //Put a record to hashmap
            opsForHash.put(key, Fields.ID.toString(), id);
            opsForHash.put(key, Fields.NUMBER.toString(), currentNumber);
            opsForHash.put(key, Fields.DATE.toString(), Long.toString(currentTime));
            opsForHash.put(key, Fields.AMOUNT.toString(), Double.toString(currentAmount));

            //Put fields values to different sorted sets for filtering and sorting and typeahead
            opsForZSet.add(IDS, id, 0);
            opsForZSet.add(NUMBERS, currentNumber, 0);
            opsForZSet.add(NUMBERS_IDS, currentNumber, i);
            opsForZSet.add(IDS_DATE, id, currentTime);
            opsForZSet.add(IDS_AMOUNT, id, currentAmount);

        }

        recordsCount.addAndGet(BATCH_SIZE);
    }

    @Override
    public Collection<Record> getRecords(Filter filter, Sort sortField, long offset, long limit) {

        redisTemplate.multi();

        //Key where save previous result
        String nextKey = "";

        if (filter != null) {
            if (filter.getFilterId() != null) {
                return Collections.singleton(objectMapToRecord(redisTemplate.opsForHash().entries(RECORD + ":" + filter.getFilterId())));
            }
            if (filter.getFilterNumber() != null) {
                final Double id = redisTemplate.opsForZSet().score(NUMBERS_IDS, filter.getFilterNumber());
                return Collections.singleton(objectMapToRecord(redisTemplate.opsForHash().entries(RECORD + ":" + id.longValue())));
            }

            if (filter.getFilterAmount() != null) {
                List<String> keys = new LinkedList<>();
                keys.add(IDS_AMOUNT);
                final String amountTempKey = IDS_AMOUNT + ":" + filter.getFilterAmount().longValue();
                keys.add(amountTempKey);

                nextKey = amountTempKey;

                redisTemplate.execute(redisScript, keys, "0", filter.getFilterAmount().toString());
            }
/*      if (filter.getFilterDate() != null) {
        List<String> keys = new LinkedList<>();
        keys.add("idsAmount");
        final String amountTempKey = "idsAmount:" + filter.getFilterAmount().longValue();
        keys.add(amountTempKey);

        nextKey = amountTempKey;

        redisTemplate.execute(redisScript, keys, 0, filter.getFilterAmount());
        recordStream = recordStream.filter(r -> r.getDate().equals(filter.getFilterDate()));
      }*/

        }
        if (sortField != null) {
            switch (sortField.getSortField()) {
                case ID:
                    if (!nextKey.isEmpty()) {
                        redisTemplate.opsForZSet().intersectAndStore(nextKey, IDS, nextKey + SORTED_TAG);
                        nextKey = nextKey + SORTED_TAG;
                    } else {
                        nextKey = IDS;
                    }
                    break;
//        case NUMBER:
//          if (!nextKey.isEmpty()) {
//            redisTemplate.opsForZSet().intersectAndStore(nextKey, NUMBERS, nextKey + SORTED_TAG);
//            nextKey = nextKey + SORTED_TAG;
//          } else {
//            //TODO: WRONG! Should contains IDS not NUMBERS
//            nextKey = NUMBERS;
//          }
//          break;
                case AMOUNT:
                    if (!nextKey.isEmpty()) {
                        redisTemplate.opsForZSet().intersectAndStore(nextKey, IDS_AMOUNT, nextKey + SORTED_TAG);
                        nextKey = nextKey + SORTED_TAG;
                    } else {
                        nextKey = IDS_AMOUNT;
                    }
                    break;
                case DATE:
                    if (!nextKey.isEmpty()) {
                        redisTemplate.opsForZSet().intersectAndStore(nextKey, IDS_DATE, nextKey + SORTED_TAG);
                        nextKey = nextKey + SORTED_TAG;
                    } else {
                        nextKey = IDS_DATE;
                    }
                    break;
            }

        }

        Collection<String> idsList = new LinkedList<>();

        if (!nextKey.isEmpty()) {
            if (sortField.getSortDirection().equals(ASC)) {
                idsList = redisTemplate.opsForZSet().rangeByScore(nextKey, 0, Double.MAX_VALUE, offset, limit);
            } else {
                idsList = redisTemplate.opsForZSet().reverseRangeByScore(nextKey, 0, Double.MAX_VALUE, offset, limit);
            }
        } else {
            for (long k = offset; k < offset + limit; k++) {
                idsList.add("" + k);
            }
        }

        final Collection<String> finalIdsList = idsList;
        return getRecordsList(finalIdsList);
    }

    private Collection<Record> getRecordsList(Collection<String> finalIdsList) {
        return this.redisTemplate.execute((RedisCallback<Collection<Record>>) connection -> {

            Collection<Record> resultList = new LinkedList<>();
            for (String id : finalIdsList) {
                String key = RECORD + ":" + id;
                Map<String, String> resultMap = ((StringRedisConnection) connection).hGetAll(key);
                if (!resultMap.isEmpty()) {
                    resultList.add(mapToRecord(resultMap));
                }
            }
            return resultList;
        });
    }

    private Record objectMapToRecord(Map<Object, Object> objectMap) {
        Map<String, String> stringMap = new TreeMap<>();
        for (Object property : objectMap.keySet()) {
            stringMap.put((String) property, (String) objectMap.get(property));
        }
        return mapToRecord(stringMap);
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
