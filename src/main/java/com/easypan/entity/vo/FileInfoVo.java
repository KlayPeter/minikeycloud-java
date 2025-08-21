package com.easypan.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author VectorX
 * @version 1.0.0
 * @description
 * @date 2024/07/23
 */
@Data
public class FileInfoVo
{

    private String fileId;

    private String filePid;

    private Long fileSize;

    private String fileName;

    private String fileCover;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",
                timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",
                timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date recoveryTime;

    private Integer folderType;

    private Integer fileCategory;

    private Integer fileType;

    private Integer status;

    private String nickName;
}
