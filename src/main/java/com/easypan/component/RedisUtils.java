package com.easypan.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-07-18 22:55:12
 */
@Component("redisUtils")
@Slf4j
public class RedisUtils<V>
{
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RedisUtils.class);

    @Resource
    private RedisTemplate<String, V> redisTemplate;

    public V get(String key) {
        return key == null ?
               null :
               redisTemplate
                       .opsForValue()
                       .get(key);
    }

    public boolean set(String key, V value) {
        try {
            redisTemplate
                    .opsForValue()
                    .set(key, value);
            return true;
        }
        catch (Exception e) {
            log.error("设置redis：key:{}，value:{}失败", key, value, e);
            return false;
        }
    }

    public boolean setex(String key, V value, Long time) {
        try {
            if (time > 0) {
                redisTemplate
                        .opsForValue()
                        .set(key, value, time, TimeUnit.SECONDS);
            }
            else {
                set(key, value);
            }
            return true;
        }
        catch (Exception e) {
            log.error("设置redis：key{}，value{}失败", key, value, e);
            return false;
        }
    }
}
