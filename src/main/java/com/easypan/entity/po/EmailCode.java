package com.easypan.entity.po;

import com.easypan.entity.enums.DateTimePatternEnum;
import com.easypan.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


/**
 * 邮箱验证码
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailCode implements Serializable {

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 编号
	 */
	private String code;

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	/**
	 * 0:未使用 1:已使用
	 */
	private Integer status;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public static EmailCodeBuilder builder() {
		return new EmailCodeBuilder();
	}

	public static class EmailCodeBuilder {
		private String email;
		private String code;
		private Date createTime;
		private Integer status;

		public EmailCodeBuilder email(String email) {
			this.email = email;
			return this;
		}

		public EmailCodeBuilder code(String code) {
			this.code = code;
			return this;
		}

		public EmailCodeBuilder createTime(Date createTime) {
			this.createTime = createTime;
			return this;
		}

		public EmailCodeBuilder status(Integer status) {
			this.status = status;
			return this;
		}

		public EmailCode build() {
			EmailCode emailCode = new EmailCode();
			emailCode.email = this.email;
			emailCode.code = this.code;
			emailCode.createTime = this.createTime;
			emailCode.status = this.status;
			return emailCode;
		}
	}

	@Override
	public String toString (){
		return "邮箱:"+(email == null ? "空" : email)+"，编号:"+(code == null ? "空" : code)+"，创建时间:"+(createTime == null ? "空" : DateUtil.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+"，0:未使用 1:已使用:"+(status == null ? "空" : status);
	}
}
