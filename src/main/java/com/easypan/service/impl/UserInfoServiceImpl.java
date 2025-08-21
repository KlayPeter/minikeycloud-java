package com.easypan.service.impl;

import com.easypan.component.RedisComponent;
import com.easypan.entity.config.AppConfig;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.dto.QQInfoDto;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.dto.UserSpaceDto;
import com.easypan.entity.enums.PageSize;
import com.easypan.entity.enums.UserStatusEnum;
import com.easypan.entity.po.FileInfo;
import com.easypan.entity.po.UserInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.query.SimplePage;
import com.easypan.entity.query.UserInfoQuery;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.exception.BusinessException;
import com.easypan.mappers.FileInfoMapper;
import com.easypan.mappers.UserInfoMapper;
import com.easypan.service.EmailCodeService;
import com.easypan.service.UserInfoService;
import com.easypan.utils.EncodeUtils;
import com.easypan.utils.HttpUtils;
import com.easypan.utils.JsonUtils;
import com.easypan.utils.RandomUtils;
import com.easypan.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 业务接口实现
 */
@Service("userInfoService")
@Slf4j
public class UserInfoServiceImpl implements UserInfoService
{
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserInfoServiceImpl.class);

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    @Resource
    private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;

    @Resource
    private EmailCodeService emailCodeService;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private AppConfig appConfig;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<UserInfo> findListByParam(UserInfoQuery param) {
        return this.userInfoMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(UserInfoQuery param) {
        return this.userInfoMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<UserInfo> findListByPage(UserInfoQuery param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ?
                       PageSize.SIZE15.getSize() :
                       param.getPageSize();

        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<UserInfo> list = this.findListByParam(param);
        PaginationResultVO<UserInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    @Override
    public Integer add(UserInfo bean) {
        return this.userInfoMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<UserInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userInfoMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(List<UserInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userInfoMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(UserInfo bean, UserInfoQuery param) {
        StringUtils.checkParam(param);
        return this.userInfoMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(UserInfoQuery param) {
        StringUtils.checkParam(param);
        return this.userInfoMapper.deleteByParam(param);
    }

    /**
     * 根据UserId获取对象
     */
    @Override
    public UserInfo getUserInfoByUserId(String userId) {
        return this.userInfoMapper.selectByUserId(userId);
    }

    /**
     * 根据UserId修改
     */
    @Override
    public Integer updateUserInfoByUserId(UserInfo bean, String userId) {
        return this.userInfoMapper.updateByUserId(bean, userId);
    }

    /**
     * 根据UserId删除
     */
    @Override
    public Integer deleteUserInfoByUserId(String userId) {
        return this.userInfoMapper.deleteByUserId(userId);
    }

    /**
     * 根据Email获取对象
     */
    @Override
    public UserInfo getUserInfoByEmail(String email) {
        return this.userInfoMapper.selectByEmail(email);
    }

    /**
     * 根据Email修改
     */
    @Override
    public Integer updateUserInfoByEmail(UserInfo bean, String email) {
        return this.userInfoMapper.updateByEmail(bean, email);
    }

    /**
     * 根据Email删除
     */
    @Override
    public Integer deleteUserInfoByEmail(String email) {
        return this.userInfoMapper.deleteByEmail(email);
    }

    /**
     * 根据QqOpenId获取对象
     */
    @Override
    public UserInfo getUserInfoByQqOpenId(String qqOpenId) {
        return this.userInfoMapper.selectByQqOpenId(qqOpenId);
    }

    /**
     * 根据QqOpenId修改
     */
    @Override
    public Integer updateUserInfoByQqOpenId(UserInfo bean, String qqOpenId) {
        return this.userInfoMapper.updateByQqOpenId(bean, qqOpenId);
    }

    /**
     * 根据QqOpenId删除
     */
    @Override
    public Integer deleteUserInfoByQqOpenId(String qqOpenId) {
        return this.userInfoMapper.deleteByQqOpenId(qqOpenId);
    }

    /**
     * 根据NickName获取对象
     */
    @Override
    public UserInfo getUserInfoByNickName(String nickName) {
        return this.userInfoMapper.selectByNickName(nickName);
    }

    /**
     * 根据NickName修改
     */
    @Override
    public Integer updateUserInfoByNickName(UserInfo bean, String nickName) {
        return this.userInfoMapper.updateByNickName(bean, nickName);
    }

    /**
     * 根据NickName删除
     */
    @Override
    public Integer deleteUserInfoByNickName(String nickName) {
        return this.userInfoMapper.deleteByNickName(nickName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(String email, String nickName, String password, String emailCode) {
        UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
        if (userInfo != null) {
            throw new BusinessException("邮箱已被注册");
        }
        userInfo = this.userInfoMapper.selectByNickName(nickName);
        if (userInfo != null) {
            throw new BusinessException("昵称已被注册");
        }

        // 校验邮箱验证码
        emailCodeService.checkEmailCode(email, emailCode);

        // 添加用户
        this.userInfoMapper.insert(UserInfo
                // 构建用户
                .builder()
                // 用户ID
                .userId(RandomUtils.getRandomNumber(Constants.LENGTH_10))
                // 昵称
                .nickName(nickName)
                // 邮箱
                .email(email)
                // 密码
                .password(EncodeUtils.encodeByMD5(password))
                // 注册时间
                .joinTime(new Date())
                // 默认启用
                .status(UserStatusEnum.ENABLE.getStatus())
                // 已使用空间，单位为Byte
                .useSpace(BigDecimal.ZERO.longValue())
                // 总空间，单位为Byte
                .totalSpace(redisComponent
                        .getSysSettings()
                        .getUserInitUseSpace() * Constants.MB)
                .build());
    }

    @Override
    public SessionWebUserDto login(String email, String password) {
        final UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
        if (userInfo == null || !userInfo
                .getPassword()
                .equals(password)) {
            throw new BusinessException("账号或密码错误");
        }

        if (UserStatusEnum.DISABLE
                .getStatus()
                .equals(userInfo.getStatus())) {
            throw new BusinessException("账号已被禁用");
        }

        // 更新登录时间
        this.userInfoMapper.updateByUserId(UserInfo
                .builder()
                .lastLoginTime(new Date())
                .build(), userInfo.getUserId());

        // 缓存用户空间使用情况
        final Long useSpace = fileInfoMapper.selectUseSpace(userInfo.getUserId());
        redisComponent.saveUserSpaceUse(userInfo.getUserId(), UserSpaceDto
                // 构建UserSpaceDto
                .builder()
                // 已使用空间
                .useSpace(useSpace)
                // 总空间
                .totalSpace(userInfo.getTotalSpace())
                .build());

        // 构建SessionWebUserDto
        return SessionWebUserDto
                // 构建SessionWebUserDto
                .builder()
                // 用户ID
                .userId(userInfo.getUserId())
                // 昵称
                .nickName(userInfo.getNickName())
                // 是否是管理员
                .isAdmin(ArrayUtils.contains(appConfig
                        .getAdminEmails()
                        .split(","), email))
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPwd(String email, String password, String emailCode) {
        final UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
        if (userInfo == null) {
            throw new BusinessException("邮箱账号不存在");
        }

        // 校验邮箱验证码
        emailCodeService.checkEmailCode(email, emailCode);

        // 修改密码
        this.userInfoMapper.updateByEmail(UserInfo
                .builder()
                .password(EncodeUtils.encodeByMD5(password))
                .build(), email);
    }

    @Override
    public SessionWebUserDto qqLogin(String code) {
        // 1、通过 code 获取 assess_token
        final String accessToken = this.getQQAccessToken(code);
        // 2、通过 access_token 获取 openid
        final String qqOpenId = this.getQQOpenId(accessToken);
        // 判断用户是否存在
        UserInfo userInfo = this.userInfoMapper.selectByQqOpenId(qqOpenId);
        if (userInfo == null) {
            // 3、通过 access_token 和 openid 获取用户信息
            final QQInfoDto qqInfoDto = this.getQQUserInfo(accessToken, qqOpenId);
            // 添加用户
            userInfo = UserInfo
                    .builder()
                    .userId(RandomUtils.getRandomNumber(Constants.LENGTH_10))
                    .qqOpenId(qqOpenId)
                    .nickName(qqInfoDto
                            .getNickName()
                            .substring(0, Constants.LENGTH_20))
                    .qqAvatar(StringUtils.isEmpty(qqInfoDto.getFigureurl_qq_2()) ?
                              qqInfoDto.getFigureurl_qq_1() :
                              qqInfoDto.getFigureurl_qq_2())
                    .joinTime(new Date())
                    .lastLoginTime(new Date())
                    .status(UserStatusEnum.ENABLE.getStatus())
                    .useSpace(BigDecimal.ZERO.longValue())
                    .totalSpace(redisComponent
                            .getSysSettings()
                            .getUserInitUseSpace() * Constants.MB)
                    .build();
            this.userInfoMapper.insert(userInfo);
        }
        else {
            this.userInfoMapper.updateByQqOpenId(UserInfo
                    .builder()
                    .lastLoginTime(new Date())
                    .build(), qqOpenId);
        }

        // 缓存用户空间使用情况
        final Long useSpace = fileInfoMapper.selectUseSpace(userInfo.getUserId());
        redisComponent.saveUserSpaceUse(userInfo.getUserId(), UserSpaceDto
                // 构建UserSpaceDto
                .builder()
                // 已使用空间
                .useSpace(useSpace)
                // 总空间
                .totalSpace(userInfo.getTotalSpace())
                .build());

        // 构建SessionWebUserDto
        final String email = userInfo.getEmail() == null ?
                             "" :
                             userInfo.getEmail();
        return SessionWebUserDto
                .builder()
                .userId(userInfo.getUserId())
                .nickName(userInfo.getNickName())
                .avatar(userInfo.getQqAvatar())
                .isAdmin(ArrayUtils.contains(appConfig
                        .getAdminEmails()
                        .split(","), email))
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(String userId, Integer status) {
        final UserInfo userInfo = this.getUserInfoByUserId(userId);
        userInfo.setStatus(status);
        if (UserStatusEnum.DISABLE
                .getStatus()
                .equals(status)) {
            userInfo.setUseSpace(0L);
            this.fileInfoMapper.deleteByUserId(userId);
        }
        this.userInfoMapper.updateByUserId(userInfo, userId);
    }

    @Override
    public void updateUserSpace(String userId, Integer changeSpace) {
        this.userInfoMapper.updateUserSpace(userId, null, changeSpace * Constants.MB);
        // 更新缓存
        this.redisComponent.resetUserSpaceUse(userId);
    }

    /**
     * 获取 QQ 登录令牌
     *
     * @param code code
     * @return {@link String }
     */
    private String getQQAccessToken(String code) {
        // 构建url
        String url = null;
        try {
            url = String.format(appConfig.getQqUrlAccessToken(), appConfig.getQqAppId(), appConfig.getQqAppKey(), code,
                    URLEncoder.encode(appConfig.getQqUrlRedirect(), StandardCharsets.UTF_8.name()));
        }
        catch (Exception e) {
            log.error("获取qq登录令牌失败：{}", e.getMessage(), e);
        }

        // 发送请求
        final String result = HttpUtils.sendRequest(url);
        if (org.apache.commons.lang3.StringUtils.isBlank(result) || result.contains(Constants.VIEW_OBJ_RESULT_KEY)) {
            log.error("获取qq登录令牌失败：{}", result);
            throw new BusinessException("获取qq登录令牌失败");
        }

        // 获取 access_token
        final String[] params = result.split("&");
        return Arrays
                .stream(params)
                .filter(param -> param.contains("access_token"))
                .findFirst()
                .map(param -> param.split("=")[1])
                .orElse(null);
    }

    /**
     * 获取 QQ Open ID
     *
     * @param accessToken 访问令牌
     * @return {@link String }
     */
    private String getQQOpenId(String accessToken) {
        // 构建url
        final String url = String.format(appConfig.getQqUrlOpenId(), accessToken);
        // 发送请求
        final String result = HttpUtils.sendRequest(url);
        // 获取返回值
        final String tmpJSON = this.getQQResp(result);
        if (StringUtils.isEmpty(tmpJSON)) {
            log.error("获取openid失败：url={}, tmpJSON={}", url, tmpJSON);
            throw new BusinessException("获取openid失败");
        }

        // 转换为json
        final Map jsonData = JsonUtils.convertJson2Obj(tmpJSON, Map.class);
        if (jsonData == null || jsonData.containsKey(Constants.VIEW_OBJ_RESULT_KEY)) {
            log.error("获取openid失败：url={}, jsonData={}", url, jsonData);
            throw new BusinessException("获取openid失败");
        }

        // 返回 openid
        return String.valueOf(jsonData.get("openid"));
    }

    private String getQQResp(String result) {
        if (StringUtils.isEmpty(result)) {
            return null;
        }
        final int pos = result.indexOf("callback");
        if (pos == -1) {
            return null;
        }
        final int start = result.indexOf("{");
        final int end = result.lastIndexOf("}");
        return result.substring(start + 1, end + 1);
    }

    /**
     * 获取 QQ 用户信息
     *
     * @param accessToken 访问令牌
     * @param openId      openIdID
     * @return {@link QQInfoDto }
     */
    private QQInfoDto getQQUserInfo(String accessToken, String openId) {
        // 构建url
        final String url = String.format(appConfig.getQqUrlUserInfo(), accessToken, appConfig.getQqAppId(), openId);
        // 发送请求
        final String response = HttpUtils.sendRequest(url);
        if (StringUtils.isEmpty(response)) {
            throw new BusinessException("获取用户信息失败");
        }

        // 转换为对象
        final QQInfoDto qqInfoDto = JsonUtils.convertJson2Obj(response, QQInfoDto.class);
        if (qqInfoDto.getRet() != 0) {
            log.error("获取用户信息失败：url={}, msg={}", url, qqInfoDto.getMsg());
            throw new BusinessException("获取用户信息失败");
        }
        return qqInfoDto;
    }
}
