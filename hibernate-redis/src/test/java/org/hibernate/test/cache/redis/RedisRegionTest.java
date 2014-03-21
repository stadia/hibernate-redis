package org.hibernate.test.cache.redis;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.redis.SingletonRedisRegionFactory;
import org.hibernate.cache.redis.strategy.AbstractReadWriteRedisAccessStrategy;
import org.hibernate.cfg.Configuration;
import org.hibernate.test.strategy.ItemValueExtractor;

import java.util.Map;

/**
 * org.hibernate.test.cache.redis.RedisRegionTest
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오전 12:52
 */
@Slf4j
public class RedisRegionTest extends RedisTest {

    @Override
    protected Map getMapFromCachedEntry(final Object entry) {
        final Map map;
        if (entry.getClass()
                 .getName()
                 .equals(AbstractReadWriteRedisAccessStrategy.class.getName() + "$Item")) {
            map = ItemValueExtractor.getValue(entry);
        } else {
            map = (Map) entry;
        }
        return map;
    }

    public RedisRegionTest(String x) {
        super(x);
    }

    @Override
    protected Class getCacheRegionFactory() {
        return SingletonRedisRegionFactory.class;
    }
}
