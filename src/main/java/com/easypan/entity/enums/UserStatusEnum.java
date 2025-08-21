package com.easypan.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author VectorX
 * @version 1.0.0
 * @description 用户状态枚举
 * @date 2024/07/23
 * @see Enum
 */
@AllArgsConstructor
@Getter
public enum UserStatusEnum
{
    DISABLE(0, "禁用"),
    ENABLE(1, "正常");

    private Integer status;
    private String desc;

    public static UserStatusEnum getByStatus(Integer status) {
        for (UserStatusEnum item : UserStatusEnum.values()) {
            if (item
                    .getStatus()
                    .equals(status)) {
                return item;
            }
        }
        return null;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
