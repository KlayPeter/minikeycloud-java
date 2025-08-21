package com.easypan.entity.constants;

/**
 * @author VectorX
 * @version V1.0
 * @description Redis 常量
 * @date 2024-07-29 20:06:43
 */
public class RedisConstants
{
    public static final String REDIS_KEY_SYS_SETTING = "easypan:syssetting:";
    public static final String REDIS_KEY_USER_SPACE_USE = "easypan:user:spaceuse:";
    public static final String REDIS_KEY_USER_TEMP_FILE_SIZE = "easypan:user:tempfile:size:";
    public static final String REDIS_KEY_DOWNLOAD = "easypan:download:";

    public static final Integer REDIS_KEY_EXPIRES_ONE_MIN = 60;
    public static final Integer REDIS_KEY_EXPIRES_FIVE_MIN = REDIS_KEY_EXPIRES_ONE_MIN * 5;
    public static final Integer REDIS_KEY_EXPIRES_ONE_HOUR = REDIS_KEY_EXPIRES_ONE_MIN * 60;
    public static final Integer REDIS_KEY_EXPIRES_ONE_DAY = REDIS_KEY_EXPIRES_ONE_HOUR * 24;
}
