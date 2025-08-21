package com.easypan.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author VectorX
 * @version 1.0.0
 * @description 文件夹类型枚举
 * @date 2024/07/28
 * @see Enum
 */
@Getter
public enum FileFolderTypeEnums
{
    FILE(0, "文件"),
    FOLDER(1, "目录");

    private final Integer type;

    private final String desc;

    private FileFolderTypeEnums(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
