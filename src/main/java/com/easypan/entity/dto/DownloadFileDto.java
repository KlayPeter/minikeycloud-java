package com.easypan.entity.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-07-23 19:03:51
 */
@Getter
@Setter
@NoArgsConstructor  // Redis反序列化需要
public class DownloadFileDto
{

    private String downloadCode;

    private String fileName;

    private String filePath;

    public DownloadFileDto(String downloadCode, String fileName, String filePath) {
        this.downloadCode = downloadCode;
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public String getDownloadCode() {
        return downloadCode;
    }

    public void setDownloadCode(String downloadCode) {
        this.downloadCode = downloadCode;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public static DownloadFileDtoBuilder builder() {
        return new DownloadFileDtoBuilder();
    }

    public static class DownloadFileDtoBuilder {
        private String downloadCode;
        private String fileName;
        private String filePath;

        public DownloadFileDtoBuilder downloadCode(String downloadCode) {
            this.downloadCode = downloadCode;
            return this;
        }

        public DownloadFileDtoBuilder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public DownloadFileDtoBuilder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public DownloadFileDto build() {
            return new DownloadFileDto(downloadCode, fileName, filePath);
        }
    }

}
