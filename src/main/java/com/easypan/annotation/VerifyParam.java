package com.easypan.annotation;

import com.easypan.entity.enums.VerifyRegexEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author VectorX
 * @version V1.0
 * @description 自定义注解，用于参数校验
 * @date 2024-07-19 22:00:37
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface VerifyParam
{
    /**
     * 最小值
     *
     * @return int
     */
    int minLength() default -1;

    /**
     * 最大值
     *
     * @return int
     */
    int maxLength() default -1;

    /**
     * 必填
     *
     * @return boolean
     */
    boolean required() default false;

    /**
     * 正则表达式
     *
     * @return {@link VerifyRegexEnum }
     */
    VerifyRegexEnum regex() default VerifyRegexEnum.NO;
}
