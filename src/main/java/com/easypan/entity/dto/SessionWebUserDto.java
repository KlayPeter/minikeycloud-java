package com.easypan.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-07-20 11:44:28
 */
// 序列化忽略未知属性
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
// 防止反序列化时报错
@NoArgsConstructor
// 防止@Builder报错
@AllArgsConstructor
@Builder
public class SessionWebUserDto
{
    private String userId;
    private String nickName;
    private Boolean isAdmin;
    private String avatar;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public static SessionWebUserDtoBuilder builder() {
        return new SessionWebUserDtoBuilder();
    }

    public static class SessionWebUserDtoBuilder {
        private String userId;
        private String nickName;
        private Boolean isAdmin;
        private String avatar;

        public SessionWebUserDtoBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public SessionWebUserDtoBuilder nickName(String nickName) {
            this.nickName = nickName;
            return this;
        }

        public SessionWebUserDtoBuilder isAdmin(Boolean isAdmin) {
            this.isAdmin = isAdmin;
            return this;
        }

        public SessionWebUserDtoBuilder avatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public SessionWebUserDto build() {
            SessionWebUserDto sessionWebUserDto = new SessionWebUserDto();
            sessionWebUserDto.userId = this.userId;
            sessionWebUserDto.nickName = this.nickName;
            sessionWebUserDto.isAdmin = this.isAdmin;
            sessionWebUserDto.avatar = this.avatar;
            return sessionWebUserDto;
        }
    }
}
