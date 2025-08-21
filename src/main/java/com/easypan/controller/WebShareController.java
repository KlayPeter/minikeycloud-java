package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.dto.DownloadFileDto;
import com.easypan.entity.dto.SessionShareDto;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.enums.FileDelFlagEnum;
import com.easypan.entity.enums.ResponseCodeEnum;
import com.easypan.entity.enums.UserStatusEnum;
import com.easypan.entity.po.FileInfo;
import com.easypan.entity.po.FileShare;
import com.easypan.entity.po.UserInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.vo.FileInfoVo;
import com.easypan.entity.vo.FolderInfoVO;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.entity.vo.ShareInfoVO;
import com.easypan.exception.BusinessException;
import com.easypan.service.FileInfoService;
import com.easypan.service.FileShareService;
import com.easypan.service.UserInfoService;
import com.easypan.utils.BeanCopyUtils;
import com.easypan.utils.DateUtil;
import com.easypan.utils.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-08-29 10:39:53
 */
@RestController("webShareController")
@RequestMapping("/showShare")
public class WebShareController extends CommonFileController
{
    @Resource
    private FileShareService fileShareService;

    @Resource
    private FileInfoService fileInfoService;

    @Resource
    private UserInfoService userInfoService;

    @PostMapping("/getShareLoginInfo")
    @GlobalInterceptor(checkParams = true,
                       checkLogin = false)
    public ResponseVO<ShareInfoVO> getShareLoginInfo(HttpSession session,
                                                     @VerifyParam(required = true)
                                                             String shareId) {
        // 1、从 session 中获取 shareId 对应的 SessionShareDto
        final SessionShareDto sessionShareDto = getShareInfoFromSession(session, shareId);
        if (Objects.isNull(sessionShareDto)) {
            return getSuccessResponseVO(null);
        }

        // 2、获取分享信息
        final ShareInfoVO shareInfoVO = getShareInfoCommon(shareId);

        // 3、判断当前用户是否为分享者
        final SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
        if (Objects.nonNull(sessionWebUserDto) && sessionWebUserDto
                .getUserId()
                .equals(shareInfoVO.getUserId())) {
            shareInfoVO.setCurrentUser(true);
        }
        else {
            shareInfoVO.setCurrentUser(false);
        }
        return getSuccessResponseVO(shareInfoVO);
    }

    @PostMapping("/getShareInfo")
    @GlobalInterceptor(checkParams = true,
                       checkLogin = false)
    public ResponseVO<ShareInfoVO> getShareInfo(
            @VerifyParam(required = true)
                    String shareId) {
        return getSuccessResponseVO(getShareInfoCommon(shareId));
    }

    @PostMapping("/checkShareCode")
    @GlobalInterceptor(checkParams = true,
                       checkLogin = false)
    public ResponseVO<Object> checkShareCode(HttpSession session,
                                             @VerifyParam(required = true)
                                                     String shareId,
                                             @VerifyParam(required = true)
                                                     String code) {
        SessionShareDto shareSessionDto = fileShareService.checkShareCode(shareId, code);
        setShareInfo2Session(session, shareId, shareSessionDto);
        return getSuccessResponseVO(null);
    }

    @PostMapping("/loadFileList")
    @GlobalInterceptor(checkParams = true,
                       checkLogin = false)
    public ResponseVO<PaginationResultVO<FileInfoVo>> loadFileList(HttpSession session,
                                                                   @VerifyParam(required = true)
                                                                           String shareId, String filePid) {
        // 1、检查分享信息
        SessionShareDto shareSessionDto = checkShare(session, shareId);

        // 2、拼接查询条件
        final String shareUserId = shareSessionDto.getShareUserId();
        final String fileId = shareSessionDto.getFileId();
        FileInfoQuery query = FileInfoQuery
                .builder()
                .userId(shareUserId)
                .delFlag(FileDelFlagEnum.USING.getFlag())
                .orderBy("last_update_time desc")
                .build();
        // 目录
        if (StringUtils.isNotEmpty(filePid) && !Constants.ZERO_STR.equals(filePid)) {
            // 检查根文件Pid
            fileInfoService.checkRootFilePid(fileId, shareUserId, filePid);
            query.setFilePid(filePid);
        }
        // 文件
        else {
            query.setFileId(fileId);
        }

        // 3、查询文件列表
        PaginationResultVO<FileInfo> resultVO = fileInfoService.findListByPage(query);

        // 4、转换为VO
        return getSuccessResponseVO(convert2PaginationVO(resultVO, FileInfoVo.class));
    }

    @PostMapping("/getFolderInfo")
    @GlobalInterceptor(checkParams = true,
                       checkLogin = false)
    public ResponseVO<List<FolderInfoVO>> getFolderInfo(HttpSession session,
                                                        @VerifyParam(required = true)
                                                                String shareId,
                                                        @VerifyParam(required = true)
                                                                String path) {
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        final List<FolderInfoVO> folderInfoVOList = super.getFolderInfo(path, shareSessionDto.getShareUserId());
        return getSuccessResponseVO(folderInfoVOList);
    }

