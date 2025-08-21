package com.easypan.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-07-18 23:00:46
 */
// 序列化忽略未知属性
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
// 防止反序列化时报错
@NoArgsConstructor
@Accessors(chain = true)
public class SysSettingsDto implements Serializable
{
    /**
     * 注册邮件主题
     */
    private String registerEmailTitle = "EasyPan邮箱验证码";

    /**
     * 注册电子邮件内容
     */
    private String registerEmailContent = "您的验证码是：%s，15分钟内有效，请尽快在页面中输入验证码完成注册。";

    /**
     * 注册初始空间5M
     */
    private Integer userInitUseSpace = 5;

    public String getRegisterEmailTitle() {
        return registerEmailTitle;
    }

    public void setRegisterEmailTitle(String registerEmailTitle) {
        this.registerEmailTitle = registerEmailTitle;
    }

    public String getRegisterEmailContent() {
        return registerEmailContent;
    }

    public void setRegisterEmailContent(String registerEmailContent) {
        this.registerEmailContent = registerEmailContent;
    }

    public Integer getUserInitUseSpace() {
        return userInitUseSpace;
    }

    public void setUserInitUseSpace(Integer userInitUseSpace) {
        this.userInitUseSpace = userInitUseSpace;
    }
}
