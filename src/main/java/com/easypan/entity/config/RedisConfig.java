package com.easypan.entity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-07-21 12:02:37
 */
@Configuration
public class RedisConfig<V>
{
    @Bean
    // 定义一个名为redisTemplate的Bean，类型为RedisTemplate<String, V>
    public RedisTemplate<String, V> redisTemplate(RedisConnectionFactory factory) {
        // 创建一个RedisTemplate对象
        final RedisTemplate<String, V> redisTemplate = new RedisTemplate<>();
        // 设置Redis连接工厂
        redisTemplate.setConnectionFactory(factory);

        // 设置key的序列化方式为字符串
        redisTemplate.setKeySerializer(RedisSerializer.string());
        // 设置value的序列化方式为JSON
        redisTemplate.setValueSerializer(RedisSerializer.json());

        // 设置hash key的序列化方式为字符串
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        // 设置hash value的序列化方式为JSON
        redisTemplate.setHashValueSerializer(RedisSerializer.json());

        // 初始化RedisTemplate
        redisTemplate.afterPropertiesSet();
        // 返回RedisTemplate对象
        return redisTemplate;
    }
}
