package com.easypan.utils;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author VectorX
 * @version V1.0
 * @description 随机实用程序
 * @date 2024-07-28 15:18:30
 */
public class RandomUtils
{
    /**
     * 生成随机数
     *
     * @param count 个数
     * @return String
     */
    public static String getRandomNumber(Integer count) {
        return RandomStringUtils.random(count, false, true);
    }

    /**
     * 生成随机数
     *
     * @param count 个数
     * @return String
     */
    public static String getRandomString(Integer count) {
        return RandomStringUtils.random(count, true, true);
    }
}
