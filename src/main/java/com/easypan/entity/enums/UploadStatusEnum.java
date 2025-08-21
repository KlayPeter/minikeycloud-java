package com.easypan.entity.enums;

import lombok.Getter;

/**
 * @author VectorX
 * @version 1.0.0
 * @description 上传状态枚举
 * @date 2024/07/25
 * @see Enum
 */
@Getter
public enum UploadStatusEnum
{
    UPLOAD_SECONDS("upload_seconds", "秒传"),
    UPLOADING("uploading", "上传中"),
    UPLOAD_FINISH("upload_finish", "上传完成");

    private String code;

    private String desc;

    UploadStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
