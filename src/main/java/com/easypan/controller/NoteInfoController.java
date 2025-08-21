package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.po.NoteInfo;
import com.easypan.entity.po.NoteShare;
import com.easypan.entity.query.NoteInfoQuery;
import com.easypan.entity.query.NoteShareQuery;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.exception.BusinessException;
import com.easypan.entity.enums.ResponseCodeEnum;
import com.easypan.service.NoteInfoService;
import com.easypan.service.NoteShareService;
import com.easypan.utils.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 笔记信息控制器
 */
@RestController
@RequestMapping("note")
public class NoteInfoController extends ABaseController {

    @Resource
    private NoteInfoService noteInfoService;

    @Resource
    private NoteShareService noteShareService;

    /**
     * 获取笔记列表
     */
    @PostMapping("loadNoteList")
    @GlobalInterceptor
    public ResponseVO<PaginationResultVO<NoteInfo>> loadNoteList(HttpSession session, NoteInfoQuery query) {
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        PaginationResultVO<NoteInfo> result = noteInfoService.getUserNoteList(userDto.getUserId(), query);
        return getSuccessResponseVO(result);
    }

    /**
     * 创建新笔记
     */
    @PostMapping("createNote")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<NoteInfo> createNote(HttpSession session,
                                           @VerifyParam(required = true) String title,
                                           String content,
                                           Integer contentType) {
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        NoteInfo noteInfo = noteInfoService.createNote(userDto.getUserId(), title, content, contentType);
        return getSuccessResponseVO(noteInfo);
    }

    /**
     * 获取笔记详情
     */
    @PostMapping("getNoteDetail")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<NoteInfo> getNoteDetail(HttpSession session,
                                              @VerifyParam(required = true) String noteId) {
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        NoteInfo noteInfo = noteInfoService.getNoteDetail(noteId, userDto.getUserId(), true);
        return getSuccessResponseVO(noteInfo);
    }

    /**
     * 保存笔记（创建或更新）
     */
    @PostMapping("saveNote")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<NoteInfo> saveNote(HttpSession session,
                                         String noteId,
                                         @VerifyParam(required = true) String title,
                                         String content,
                                         Integer contentType,
                                         Integer isPublic) {
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        NoteInfo noteInfo;
        if (noteId == null || noteId.trim().isEmpty()) {
            // 创建新笔记
            noteInfo = noteInfoService.createNote(userDto.getUserId(), title, content, contentType);
            if (isPublic != null) {
                NoteInfo updateInfo = new NoteInfo();
                updateInfo.setIsPublic(isPublic);
                noteInfoService.updateNoteInfoByNoteIdAndUserId(updateInfo, noteInfo.getNoteId(), userDto.getUserId());
                noteInfo.setIsPublic(isPublic);
            }
        } else {
            // 更新现有笔记
            noteInfoService.updateNoteContent(userDto.getUserId(), noteId, title, content, contentType);
            if (isPublic != null) {
                NoteInfo updateInfo = new NoteInfo();
                updateInfo.setIsPublic(isPublic);
                noteInfoService.updateNoteInfoByNoteIdAndUserId(updateInfo, noteId, userDto.getUserId());
            }
            noteInfo = noteInfoService.getNoteDetail(noteId, userDto.getUserId(), false);
        }
        return getSuccessResponseVO(noteInfo);
    }

    /**
     * 更新笔记
     */
    @PostMapping("updateNote")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<Object> updateNote(HttpSession session,
                                         @VerifyParam(required = true) String noteId,
                                         @VerifyParam(required = true) String title,
                                         String content,
                                         Integer contentType) {
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        noteInfoService.updateNoteContent(userDto.getUserId(), noteId, title, content, contentType);
        return getSuccessResponseVO(null);
    }

    /**
     * 删除笔记
     */
    @PostMapping("deleteNote")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<Object> deleteNote(HttpSession session,
                                         @VerifyParam(required = true) String noteId) {
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        noteInfoService.deleteNote(userDto.getUserId(), noteId);
        return getSuccessResponseVO(null);
    }

