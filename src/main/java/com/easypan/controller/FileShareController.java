package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.entity.po.FileShare;
import com.easypan.entity.query.FileShareQuery;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.service.FileShareService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

/**
 * 文件分享信息 Controller
 */
@RestController("fileShareController")
@RequestMapping("share")
public class FileShareController extends ABaseController
{

    @Resource
    private FileShareService fileShareService;

    /**
     * 根据条件分页查询
     */
    @RequestMapping("/loadShareList")
    @GlobalInterceptor
    public ResponseVO<PaginationResultVO<FileShare>> loadShareList(HttpSession session, FileShareQuery query) {
        query.setQueryFileName(true);
        query.setUserId(getUserInfoFromSession(session).getUserId());
        query.setOrderBy("share_time desc");
        final PaginationResultVO<FileShare> listByPage = fileShareService.findListByPage(query);
        return getSuccessResponseVO(listByPage);
    }

    @RequestMapping("/shareFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<FileShare> shareFile(HttpSession session,
                                           @VerifyParam(required = true)
                                                   String fileId,
                                           @VerifyParam(required = true)
                                                   Integer validType, String code) {
        final FileShare fileShare = new FileShare();
        fileShare.setFileId(fileId);
        fileShare.setUserId(getUserInfoFromSession(session).getUserId());
        fileShare.setValidType(validType);
        fileShare.setCode(code);
        fileShareService.saveShare(fileShare);
        return getSuccessResponseVO(fileShare);
    }

    @RequestMapping("/cancelShare")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<Object> cancelShare(HttpSession session,
                                          @VerifyParam(required = true)
                                                  String shareIds) {
        final List<String> shareIdList = Arrays.asList(shareIds.split(","));
        final String userId = getUserInfoFromSession(session).getUserId();
        fileShareService.deleteFileShareBatch(shareIdList, userId);
        return getSuccessResponseVO(null);
    }

}