    @GetMapping("/getFile/{shareId}/{fileId}")
    @GlobalInterceptor(checkParams = true,
                       checkLogin = false)
    public ResponseVO<Object> getFile(HttpServletResponse response, HttpSession session,
                                      @VerifyParam(required = true)
                                      @PathVariable("shareId")
                                              String shareId,
                                      @VerifyParam(required = true)
                                      @PathVariable("fileId")
                                              String fileId) {
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        super.getFileInfo(response, fileId, shareSessionDto.getShareUserId());
        return getSuccessResponseVO(null);
    }

    @GetMapping("/ts/getVideoInfo/{shareId}/{fileId}")
    @GlobalInterceptor(checkParams = true,
                       checkLogin = false)
    public ResponseVO<Object> getVideoInfo(HttpServletResponse response, HttpSession session,
                                           @VerifyParam(required = true)
                                           @PathVariable("shareId")
                                                   String shareId,
                                           @VerifyParam(required = true)
                                           @PathVariable("fileId")
                                                   String fileId) {
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        super.getFileInfo(response, fileId, shareSessionDto.getShareUserId());
        return getSuccessResponseVO(null);
    }

    @PostMapping("/createDownloadUrl/{shareId}/{fileId}")
    @GlobalInterceptor(checkParams = true,
                       checkLogin = false)
    public ResponseVO<DownloadFileDto> createDownloadUrl(HttpSession session,
                                                         @PathVariable("shareId")
                                                         @VerifyParam(required = true)
                                                                 String shareId,
                                                         @PathVariable("fileId")
                                                         @VerifyParam(required = true)
                                                                 String fileId) {
        final SessionShareDto shareSessionDto = checkShare(session, shareId);
        final DownloadFileDto downloadFileDto = fileInfoService.createDownloadUrl(shareSessionDto.getShareUserId(), fileId);
        return getSuccessResponseVO(downloadFileDto);
    }

    @GetMapping("/download/{code}")
    @GlobalInterceptor(checkParams = true,
                       checkLogin = false)
    public void download(HttpServletRequest request, HttpServletResponse response,
                         @VerifyParam(required = true)
                         @PathVariable("code")
                                 String code) throws UnsupportedEncodingException {
        super.download(request, response, code);
    }

    @PostMapping("/saveShare")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<Object> saveShare(HttpSession session,
                                        @VerifyParam(required = true)
                                                String shareId,
                                        @VerifyParam(required = true)
                                                String shareFileIds,
                                        @VerifyParam(required = true)
                                                String myFolderId) {
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        final String shareUserId = shareSessionDto.getShareUserId();
        final String userId = webUserDto.getUserId();
        if (shareUserId.equals(userId)) {
            throw new BusinessException("自己分享的文件无法保存到自己的网盘");
        }
        final String fileId = shareSessionDto.getFileId();
        fileInfoService.saveShare(fileId, shareFileIds, myFolderId, shareUserId, userId);
        return getSuccessResponseVO(null);
    }

    private SessionShareDto checkShare(HttpSession session, String shareId) {
        final SessionShareDto sessionShareDto = getShareInfoFromSession(session, shareId);
        // 分享验证
        if (Objects.isNull(sessionShareDto)) {
            throw new BusinessException(ResponseCodeEnum.CODE_903);
        }
        // 分享过期
        if (DateUtil.isExpire(sessionShareDto.getExpireTime())) {
            throw new BusinessException(ResponseCodeEnum.CODE_902);
        }
        return sessionShareDto;
    }

    private ShareInfoVO getShareInfoCommon(String shareId) {
        // 1、根据 shareId 获取分享信息
        FileShare fileShare = fileShareService.getFileShareByShareId(shareId);
        if (Objects.isNull(fileShare) || DateUtil.isExpire(fileShare.getExpireTime())) {
            throw new BusinessException(ResponseCodeEnum.CODE_902);
        }

        // 2、根据 fileId 获取文件信息
        final FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(fileShare.getFileId(), fileShare.getUserId());
        if (Objects.isNull(fileInfo) || FileDelFlagEnum.USING
                .getFlag()
                .compareTo(fileInfo.getDelFlag()) != 0) {
            throw new BusinessException(ResponseCodeEnum.CODE_902);
        }

        // 3、根据 userId 获取用户信息
        final UserInfo userInfo = userInfoService.getUserInfoByUserId(fileShare.getUserId());
        if (Objects.isNull(userInfo) || UserStatusEnum.ENABLE
                .getStatus()
                .compareTo(userInfo.getStatus()) != 0) {
            throw new BusinessException(ResponseCodeEnum.CODE_902);
        }

        // 4、转换为 ShareInfoVO
        final ShareInfoVO shareInfoVO = BeanCopyUtils.copy(fileShare, ShareInfoVO.class);
        Objects.requireNonNull(shareInfoVO);
        shareInfoVO.setFileName(fileInfo.getFileName());
        shareInfoVO.setNickName(userInfo.getNickName());
        shareInfoVO.setAvatar(userInfo.getQqAvatar());
        return shareInfoVO;
    }
}
