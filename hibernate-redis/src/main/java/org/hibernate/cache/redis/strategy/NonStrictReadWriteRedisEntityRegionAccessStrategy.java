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

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.redis.regions.RedisEntityRegion;
import org.hibernate.cache.EntityRegion;
import org.hibernate.cache.access.EntityRegionAccessStrategy;
import org.hibernate.cache.access.SoftLock;
import org.hibernate.cfg.Settings;

/**
 * Redis specific non-strict read/write entity region access strategy
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 10:46
 */
@Slf4j
public class NonStrictReadWriteRedisEntityRegionAccessStrategy
        extends AbstractRedisAccessStrategy<RedisEntityRegion>
        implements EntityRegionAccessStrategy {

    public NonStrictReadWriteRedisEntityRegionAccessStrategy(RedisEntityRegion region, Settings settings) {
        super(region, settings);
    }

    @Override
    public EntityRegion getRegion() {
        return region;
    }

    @Override
    public Object get(Object key, long txTimestamp) {
        log.trace("get cache item... key=[{}], txTimestamp=[{}]", key, txTimestamp);
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
        return null;
    }

    @Override
    public void unlockItem(Object key, SoftLock lock) {
        log.trace("unlock cache item... key=[{}], lock=[{}]", key, lock);
        region.remove(key);
    }

    @Override
    public boolean insert(Object key, Object value, Object version) {
        return false;
    }

    @Override
    public boolean afterInsert(Object key, Object value, Object version) {
        return false;
    }

    @Override
    public boolean update(Object key,
                          Object value,
                          Object currentVersion,
                          Object previousVersion) {
        log.trace("update cache item... key=[{}], value=[{}]", key, value);
        remove(key);
        return false;
    }

    @Override
    public boolean afterUpdate(Object key,
                               Object value,
                               Object currentVersion,
                               Object previousVersion,
                               SoftLock lock) {
        log.trace("after update cache item... key=[{}], value=[{}]", key, value);
        unlockItem(key, lock);
        return false;
    }

    @Override
    public void remove(Object key) {
        log.trace("remove cache item... key=[{}]", key);
        region.remove(key);
    }
}
