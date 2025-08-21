package com.easypan.service.impl;

import com.easypan.component.RedisComponent;
import com.easypan.entity.config.AppConfig;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.constants.VerificationCodeConstants;
import com.easypan.entity.dto.SysSettingsDto;
import com.easypan.entity.enums.PageSize;
import com.easypan.entity.po.EmailCode;
import com.easypan.entity.po.UserInfo;
import com.easypan.entity.query.EmailCodeQuery;
import com.easypan.entity.query.SimplePage;
import com.easypan.entity.query.UserInfoQuery;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.exception.BusinessException;
import com.easypan.mappers.EmailCodeMapper;
import com.easypan.mappers.UserInfoMapper;
import com.easypan.service.EmailCodeService;
import com.easypan.utils.RandomUtils;
import com.easypan.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 邮箱验证码 业务接口实现
 */
@Service("emailCodeService")
@Slf4j
public class EmailCodeServiceImpl implements EmailCodeService
{
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EmailCodeServiceImpl.class);
    @Resource
    private EmailCodeMapper<EmailCode, EmailCodeQuery> emailCodeMapper;

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private AppConfig appConfig;

    @Resource
    private RedisComponent redisComponent;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<EmailCode> findListByParam(EmailCodeQuery param) {
        return this.emailCodeMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(EmailCodeQuery param) {
        return this.emailCodeMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<EmailCode> findListByPage(EmailCodeQuery param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ?
                       PageSize.SIZE15.getSize() :
                       param.getPageSize();

        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<EmailCode> list = this.findListByParam(param);
        PaginationResultVO<EmailCode> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    @Override
    public Integer add(EmailCode bean) {
        return this.emailCodeMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<EmailCode> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.emailCodeMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(List<EmailCode> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.emailCodeMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(EmailCode bean, EmailCodeQuery param) {
        StringUtils.checkParam(param);
        return this.emailCodeMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(EmailCodeQuery param) {
        StringUtils.checkParam(param);
        return this.emailCodeMapper.deleteByParam(param);
    }

    /**
     * 根据EmailAndCode获取对象
     */
    @Override
    public EmailCode getEmailCodeByEmailAndCode(String email, String code) {
        return this.emailCodeMapper.selectByEmailAndCode(email, code);
    }

    /**
     * 根据EmailAndCode修改
     */
    @Override
    public Integer updateEmailCodeByEmailAndCode(EmailCode bean, String email, String code) {
        return this.emailCodeMapper.updateByEmailAndCode(bean, email, code);
    }

    /**
     * 根据EmailAndCode删除
     */
    @Override
    public Integer deleteEmailCodeByEmailAndCode(String email, String code) {
        return this.emailCodeMapper.deleteByEmailAndCode(email, code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendEmailCode(String email, Integer type) {
        // 0:注册 1:找回密码
        if (Objects.equals(type, Constants.ZERO)) {
            final UserInfo userInfo = userInfoMapper.selectByEmail(email);
            if (userInfo != null) {
                throw new RuntimeException("该邮箱已被注册");
            }
        }

        // 生成验证码
        final String code = RandomUtils.getRandomNumber(VerificationCodeConstants.LENGTH_CODE_5);
        log.debug("验证码：{}", code);

        // 发送验证码
        sendEmailCode(email, code);

        // 禁用之前验证码
        emailCodeMapper.disableEmailCode(email);

        // 保存验证码
        final EmailCode emailCode = EmailCode
                .builder()
                .email(email)
                .code(code)
                .status(Constants.ZERO)
                .createTime(new Date())
                .build();
        emailCodeMapper.insert(emailCode);

    }

    /**
     * 发送电子邮件验证码
     *
     * @param toEmail 电子邮件
     * @param code    验证码
     */
    private void sendEmailCode(String toEmail, String code) {
        try {
            // 获取邮件注册系统设置
            final SysSettingsDto sysSettingsDto = redisComponent.getSysSettings();

            final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            // 发件人
            helper.setFrom(appConfig.getSendUserName());
            // 收件人
            helper.setTo(toEmail);
            // 邮件主题
            helper.setSubject(sysSettingsDto.getRegisterEmailTitle());
            // 邮件内容
            helper.setText(String.format(sysSettingsDto.getRegisterEmailContent(), code));
            // 发送时间
            helper.setSentDate(new Date());
            javaMailSender.send(mimeMessage);
        }
        catch (MessagingException e) {
            log.error("邮件发送失败：{}", e.getMessage(), e);
            throw new BusinessException("邮件发送失败");
        }
    }

    @Override
    public void checkEmailCode(String email, String code) {
        final EmailCode emailCode = getEmailCodeByEmailAndCode(email, code);
        if (emailCode == null) {
            throw new BusinessException("邮箱验证码错误");
        }

        // 验证码已使用或已失效
        if (emailCode.getStatus() == 1 || System.currentTimeMillis() - emailCode
                .getCreateTime()
                .getTime() > Constants.TIMEOUT_MINUTE_15) {
            throw new BusinessException("邮箱验证码已失效");
        }
        this.emailCodeMapper.disableEmailCode(email);
    }

}
