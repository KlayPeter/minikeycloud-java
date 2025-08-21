package com.easypan.entity.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-07-18 22:42:49
 */
@Component("appConfig")
@Getter
public class AppConfig
{
    @Value("${spring.mail.username:}")
    private String sendUserName;

    public String getSendUserName() {
        return sendUserName;
    }

    @Value("${admin.emails:}")
    private String adminEmails;

    public String getAdminEmails() {
        return adminEmails;
    }

    @Value("${project.folder}")
    private String projectFolder;

    // ==================================  QQ登录相关 ============================
    @Value("${qq.app.id}")
    private String qqAppId;

    @Value("${qq.app.key}")
    private String qqAppKey;

    @Value("${qq.url.authorization}")
    private String qqUrlAuthorization;

    @Value("${qq.url.access.token}")
    private String qqUrlAccessToken;

    @Value("${qq.url.openid}")
    private String qqUrlOpenId;

    @Value("${qq.url.user.info}")
    private String qqUrlUserInfo;

    @Value("${qq.url.redirect}")
    private String qqUrlRedirect;

    public String getQqAppId() {
        return qqAppId;
    }

    public String getQqAppKey() {
        return qqAppKey;
    }

    public String getQqUrlAuthorization() {
        return qqUrlAuthorization;
    }

    public String getQqUrlAccessToken() {
        return qqUrlAccessToken;
    }

    public String getQqUrlOpenId() {
        return qqUrlOpenId;
    }

    public String getQqUrlUserInfo() {
        return qqUrlUserInfo;
    }

    public String getQqUrlRedirect() {
        return qqUrlRedirect;
    }

    public String getProjectFolder() {
        return projectFolder;
    }
}
