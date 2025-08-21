package com.easypan.entity.vo;

import com.easypan.entity.enums.DateTimePatternEnum;
import com.easypan.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 *
 */
@Getter
@Setter
@Accessors(chain = true)
public class UserInfoVO
{

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * qqOpenID
     */
    private String qqOpenId;

    /**
     * qq头像
     */
    private String qqAvatar;

    /**
     * 密码
     */
    private String password;

    /**
     * 加入时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",
                timezone = "GMT+8")
    private Date joinTime;

    /**
     * 最后登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",
                timezone = "GMT+8")
    private Date lastLoginTime;

    /**
     * 0:禁用 1:启用
     */
    private Integer status;

    /**
     * 使用空间 单位:byte
     */
    private Long useSpace;

    /**
     * 总空间 单位:byte
     */
    private Long totalSpace;

    @Override
    public String toString() {
        return "用户ID:" + (userId == null ?
                          "空" :
                          userId) + "，昵称:" + (nickName == null ?
                                              "空" :
                                              nickName) + "，邮箱:" + (email == null ?
                                                                    "空" :
                                                                    email) + "，qqOpenID:" + (qqOpenId == null ?
                                                                                             "空" :
                                                                                             qqOpenId) + "，qq头像:" + (qqAvatar == null ?
                                                                                                                     "空" :
                                                                                                                     qqAvatar) + "，密码:" + (password == null ?
                                                                                                                                           "空" :
                                                                                                                                           password) + "，加入时间:" + (joinTime == null ?
                                                                                                                                                                   "空" :
                                                                                                                                                                   DateUtil.format(
                                                                                                                                                                           joinTime,
                                                                                                                                                                           DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + "，最后登录时间:" + (lastLoginTime == null ?
                                                                                                                                                                                                                                                  "空" :
                                                                                                                                                                                                                                                  DateUtil.format(
                                                                                                                                                                                                                                                          lastLoginTime,
                                                                                                                                                                                                                                                          DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + "，0:禁用 1:启用:" + (status == null ?
                                                                                                                                                                                                                                                                                                                                    "空" :
                                                                                                                                                                                                                                                                                                                                    status) + "，使用空间 单位:byte:" + (useSpace == null ?
                                                                                                                                                                                                                                                                                                                                                                  "空" :
                                                                                                                                                                                                                                                                                                                                                                  useSpace) + "，总空间 单位:byte:" + (totalSpace == null ?
                                                                                                                                                                                                                                                                                                                                                                                                 "空" :
                                                                                                                                                                                                                                                                                                                                                                                                 totalSpace);
    }
}
