package com.easypan.entity.query;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 笔记信息查询参数
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Accessors(chain = true)
public class NoteInfoQuery extends BaseParam {

    /**
     * 笔记ID
     */
    private String noteId;

    private String noteIdFuzzy;

    /**
     * 用户ID
     */
    private String userId;

    private String userIdFuzzy;

    /**
     * 笔记标题
     */
    private String title;

    private String titleFuzzy;

    /**
     * 内容类型 1:markdown 2:富文本
     */
    private Integer contentType;

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
     * 状态 0:删除 1:正常
     */
    private Integer status;

    /**
     * 是否公开 0:私有 1:公开
     */
    private Integer isPublic;

    /**
     * 笔记ID数组
     */
    private String[] noteIdArray;

    /**
     * 排除的笔记ID数组
     */
    private String[] excludedNoteIdArray;

    /**
     * 是否查询用户昵称
     */
    private Boolean queryNickName;

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setContentType(Integer contentType) {
        this.contentType = contentType;
    }

    public Integer getContentType() {
        return contentType;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setIsPublic(Integer isPublic) {
        this.isPublic = isPublic;
    }

    public Integer getIsPublic() {
        return isPublic;
    }

    public void setNoteIdArray(String[] noteIdArray) {
        this.noteIdArray = noteIdArray;
    }

    public String[] getNoteIdArray() {
        return noteIdArray;
    }

    public void setExcludedNoteIdArray(String[] excludedNoteIdArray) {
        this.excludedNoteIdArray = excludedNoteIdArray;
    }

    public String[] getExcludedNoteIdArray() {
        return excludedNoteIdArray;
    }

    public void setQueryNickName(Boolean queryNickName) {
        this.queryNickName = queryNickName;
    }

    public Boolean getQueryNickName() {
        return queryNickName;
    }
}