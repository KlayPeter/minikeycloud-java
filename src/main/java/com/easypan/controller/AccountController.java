package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.component.RedisComponent;
import com.easypan.entity.config.AppConfig;
import com.easypan.entity.constants.FileFolderConstants;
import com.easypan.entity.constants.ImageConstants;
import com.easypan.entity.constants.SessionConstants;
import com.easypan.entity.constants.VerificationCodeConstants;
import com.easypan.entity.dto.CreateImageCode;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.dto.UserSpaceDto;
import com.easypan.entity.enums.VerifyRegexEnum;
import com.easypan.entity.po.UserInfo;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.exception.BusinessException;
import com.easypan.service.EmailCodeService;
import com.easypan.service.UserInfoService;
import com.easypan.utils.EncodeUtils;
import com.easypan.utils.RandomUtils;
import com.easypan.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-07-17 22:51:17
 */
@RestController
@Slf4j
public class AccountController extends ABaseController
{
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AccountController.class);

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/json;charset=UTF-8";
    private static final String IMAGE_JPG = "image/jpg";

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private EmailCodeService emailCodeService;

    @Resource
    private AppConfig appConfig;

    @Resource
    private RedisComponent redisComponent;

    @GetMapping("/checkCode")
    public void checkCode(HttpServletResponse response, HttpSession session, Integer type) throws Exception {
        CreateImageCode vCode = new CreateImageCode(130, 30, 5, 10);
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        String code = vCode.getCode();
        log.debug("验证码：" + code);
        if (type == null || type == 0) {
            session.setAttribute(VerificationCodeConstants.CHECK_CODE_KEY, code);
        }
        else {
            session.setAttribute(VerificationCodeConstants.CHECK_CODE_KEY_EMAIL, code);
        }
        vCode.write(response.getOutputStream());
    }

    @PostMapping("/sendEmailCode")
    @GlobalInterceptor(checkParams = true,
                       checkLogin = false)
    public ResponseVO sendEmailCode(HttpSession session,
                                    @VerifyParam(required = true,
                                                 regex = VerifyRegexEnum.EMAIL,
                                                 maxLength = 150)
                                            String email,
                                    @VerifyParam(required = true)
                                            String checkCode,
                                    @VerifyParam(required = true)
                                            Integer type) {
        try {
            // 校验验证码
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(VerificationCodeConstants.CHECK_CODE_KEY_EMAIL))) {
                throw new BusinessException("验证码错误");
            }

            // 发送验证码
            emailCodeService.sendEmailCode(email, type);

            // 返回成功
            // {
            // 	"status": "success",
            // 	"code": 200,
            // 	"info": "请求成功",
            // 	"data": null
            // }
            return getSuccessResponseVO(null);
        }
        finally {
            // 清除验证码
            session.removeAttribute(VerificationCodeConstants.CHECK_CODE_KEY_EMAIL);
        }
    }

    @PostMapping("/register")
    @GlobalInterceptor(checkParams = true,
                       checkLogin = false)
    public ResponseVO register(HttpSession session,
                               @VerifyParam(required = true,
                                            regex = VerifyRegexEnum.EMAIL,
                                            maxLength = 150)
                                       String email,
                               @VerifyParam(required = true)
                                       String nickName,
                               @VerifyParam(required = true,
                                            regex = VerifyRegexEnum.PASSWORD,
                                            minLength = 8,
                                            maxLength = 18)
                                       String password,
                               @VerifyParam(required = true)
                                       String checkCode,
                               @VerifyParam(required = true)
                                       String emailCode) {
        try {
            // 校验图片验证码 (临时允许测试验证码)
            String sessionCheckCode = (String) session.getAttribute(VerificationCodeConstants.CHECK_CODE_KEY);
            if (!checkCode.equalsIgnoreCase(sessionCheckCode) && !"test".equalsIgnoreCase(checkCode)) {
                throw new BusinessException("图片验证码错误");
            }

            userInfoService.register(email, nickName, password, emailCode);
            // 返回成功
            return getSuccessResponseVO(null);
        }
        finally {
            // 清除图片验证码
            session.removeAttribute(VerificationCodeConstants.CHECK_CODE_KEY);
        }
    }

    @PostMapping("/login")
    @GlobalInterceptor(checkParams = true,
                       checkLogin = false)
    public ResponseVO login(HttpSession session,
                            @VerifyParam(required = true,
                                         regex = VerifyRegexEnum.EMAIL,
                                         maxLength = 150)
                                    String email,
                            /** 登录的时候不要校验Miami长度，因为前端传递过来的是MD5密文，长度固定为32位 **/
                            @VerifyParam(required = true,
                                         regex = VerifyRegexEnum.PASSWORD)
                                    String password,
                            @VerifyParam(required = true)
                                    String checkCode) {
        try {
            // 校验图片验证码 (临时允许测试验证码)
            String sessionCheckCode = (String) session.getAttribute(VerificationCodeConstants.CHECK_CODE_KEY);
            if (!checkCode.equalsIgnoreCase(sessionCheckCode) && !"test".equalsIgnoreCase(checkCode)) {
                throw new BusinessException("图片验证码错误");
            }

            // 登录
            final SessionWebUserDto sessionWebUserDto = userInfoService.login(email, password);

            // 存入session
            session.setAttribute(SessionConstants.SESSION_KEY, sessionWebUserDto);

            // 返回成功
            return getSuccessResponseVO(sessionWebUserDto);
        }
        finally {
            // 清除图片验证码
            session.removeAttribute(VerificationCodeConstants.CHECK_CODE_KEY);
        }
    }

    @PostMapping("/resetPwd")
    @GlobalInterceptor(checkParams = true,
                       checkLogin = false)
    public ResponseVO resetPwd(HttpSession session,
                               @VerifyParam(required = true,
                                            regex = VerifyRegexEnum.EMAIL,
                                            maxLength = 150)
                                       String email,
                               @VerifyParam(required = true,
                                            regex = VerifyRegexEnum.PASSWORD,
                                            minLength = 8,
                                            maxLength = 18)
                                       String password,
                               @VerifyParam(required = true)
                                       String checkCode,
                               @VerifyParam(required = true)
                                       String emailCode) {
        try {
            // 校验图片验证码
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(VerificationCodeConstants.CHECK_CODE_KEY))) {
                throw new BusinessException("图片验证码错误");
            }

            // 登录
            userInfoService.resetPwd(email, password, emailCode);

            // 返回成功
            return getSuccessResponseVO(null);
        }
        finally {
            // 清除图片验证码
            session.removeAttribute(VerificationCodeConstants.CHECK_CODE_KEY);
        }
    }

    @GetMapping("/getAvatar/{userId}")
    @GlobalInterceptor(checkParams = true,
                       checkLogin = false)
    public void getAvatar(HttpServletResponse response,
                          @VerifyParam(required = true)
                          @PathVariable("userId")
                                  String userId) {
        // 创建头像文件夹
        final String projectFolder = appConfig.getProjectFolder();
        final String avatarFolderName = FileFolderConstants.FILE_FOLDER_FILE + FileFolderConstants.FILE_FOLDER_AVATAR_NAME;
        final File folder = new File(projectFolder + avatarFolderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // 创建头像文件
        String avatarPath = projectFolder + avatarFolderName + userId + ImageConstants.IMAGE_SUFFIX_JPG;
        if (!new File(avatarPath).exists()) {
            avatarPath = projectFolder + avatarFolderName + ImageConstants.DEFAULT_AVATAR_NAME;
            if (!new File(avatarPath).exists()) {
                printNoDefaultImage(response);
            }
        }

        // 读取文件
        response.setContentType(IMAGE_JPG);
        readFile(response, avatarPath);
    }

    /**
     * 处理无默认头像
     *
     * @param response 响应
     */
    private void printNoDefaultImage(HttpServletResponse response) {
        response.setHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE);
        response.setStatus(HttpStatus.OK.value());
        try (PrintWriter writer = response.getWriter()) {
            writer.write(MessageFormat.format("请在头像目录下放置默认头像{0}", ImageConstants.DEFAULT_AVATAR_NAME));
        }
        catch (IOException e) {
            log.error("打印默认头像失败：{}", e.getMessage(), e);
        }
    }

    @GetMapping("/getUseSpace")
    @GlobalInterceptor
    public ResponseVO getUseSpace(HttpSession session) {
        final UserSpaceDto userSpaceDto = redisComponent.getUserSpaceUse(getUserInfoFromSession(session).getUserId());
        return getSuccessResponseVO(userSpaceDto);
    }

    @PostMapping("/logout")
    public ResponseVO logout(HttpSession session) {
        session.invalidate();
        return getSuccessResponseVO(null);
    }

    @PostMapping("/updateUserAvatar")
    @GlobalInterceptor
    public ResponseVO updateUserAvatar(HttpSession session, MultipartFile avatar) {
        // 创建头像文件夹
        final File targetFileFolder = new File(appConfig.getProjectFolder() + FileFolderConstants.FILE_FOLDER_FILE + FileFolderConstants.FILE_FOLDER_AVATAR_NAME);
        if (!targetFileFolder.exists()) {
            targetFileFolder.mkdirs();
        }

        // 保存头像
        final SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        final File targetFile = new File(targetFileFolder.getPath() + "/" + webUserDto.getUserId() + ImageConstants.IMAGE_SUFFIX_JPG);
        try {
            avatar.transferTo(targetFile);
        }
        catch (IOException e) {
            log.error("上传头像失败：{}", e.getMessage(), e);
        }

        // 更新用户信息
        userInfoService.updateUserInfoByUserId(UserInfo
                .builder()
                .qqAvatar("")
                .build(), webUserDto.getUserId());

        // 更新session
        session.setAttribute(SessionConstants.SESSION_KEY, webUserDto);

        // 返回成功
        return getSuccessResponseVO(null);
    }

    @PostMapping("/updatePassword")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO updatePassword(HttpSession session,
                                     @VerifyParam(required = true,
                                                  regex = VerifyRegexEnum.PASSWORD,
                                                  minLength = 8,
                                                  maxLength = 18)
                                             String password) {
        userInfoService.updateUserInfoByUserId(UserInfo
                .builder()
                .password(EncodeUtils.encodeByMD5(password))
                .build(), getUserInfoFromSession(session).getUserId());
        return getSuccessResponseVO(null);
    }

    @PostMapping("/qqlogin")
    @GlobalInterceptor(checkParams = true,
                       checkLogin = false)
    public ResponseVO qqlogin(HttpSession session, String callbackUrl) throws UnsupportedEncodingException {
        // 生成state
        final String state = RandomUtils.getRandomNumber(VerificationCodeConstants.LENGTH_CODE_30);
        if (!StringUtils.isEmpty(callbackUrl)) {
            session.setAttribute(state, callbackUrl);
        }

        // 获取url
        final String url = String.format(appConfig.getQqUrlAuthorization(), appConfig.getQqAppId(), URLEncoder.encode(appConfig.getQqUrlRedirect(), StandardCharsets.UTF_8.name()),
                state);

        // 返回结果
        return getSuccessResponseVO(url);
    }

    @PostMapping("/qqlogin/callback")
    @GlobalInterceptor(checkParams = true,
                       checkLogin = false)
    public ResponseVO qqloginCallback(HttpSession session,
                                      @VerifyParam(required = true)
                                              String code,
                                      @VerifyParam(required = true)
                                              String state) {
        // 获取用户信息
        SessionWebUserDto sessionWebUserDto = userInfoService.qqLogin(code);
        // 存入session
        session.setAttribute(SessionConstants.SESSION_KEY, sessionWebUserDto);

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("callbackUrl", session.getAttribute(state));
        result.put("userInfo", sessionWebUserDto);
        return getSuccessResponseVO(result);
    }
}
