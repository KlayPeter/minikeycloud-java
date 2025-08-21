package com.easypan.entity.query;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 文件信息表参数
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Accessors(chain = true)
public class FileInfoQuery extends BaseParam
{

    /**
     * 文件ID
     */
    private String fileId;

    private String fileIdFuzzy;

    /**
     * 用户ID
     */
    private String userId;

    private String userIdFuzzy;

    /**
     * 文件MD5值
     */
    private String fileMd5;

    private String fileMd5Fuzzy;

    /**
     * 文件父级ID
     */
    private String filePid;

    private String filePidFuzzy;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件名
     */
    private String fileName;

    private String fileNameFuzzy;

    /**
     * 文件封面
     */
    private String fileCover;

    private String fileCoverFuzzy;

    /**
     * 文件路径
     */
    private String filePath;

    private String filePathFuzzy;

    /**
     * 创建时间
     */
    private String createTime;

    private String createTimeStart;

    private String createTimeEnd;

    /**
     * 最后更新时间
     */
    private String lastUpdateTime;

    private String lastUpdateTimeStart;

    private String lastUpdateTimeEnd;

    /**
     * 文件夹类型  0:文件  1:目录
     */
    private Integer folderType;

    /**
     * 文件分类  1:视频  2:音频  3:图片  4:文档  5:其他
     */
    private Integer fileCategory;

    /**
     * 文件类型  1:视频  2:音频  3:图片  4:pdf  5:doc  6:excel  7:txt  8:code  9:zip  10:其他
     */
    private Integer fileType;

    /**
     * 0:转码中  1:转码失败  2:转码成功
     */
    private Integer status;

    /**
     * 进入回收站时间
     */
    private String recoveryTime;

    private String recoveryTimeStart;

    private String recoveryTimeEnd;

    /**
     * 删除标记  0:删除  1:回收站  2:正常
     */
    private Integer delFlag;

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setExcludedFileIdArray(String[] excludedFileIdArray) {
        this.excludedFileIdArray = excludedFileIdArray;
    }

    public String[] getExcludedFileIdArray() {
        return excludedFileIdArray;
    }

    public void setFilePid(String filePid) {
        this.filePid = filePid;
    }

    public String getFilePid() {
        return filePid;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public void setFileCategory(Integer fileCategory) {
        this.fileCategory = fileCategory;
    }

    public Integer getFileCategory() {
        return fileCategory;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getFileId() {
        return fileId;
    }

    /**
     * 文件ID集合
     */
    private String[] fileIdArray;

    /**
     * 排除文件ID集合
     */
    private String[] excludedFileIdArray;

    /**
     * 是否查询昵称
     */
    private Boolean queryNickName;

    /**
     * 是否查询过期时间
     */
    private Boolean queryExpireTime;

    public void setQueryNickName(Boolean queryNickName) {
        this.queryNickName = queryNickName;
    }

    public Boolean getQueryNickName() {
        return queryNickName;
    }


}
