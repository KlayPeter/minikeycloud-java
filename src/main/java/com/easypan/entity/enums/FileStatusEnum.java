package com.easypan.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author VectorX
 * @version 1.0.0
 * @description 文件状态枚举
 * @date 2024/07/25
 * @see Enum
 */
@Getter
public enum FileStatusEnum
{
    TRANSFER(0, "转码中"),
    TRANSFER_FAIL(1, "转码失败"),
    USING(2, "使用中");

    private Integer status;

    private String desc;

    FileStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}

