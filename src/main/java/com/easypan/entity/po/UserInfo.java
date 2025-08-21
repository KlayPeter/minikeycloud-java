package com.easypan.entity.po;

import com.easypan.entity.enums.DateTimePatternEnum;
import com.easypan.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class UserInfo implements Serializable
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
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date joinTime;

    /**
     * 最后登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",
                timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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

    public static UserInfoBuilder builder() {
        return new UserInfoBuilder();
    }

    public String getPassword() {
        return password;
    }

    public Integer getStatus() {
        return status;
    }

    public String getUserId() {
        return userId;
    }

    public Long getTotalSpace() {
        return totalSpace;
    }

    public String getNickName() {
        return nickName;
    }

    public String getEmail() {
        return email;
    }

    public String getQqOpenId() {
        return qqOpenId;
    }

    public String getQqAvatar() {
        return qqAvatar;
    }

    public Date getJoinTime() {
        return joinTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public Long getUseSpace() {
        return useSpace;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setQqOpenId(String qqOpenId) {
        this.qqOpenId = qqOpenId;
    }

    public void setQqAvatar(String qqAvatar) {
        this.qqAvatar = qqAvatar;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setJoinTime(Date joinTime) {
        this.joinTime = joinTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setUseSpace(Long useSpace) {
        this.useSpace = useSpace;
    }

    public void setTotalSpace(Long totalSpace) {
        this.totalSpace = totalSpace;
    }

    public static class UserInfoBuilder {
        private String userId;
        private String nickName;
        private String email;
        private String qqOpenId;
        private String qqAvatar;
        private String password;
        private Date joinTime;
        private Date lastLoginTime;
        private Integer status;
        private Long useSpace;
        private Long totalSpace;

        public UserInfoBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public UserInfoBuilder nickName(String nickName) {
            this.nickName = nickName;
            return this;
        }

        public UserInfoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserInfoBuilder qqOpenId(String qqOpenId) {
            this.qqOpenId = qqOpenId;
            return this;
        }

        public UserInfoBuilder qqAvatar(String qqAvatar) {
            this.qqAvatar = qqAvatar;
            return this;
        }

        public UserInfoBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserInfoBuilder joinTime(Date joinTime) {
            this.joinTime = joinTime;
            return this;
        }

        public UserInfoBuilder lastLoginTime(Date lastLoginTime) {
            this.lastLoginTime = lastLoginTime;
            return this;
        }

        public UserInfoBuilder status(Integer status) {
            this.status = status;
            return this;
        }

        public UserInfoBuilder useSpace(Long useSpace) {
            this.useSpace = useSpace;
            return this;
        }

        public UserInfoBuilder totalSpace(Long totalSpace) {
            this.totalSpace = totalSpace;
            return this;
        }

        public UserInfo build() {
            UserInfo userInfo = new UserInfo();
            userInfo.userId = this.userId;
            userInfo.nickName = this.nickName;
            userInfo.email = this.email;
            userInfo.qqOpenId = this.qqOpenId;
            userInfo.qqAvatar = this.qqAvatar;
            userInfo.password = this.password;
            userInfo.joinTime = this.joinTime;
            userInfo.lastLoginTime = this.lastLoginTime;
            userInfo.status = this.status;
            userInfo.useSpace = this.useSpace;
            userInfo.totalSpace = this.totalSpace;
            return userInfo;
        }
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userId='" + userId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", email='" + email + '\'' +
                ", qqOpenId='" + qqOpenId + '\'' +
                ", qqAvatar='" + qqAvatar + '\'' +
                ", password='" + password + '\'' +
                ", joinTime=" + joinTime +
                ", lastLoginTime=" + lastLoginTime +
                ", status=" + status +
                ", useSpace=" + useSpace +
                ", totalSpace=" + totalSpace +
                '}';
    }
}
