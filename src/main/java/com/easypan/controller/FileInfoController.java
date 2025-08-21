package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.entity.dto.DownloadFileDto;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.dto.UploadFileDto;
import com.easypan.entity.enums.FileCategoryEnums;
import com.easypan.entity.enums.FileDelFlagEnum;
import com.easypan.entity.po.FileInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.vo.FileInfoVo;
import com.easypan.entity.vo.FolderInfoVO;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.entity.vo.UploadResultVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-07-23 19:03:51
 */
@RestController
@RequestMapping("file")
public class FileInfoController extends CommonFileController
{

    @PostMapping("loadDataList")
    @GlobalInterceptor
    public ResponseVO<PaginationResultVO<FileInfoVo>> loadDataList(HttpSession session, FileInfoQuery query, String category) {
        // 构造查询条件
        Optional
                .ofNullable(FileCategoryEnums.getByCode(category))
                .ifPresent(fileCategoryEnums -> query.setFileCategory(fileCategoryEnums.getCategory()));
        // 可以考虑使用 ThreadLocal 吧？
        query.setUserId(getUserInfoFromSession(session).getUserId());
        query.setOrderBy("last_update_time desc");
        query.setDelFlag(FileDelFlagEnum.USING.getFlag());

        // 分页查询
        final PaginationResultVO<FileInfo> fileInfoPaginationResultVO = fileInfoService.findListByPage(query);

        // 转换为VO
        final PaginationResultVO<FileInfoVo> fileInfoVoPaginationResultVO = convert2PaginationVO(fileInfoPaginationResultVO, FileInfoVo.class);

        // 返回结果
        return getSuccessResponseVO(fileInfoVoPaginationResultVO);
    }

    @PostMapping("uploadFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<UploadResultVo> uploadFile(HttpSession session, String fileId, MultipartFile file,
                                                 @VerifyParam(required = true)
                                                         UploadFileDto uploadFileDto) {
        // 用户Session信息
        final SessionWebUserDto userDto = getUserInfoFromSession(session);

        // 上传文件
        final UploadResultVo uploadResultVo = fileInfoService.uploadFile(userDto, uploadFileDto);

        // 返回结果
        return getSuccessResponseVO(uploadResultVo);
    }

    @GetMapping("getImage/{imageFolder}/{imageName}")
    @GlobalInterceptor(checkParams = true)
    public void getImage(HttpServletResponse response,
                         @PathVariable(value = "imageFolder")
                                 String imageFolder,
                         @PathVariable(value = "imageName")
                                 String imageName) {
        super.getImage(response, imageFolder, imageName);
    }

    @GetMapping("ts/getVideoInfo/{fileId}")
    @GlobalInterceptor(checkParams = true)
    public void getVideoInfo(HttpServletResponse response, HttpSession session,
                             @PathVariable(value = "fileId")
                                     String fileId) {
        super.getFileInfo(response, fileId, getUserInfoFromSession(session).getUserId());
    }

    @GetMapping("getFile/{fileId}")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public void getFile(HttpServletResponse response, HttpSession session,
                        @PathVariable(value = "fileId")
                                String fileId) {
        SessionWebUserDto userInfo = getUserInfoFromSession(session);
        String userId = userInfo != null ? userInfo.getUserId() : null;
        super.getFileInfo(response, fileId, userId);
    }

    @PostMapping("newFoloder")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<FileInfoVo> newFolder(HttpSession session,
                                            @VerifyParam(required = true)
                                                    String filePid,
                                            @VerifyParam(required = true)
                                                    String fileName) {
        final FileInfoVo fileInfoVo = fileInfoService.newFolder(getUserInfoFromSession(session).getUserId(), filePid, fileName);
        return getSuccessResponseVO(fileInfoVo);
    }

    @PostMapping("getFolderInfo")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<List<FolderInfoVO>> getFolderInfo(HttpSession session,
                                                        @VerifyParam(required = true)
                                                                String path) {
        final List<FolderInfoVO> fileInfoVoList = super.getFolderInfo(path, getUserInfoFromSession(session).getUserId());
        return this.getSuccessResponseVO(fileInfoVoList);
    }

    @PostMapping("rename")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<FileInfoVo> rename(HttpSession session,
                                         @VerifyParam(required = true)
                                                 String fileId,
                                         @VerifyParam(required = true)
                                                 String fileName) {
        final FileInfoVo fileInfoVo = fileInfoService.rename(getUserInfoFromSession(session).getUserId(), fileId, fileName);
        return getSuccessResponseVO(fileInfoVo);
    }

    @PostMapping("loadAllFolder")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<List<FileInfoVo>> loadAllFolder(HttpSession session,
                                                      @VerifyParam(required = true)
                                                              String filePid, String currentFileIds) {
        final List<FileInfoVo> fileInfoVoList = fileInfoService.loadAllFolder(getUserInfoFromSession(session).getUserId(), filePid, currentFileIds);
        return getSuccessResponseVO(fileInfoVoList);
    }

    @PostMapping("changeFileFolder")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<Object> changeFileFolder(HttpSession session,
                                             @VerifyParam(required = true)
                                                     String fileIds,
                                             @VerifyParam(required = true)
                                                     String filePid) {
        fileInfoService.changeFileFolder(getUserInfoFromSession(session).getUserId(), fileIds, filePid);
        return getSuccessResponseVO(null);
    }

    @PostMapping("createDownloadUrl/{fileId}")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<DownloadFileDto> createDownloadUrl(HttpSession session,
                                                         @VerifyParam(required = true)
                                                         @PathVariable(value = "fileId")
                                                                 String fileId) {
        final DownloadFileDto downloadFileDto = fileInfoService.createDownloadUrl(getUserInfoFromSession(session).getUserId(), fileId);
        return getSuccessResponseVO(downloadFileDto);
    }

    @GetMapping("download/{code}")
    @GlobalInterceptor(checkParams = true,
                       checkLogin = false)
    public void download(HttpServletRequest request, HttpServletResponse response,
                         @VerifyParam(required = true)
                         @PathVariable(value = "code")
                                 String code) throws UnsupportedEncodingException {
        super.download(request, response, code);
    }

    @PostMapping("delFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<Object> delFile(HttpSession session,
                                    @VerifyParam(required = true)
                                    @RequestParam(value = "fileIds")
                                            String fileIds) {
        final String userId = getUserInfoFromSession(session).getUserId();
        fileInfoService.delFile(userId, fileIds);
        return getSuccessResponseVO(null);
    }

}