    /**
     * 搜索笔记
     */
    @PostMapping("searchNotes")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<PaginationResultVO<NoteInfo>> searchNotes(HttpSession session,
                                                                @VerifyParam(required = true) String keyword,
                                                                NoteInfoQuery query) {
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        PaginationResultVO<NoteInfo> result = noteInfoService.searchNotes(userDto.getUserId(), keyword, query);
        return getSuccessResponseVO(result);
    }

    /**
     * 批量删除笔记
     */
    @PostMapping("batchDeleteNotes")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<Object> batchDeleteNotes(HttpSession session,
                                               @VerifyParam(required = true) String noteIds) {
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        String[] noteIdArray = noteIds.split(",");
        for (String noteId : noteIdArray) {
            noteInfoService.deleteNote(userDto.getUserId(), noteId.trim());
        }
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
     * 创建笔记分享
     */
    @PostMapping("createShare")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<NoteShare> createShare(HttpSession session,
                                             @VerifyParam(required = true) String noteId,
                                             String shareTitle,
                                             Integer validType,
                                             String code) {
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        NoteShare noteShare = noteShareService.shareNote(userDto.getUserId(), noteId, shareTitle, validType, code);
        return getSuccessResponseVO(noteShare);
    }

    /**
     * 重新生成分享链接
     */
    @PostMapping("regenerateShare")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<NoteShare> regenerateShare(HttpSession session,
                                                 @VerifyParam(required = true) String shareId) {
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        
        // 获取原分享信息
        NoteShare originalShare = noteShareService.getNoteShareByShareId(shareId);
        if (originalShare == null || !originalShare.getUserId().equals(userDto.getUserId())) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        
        // 先取消原有分享
        NoteShareQuery query = new NoteShareQuery();
        query.setNoteId(originalShare.getNoteId());
        query.setUserId(userDto.getUserId());
        query.setStatus(1);
        noteShareService.deleteByParam(query);
        
        // 创建新分享，保持原有的分享设置
        NoteShare noteShare = noteShareService.shareNote(
            userDto.getUserId(), 
            originalShare.getNoteId(), 
            originalShare.getShareTitle(), 
            originalShare.getValidType(), 
            originalShare.getCode()
        );
        return getSuccessResponseVO(noteShare);
    }

    /**
     * 取消分享
     */
    @PostMapping("cancelShare")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO<Object> cancelShare(HttpSession session,
                                          @VerifyParam(required = true) String shareIds) {
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        String[] shareIdArray = shareIds.split(",");
        for (String shareId : shareIdArray) {
            noteShareService.cancelShare(userDto.getUserId(), shareId.trim());
        }
        return getSuccessResponseVO(null);
    }

    /**
     * 检查分享码
     */
    @PostMapping("checkShareCode")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO<Map<String, Object>> checkShareCode(@VerifyParam(required = true) String shareId) {
        NoteShare noteShare = noteShareService.getNoteShareByShareId(shareId);
        
        Map<String, Object> result = new HashMap<>();
        if (noteShare == null || noteShare.getStatus() == 0) {
            result.put("needCode", false);
            result.put("valid", false);
        } else {
            // 检查是否过期
            boolean isExpired = false;
            if (noteShare.getValidType() != 3 && noteShare.getExpireTime() != null) {
                isExpired = new Date().after(noteShare.getExpireTime());
            }
            
            result.put("needCode", !StringUtils.isEmpty(noteShare.getCode()));
            result.put("valid", !isExpired);
        }
        return getSuccessResponseVO(result);
    }

    /**
     * 获取分享内容
     */
    @PostMapping("getShareContent")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO<Map<String, Object>> getShareContent(@VerifyParam(required = true) String shareId) {
        NoteShare noteShare = noteShareService.getNoteShareByShareId(shareId);
        
        if (noteShare == null || noteShare.getStatus() == 0) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        
        // 检查是否过期
        if (noteShare.getValidType() != 3 && noteShare.getExpireTime() != null) {
            boolean isExpired = new Date().after(noteShare.getExpireTime());
            if (isExpired) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
        }
        
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
     * 获取分享详情（POST方法）
     */
    @PostMapping("getShareDetail")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO<Map<String, Object>> getShareDetail(@VerifyParam(required = true) String shareId,
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
}