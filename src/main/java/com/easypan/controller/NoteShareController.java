package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.po.NoteInfo;
import com.easypan.entity.po.NoteShare;
import com.easypan.entity.query.NoteShareQuery;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.service.NoteInfoService;
import com.easypan.service.NoteShareService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 笔记分享控制器
 */
@RestController
@RequestMapping("noteShare")
public class NoteShareController extends ABaseController {

    @Resource
    private NoteShareService noteShareService;

    @Resource
    private NoteInfoService noteInfoService;

    /**
     * 创建笔记分享
     */
    @PostMapping("shareNote")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<NoteShare> shareNote(HttpSession session,
                                           @VerifyParam(required = true) String noteId,
                                           String shareTitle,
                                           Integer validType,
                                           String code) {
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        NoteShare noteShare = noteShareService.shareNote(userDto.getUserId(), noteId, shareTitle, validType, code);
        return getSuccessResponseVO(noteShare);
    }

    /**
     * 取消分享
     */
    @PostMapping("cancelShare")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<Object> cancelShare(HttpSession session,
                                          @VerifyParam(required = true) String shareId) {
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        noteShareService.cancelShare(userDto.getUserId(), shareId);
        return getSuccessResponseVO(null);
    }

    /**
     * 获取用户分享列表
     */
    @PostMapping("loadShareList")
    @GlobalInterceptor
    public ResponseVO<PaginationResultVO<NoteShare>> loadShareList(HttpSession session, NoteShareQuery query) {
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        PaginationResultVO<NoteShare> result = noteShareService.getUserShareList(userDto.getUserId(), query);
        return getSuccessResponseVO(result);
    }

    /**
     * 获取分享详情（公开访问，不需要登录）
     */
    @GetMapping("getShareDetail/{shareId}")
    @GlobalInterceptor(checkLogin = false)
    public ResponseVO<Map<String, Object>> getShareDetail(@PathVariable("shareId") String shareId,
                                                          String code) {
        NoteShare noteShare = noteShareService.getShareDetail(shareId, code);
        
        // 更新浏览次数
        noteShareService.updateShareViewCount(shareId);
        
        // 获取笔记内容
        NoteInfo noteInfo = noteInfoService.getNoteInfoByNoteIdAndUserId(noteShare.getNoteId(), noteShare.getUserId());
        
        Map<String, Object> result = new HashMap<>();
        result.put("shareInfo", noteShare);
        result.put("noteInfo", noteInfo);
        
        return getSuccessResponseVO(result);
    }

    /**
     * 检查分享链接是否需要提取码
     */
    @PostMapping("checkShareCode")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO<Map<String, Object>> checkShareCode(@VerifyParam(required = true) String shareId) {
        NoteShare noteShare = noteShareService.getNoteShareByShareId(shareId);
        
        Map<String, Object> result = new HashMap<>();
        if (noteShare == null || noteShare.getStatus() == 0) {
            result.put("valid", false);
            result.put("message", "分享链接不存在或已失效");
        } else if (noteShare.getExpireTime() != null && noteShare.getExpireTime().before(new java.util.Date())) {
            result.put("valid", false);
            result.put("message", "分享链接已过期");
        } else {
            result.put("valid", true);
            result.put("needCode", noteShare.getCode() != null && !noteShare.getCode().isEmpty());
            result.put("shareTitle", noteShare.getShareTitle());
        }
        
        return getSuccessResponseVO(result);
    }

    /**
     * 验证分享提取码
     */
    @PostMapping("verifyShareCode")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO<Object> verifyShareCode(@VerifyParam(required = true) String shareId,
                                              @VerifyParam(required = true) String code) {
        noteShareService.getShareDetail(shareId, code);
        return getSuccessResponseVO(null);
    }

    /**
     * 重新生成分享链接
     */
    @PostMapping("regenerateShare")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<NoteShare> regenerateShare(HttpSession session,
                                                 @VerifyParam(required = true) String noteId,
                                                 String shareTitle,
                                                 Integer validType,
                                                 String code) {
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        
        // 先取消原有分享
        NoteShareQuery query = new NoteShareQuery();
        query.setNoteId(noteId);
        query.setUserId(userDto.getUserId());
        query.setStatus(1);
        noteShareService.deleteByParam(query);
        
        // 创建新分享
        NoteShare noteShare = noteShareService.shareNote(userDto.getUserId(), noteId, shareTitle, validType, code);
        return getSuccessResponseVO(noteShare);
    }
}