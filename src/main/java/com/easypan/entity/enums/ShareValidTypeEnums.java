package com.easypan.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description: DATE: 2024/6/29 11:30 Author: 小廖同学
 */

@Getter
public enum ShareValidTypeEnums
{
    DAY_1(0, 1, "1天"),
    DAY_7(1, 7, "7天"),
    DAY_30(2, 30, "30天"),
    FOREVER(3, -1, "永久有效");

    private Integer type;
    private Integer days;
    private String desc;

    private ShareValidTypeEnums(Integer type, Integer days, String desc) {
        this.type = type;
        this.days = days;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public Integer getDays() {
        return days;
    }

    public String getDesc() {
        return desc;
    }

    public static ShareValidTypeEnums getByType(Integer type) {
        for (ShareValidTypeEnums typeEnums : ShareValidTypeEnums.values()) {
            if (typeEnums
                    .getType()
                    .equals(type)) {
                return typeEnums;
            }
        }
        return null;
    }

}

