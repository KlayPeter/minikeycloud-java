package com.easypan.entity.query;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 笔记分享查询参数
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Accessors(chain = true)
public class NoteShareQuery extends BaseParam {

    /**
     * 分享ID
     */
    private String shareId;

    private String shareIdFuzzy;

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
     * 分享标题
     */
    private String shareTitle;

    private String shareTitleFuzzy;

    /**
     * 有效期类型 0:1天 1:7天 2:30天 3:永久有效
     */
    private Integer validType;

    /**
     * 过期时间
     */
    private String expireTime;

    private String expireTimeStart;

    private String expireTimeEnd;

    /**
     * 分享时间
     */
    private String shareTime;

    private String shareTimeStart;

    private String shareTimeEnd;

    /**
     * 状态 0:取消分享 1:正常分享
     */
    private Integer status;

    /**
     * 分享ID数组
     */
    private String[] shareIdArray;

    /**
     * 是否查询笔记信息
     */
    private Boolean queryNoteInfo;

    /**
     * 是否查询用户昵称
     */
    private Boolean queryNickName;

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getShareId() {
        return shareId;
    }

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

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setValidType(Integer validType) {
        this.validType = validType;
    }

    public Integer getValidType() {
        return validType;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setShareIdArray(String[] shareIdArray) {
        this.shareIdArray = shareIdArray;
    }

    public String[] getShareIdArray() {
        return shareIdArray;
    }

    public void setQueryNoteInfo(Boolean queryNoteInfo) {
        this.queryNoteInfo = queryNoteInfo;
    }

    public Boolean getQueryNoteInfo() {
        return queryNoteInfo;
    }

    public void setQueryNickName(Boolean queryNickName) {
        this.queryNickName = queryNickName;
    }

    public Boolean getQueryNickName() {
        return queryNickName;
    }
}