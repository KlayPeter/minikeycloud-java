package com.easypan.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-07-23 20:23:42
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadResultVo implements Serializable
{
    /**
     * 文件 ID
     */
    private String fileId;
    /**
     * 状态
     */
    private String status;

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
