package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.entity.enums.FileDelFlagEnum;
import com.easypan.entity.po.FileInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.vo.FileInfoVo;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.service.FileInfoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-08-24 14:31:00
 */
@RestController("recycleController")
@RequestMapping("/recycle")
public class RecycleController extends ABaseController
{
    @Resource
    private FileInfoService fileInfoService;

    @PostMapping("/loadRecycleList")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO loadRecycleList(HttpSession session, Integer pageSize, Integer pageNo) {
        final PaginationResultVO<FileInfo> resultVO = fileInfoService.findListByPage(FileInfoQuery
                .builder()
                .pageSize(pageSize)
                .pageNo(pageNo)
                .userId(getUserInfoFromSession(session).getUserId())
                .delFlag(FileDelFlagEnum.RECYCLE.getFlag())
                .orderBy("recovery_time desc")
                .build());
        return getSuccessResponseVO(convert2PaginationVO(resultVO, FileInfoVo.class));
    }

    @PostMapping("/recoverFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO recoverFile(HttpSession session,
                                  @VerifyParam(required = true)
                                          String fileIds) {
        fileInfoService.recoveryFileBatch(getUserInfoFromSession(session).getUserId(), fileIds);
        return getSuccessResponseVO(null);
    }

    @PostMapping("/delFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO delFile(HttpSession session,
                              @VerifyParam(required = true)
                                      String fileIds) {
        fileInfoService.delFileBatch(getUserInfoFromSession(session).getUserId(), fileIds, false);
        return getSuccessResponseVO(null);
    }
}
