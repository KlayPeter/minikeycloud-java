package com.easypan.utils;

import java.util.List;

/**
 * @author VectorX
 * @version V1.0
 * @description 集合实用程序
 * @date 2024-08-04 22:50:02
 */
public class CollectionUtils
{
    public static <T> boolean isEmpty(List<T> list) {
        return org.springframework.util.CollectionUtils.isEmpty(list);
    }

    public static <T> boolean isNotEmpty(List<T> list) {
        return !org.springframework.util.CollectionUtils.isEmpty(list);
    }
}
