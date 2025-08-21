package com.easypan.entity.query;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 参数
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class UserInfoQuery extends BaseParam
{
    /**
     * 用户ID
     */
    private String userId;

    private String userIdFuzzy;

    /**
     * 昵称
     */
    private String nickName;

    private String nickNameFuzzy;

    /**
     * 邮箱
     */
    private String email;

    private String emailFuzzy;

    /**
     * qqOpenID
     */
    private String qqOpenId;

    private String qqOpenIdFuzzy;

    /**
     * qq头像
     */
    private String qqAvatar;

    private String qqAvatarFuzzy;

    /**
     * 密码
     */
    private String password;

    private String passwordFuzzy;

    /**
     * 加入时间
     */
    private String joinTime;

    private String joinTimeStart;

    private String joinTimeEnd;

    /**
     * 最后登录时间
     */
    private String lastLoginTime;

    private String lastLoginTimeStart;

    private String lastLoginTimeEnd;

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
}
