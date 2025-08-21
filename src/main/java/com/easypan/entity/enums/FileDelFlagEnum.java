package com.easypan.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author VectorX
 * @version 1.0.0
 * @description File del Flag 枚举
 * @date 2024/07/23
 * @see Enum
 */
@AllArgsConstructor
@Getter
public enum FileDelFlagEnum
{

    DEL(0, "删除"),
    RECYCLE(1, "回收站"),
    USING(2, "使用中");

    private Integer flag;

    private String desc;
}
