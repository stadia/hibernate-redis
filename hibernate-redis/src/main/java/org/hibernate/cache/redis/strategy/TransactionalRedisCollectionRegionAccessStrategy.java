/*
 * Copyright 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hibernate.cache.redis.strategy;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.redis.jedis.JedisClient;
import org.hibernate.cache.redis.regions.RedisCollectionRegion;
import org.hibernate.cache.CollectionRegion;
import org.hibernate.cache.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.access.SoftLock;
import org.hibernate.cfg.Settings;

/**
 * JTA CollectionRegionAccessStrategy.
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 11:14
 */
@Slf4j
public class TransactionalRedisCollectionRegionAccessStrategy
        extends AbstractRedisAccessStrategy<RedisCollectionRegion>
        implements CollectionRegionAccessStrategy {

    @Getter
    private final JedisClient redis;

    public TransactionalRedisCollectionRegionAccessStrategy(RedisCollectionRegion region,
                                                            Settings settings) {
        super(region, settings);
        this.redis = region.getRedis();
    }

    @Override
    public CollectionRegion getRegion() {
        return region;
    }

    @Override
    public Object get(Object key, long txTimestamp) {
        log.trace("retrive cache item in transactional. key=[{}]", key);
        return region.get(key);
    }

    @Override
    public boolean putFromLoad(Object key,
                               Object value,
                               long txTimestamp,
                               Object version,
                               boolean minimalPutOverride) {
        if (minimalPutOverride && region.contains(key)) {
            log.trace("cancel put from load... minimalPutOverride=[true], contains=[true]");
            return false;
        }
        log.trace("set cache item after entity loading... key=[{}], value=[{}], txTimestamp=[{}]", key, value, txTimestamp);
        region.put(key, value);
        return true;
    }

    @Override
    public SoftLock lockItem(Object key, Object version) {
        log.trace("lock cache item... key=[{}], version=[{}]", key, version);
        region.remove(key);
        return null;
    }

    @Override
    public void unlockItem(Object key, SoftLock lock) {
        log.trace("unlock cache item... key=[{}], lock=[{}]", key, lock);
        region.remove(key);
    }

    @Override
    public void remove(Object key) {
        log.trace("remove cache item... key=[{}]", key);
        region.remove(key);
    }
}
