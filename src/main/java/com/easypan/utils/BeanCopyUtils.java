package com.easypan.utils;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author VectorX
 * @version 1.0.0
 * @description 复制工具
 * @date 2024/07/23
 */
public class BeanCopyUtils
{
    public static <T, S> List<T> copyList(List<S> slist, Class<T> tClass) {
        return slist
                .stream()
                .map(s -> copy(s, tClass))
                .collect(Collectors.toList());
    }

    public static <T, S> T copy(S s, Class<T> tClass) {
        try {
            T t = tClass.newInstance();
            BeanUtils.copyProperties(s, t);
            return t;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
