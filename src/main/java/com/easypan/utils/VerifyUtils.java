package com.easypan.utils;

import com.easypan.entity.enums.VerifyRegexEnum;

import java.util.regex.Pattern;

/**
 * @author VectorX
 * @version V1.0
 * @description 正则表达式校验工具类
 * @date 2024-07-19 23:23:17
 */
public class VerifyUtils
{
    /**
     * 校验正则表达式
     *
     * @param regex 正则表达式
     * @param value 值
     * @return boolean
     */
    public static boolean verify(VerifyRegexEnum regex, String value) {
        return verify(regex.getRegex(), value);
    }

    /**
     * 校验正则表达式
     *
     * @param regex 正则表达式
     * @param value 值
     * @return boolean
     */
    public static boolean verify(String regex, String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }

        return Pattern
                .compile(regex)
                .matcher(value)
                .matches();
    }
}
