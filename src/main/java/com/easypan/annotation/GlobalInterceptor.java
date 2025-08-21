package com.easypan.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author VectorX
 * @version V1.0
 * @description 自定义注解，用于全局拦截器
 * @date 2024-07-19 21:47:21
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
// 被 javadoc 或类似工具记录。帮助生成 API 文档时包含这个注解的信息
@Documented
// 这干啥用的？？？
@Mapping
public @interface GlobalInterceptor
{
    /**
     * 校验参数
     *
     * @return boolean
     */
    boolean checkParams() default false;

    /**
     * 校验登录
     *
     * @return boolean
     */
    boolean checkLogin() default true;

    /**
     * 校验管理员
     *
     * @return boolean
     */
    boolean checkAdmin() default false;
}
