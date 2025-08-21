package com.easypan.entity.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-07-23 20:20:11
 */
@Getter
@Setter
public class UploadFileDto
{
    /**
     * 文件 ID
     */
    String fileId;

    /**
     * 文件 PID
     */
    String filePid;

    /**
     * 文件
     */
    MultipartFile file;

    /**
     * 文件名
     */
    String fileName;

    /**
     * 文件 MD5
     */
    String fileMd5;

    /**
     * 块索引
     */
    Integer chunkIndex;

    /**
     * 块合计
     */
    Integer chunks;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFilePid() {
        return filePid;
    }

    public void setFilePid(String filePid) {
        this.filePid = filePid;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public Integer getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(Integer chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public Integer getChunks() {
        return chunks;
    }

    public void setChunks(Integer chunks) {
        this.chunks = chunks;
    }
}
