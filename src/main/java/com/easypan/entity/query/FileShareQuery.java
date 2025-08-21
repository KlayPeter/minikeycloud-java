package com.easypan.entity.query;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 文件分享信息参数
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Accessors(chain = true)
public class FileShareQuery extends BaseParam
{

    /**
     * 分享ID
     */
    private String shareId;

    private String shareIdFuzzy;

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
     * 提取码
     */
    private String code;

    private String codeFuzzy;

    /**
     * 浏览次数
     */
    private Integer showCount;

    /**
     * 是否查询文件名
     */
    private Boolean queryFileName;

    public void setQueryFileName(Boolean queryFileName) {
        this.queryFileName = queryFileName;
    }

    public Boolean getQueryFileName() {
        return queryFileName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
