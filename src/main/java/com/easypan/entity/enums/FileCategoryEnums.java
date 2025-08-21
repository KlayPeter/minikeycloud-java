package com.easypan.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author VectorX
 * @version 1.0.0
 * @description
 * @date 2024/07/23
 * @see Enum
 */
public enum FileCategoryEnums
{
    VIDEO(1, "video", "视频"),
    MUSIC(2, "music", "音频"),
    IMAGE(3, "image", "图片"),
    DOC(4, "doc", "文档"),
    OTHERS(5, "others", "其他");

    @Getter
    private Integer category;

    @Getter
    private String code;

    private String desc;

    FileCategoryEnums(Integer category, String code, String desc) {
        this.category = category;
        this.code = code;
        this.desc = desc;
    }

    public Integer getCategory() {
        return category;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static FileCategoryEnums getByCode(String code) {
        for (FileCategoryEnums item : FileCategoryEnums.values()) {
            if (item
                    .getCode()
                    .equals(code)) {
                return item;
            }
        }
        return null;
    }

}
