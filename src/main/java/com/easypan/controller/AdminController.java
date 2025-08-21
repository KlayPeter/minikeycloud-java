package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.entity.dto.DownloadFileDto;
import com.easypan.entity.dto.SysSettingsDto;
import com.easypan.entity.po.FileInfo;
import com.easypan.entity.po.UserInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.query.UserInfoQuery;
import com.easypan.entity.vo.FolderInfoVO;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.entity.vo.UserInfoVO;
import com.easypan.service.UserInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

/**
 * 文件分享信息 Controller
 */
@RestController("adminController")
@RequestMapping("/admin")
public class AdminController extends CommonFileController
{
    @Resource
    private UserInfoService userInfoService;

    @PostMapping("/getSysSettings")
    @GlobalInterceptor(checkParams = true,
                       checkAdmin = true)
    public ResponseVO<SysSettingsDto> getSysSettings() {
        return getSuccessResponseVO(redisComponent.getSysSettings());
    }

    @PostMapping("/saveSysSettings")
    @GlobalInterceptor(checkParams = true,
                       checkAdmin = true)
    public ResponseVO<Object> saveSysSettings(
            @VerifyParam(required = true)
                    String registerEmailTitle,
            @VerifyParam(required = true)
                    String registerEmailContent,
            @VerifyParam(required = true)
                    Integer userInitUseSpace) {
        final SysSettingsDto sysSettingsDto = new SysSettingsDto();
        sysSettingsDto.setRegisterEmailTitle(registerEmailTitle);
        sysSettingsDto.setRegisterEmailContent(registerEmailContent);
        sysSettingsDto.setUserInitUseSpace(userInitUseSpace);
        redisComponent.saveSysSettings(sysSettingsDto);
        return getSuccessResponseVO(null);
    }

    @PostMapping("/loadUserList")
    @GlobalInterceptor(checkParams = true,
                       checkAdmin = true)
    public ResponseVO<Object> loadUserList(UserInfoQuery query) {
        query.setOrderBy("join_time desc");
        final PaginationResultVO<UserInfo> paginationResultVO = userInfoService.findListByPage(query);
        return getSuccessResponseVO(convert2PaginationVO(paginationResultVO, UserInfoVO.class));
    }

    @PostMapping("/updateUserStatus")
    @GlobalInterceptor(checkParams = true,
                       checkAdmin = true)
    public ResponseVO<Object> updateUserStatus(
            @VerifyParam(required = true)
                    String userId,
            @VerifyParam(required = true)
                    Integer status) {
        userInfoService.updateUserStatus(userId, status);
        return getSuccessResponseVO(null);
    }

    @PostMapping("/updateUserSpace")
    @GlobalInterceptor(checkParams = true,
                       checkAdmin = true)
    public ResponseVO<Object> updateUserSpace(
            @VerifyParam(required = true)
                    String userId,
            @VerifyParam(required = true)
                    Integer changeSpace) {
        userInfoService.updateUserSpace(userId, changeSpace);
        return getSuccessResponseVO(null);
    }

    @PostMapping("loadFileList")
    @GlobalInterceptor(checkParams = true,
                       checkAdmin = true)
    public ResponseVO<PaginationResultVO<FileInfo>> loadFileList(FileInfoQuery query) {
        query.setQueryNickName(true);
        query.setOrderBy("last_update_time desc");
        final PaginationResultVO<FileInfo> paginationResultVO = this.fileInfoService.findListByPage(query);
        return getSuccessResponseVO(paginationResultVO);
    }

    @PostMapping("getFolderInfo")
    @GlobalInterceptor(checkParams = true,
                       checkAdmin = true)
    public ResponseVO<List<FolderInfoVO>> getFolderInfo(
            @VerifyParam(required = true)
                    String path) {
        return getSuccessResponseVO(super.getFolderInfo(path, null));
    }

    @GetMapping("getFile/{userId}/{fileId}")
    @GlobalInterceptor(checkParams = true,
                       checkAdmin = true)
    public ResponseVO<Object> getFile(HttpServletResponse response,
                                      @VerifyParam(required = true)
                                      @PathVariable("userId")
                                              String userId,
                                      @PathVariable("fileId")
                                              String fileId) {
        super.getFileInfo(response, fileId, userId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/ts/getVideoInfo/{userId}/{fileId}")
    @GlobalInterceptor(checkParams = true,
                       checkAdmin = true)
    public ResponseVO<Object> getVideoInfo(HttpServletResponse response,
                                           @VerifyParam(required = true)
                                           @PathVariable("userId")
                                                   String userId,
                                           @VerifyParam(required = true)
                                           @PathVariable("fileId")
                                                   String fileId) {
        super.getFileInfo(response, fileId, userId);
        return getSuccessResponseVO(null);
    }

    /**
     * 创建下载链接
     *
     * @param userId 用户Id
     * @param fileId 文件Id
     * @return ResponseVO
     */
    @RequestMapping("/createDownloadUrl/{userId}/{fileId}")
    @GlobalInterceptor(checkParams = true,
                       checkAdmin = true)
    public ResponseVO<DownloadFileDto> createDownloadUrl(
            @VerifyParam(required = true)
            @PathVariable("userId")
                    String userId,
            @VerifyParam(required = true)
            @PathVariable("fileId")
                    String fileId) {
        final DownloadFileDto downloadFileDto = fileInfoService.createDownloadUrl(userId, fileId);
        return getSuccessResponseVO(downloadFileDto);
    }

    @GetMapping("/download/{code}")
    @GlobalInterceptor(checkParams = true,
                       checkLogin = false)
    public void download(HttpServletRequest request, HttpServletResponse response,
                         @VerifyParam(required = true)
                         @PathVariable(value = "code")
                                 String code) throws UnsupportedEncodingException {
        super.download(request, response, code);
    }

    @PostMapping("/delFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<Object> delFile(
            @VerifyParam(required = true)
            @RequestParam(value = "fileIdAndUserIds")
                    String fileIdAndUserIds) {
        for (String fileIdAndUserId : fileIdAndUserIds.split(",")) {
            final List<String> fileIdAndUserIdList = Arrays.asList(fileIdAndUserId.split("_"));
            fileInfoService.delFileBatch(fileIdAndUserIdList.get(0), fileIdAndUserIdList.get(1), true);
        }
        return getSuccessResponseVO(null);
    }

}
