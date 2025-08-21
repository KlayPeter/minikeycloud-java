package com.easypan.service.impl;

import com.easypan.entity.constants.Constants;
import com.easypan.entity.enums.PageSize;
import com.easypan.entity.enums.ResponseCodeEnum;
import com.easypan.entity.po.NoteInfo;
import com.easypan.entity.po.NoteShare;
import com.easypan.entity.query.NoteShareQuery;
import com.easypan.entity.query.SimplePage;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.exception.BusinessException;
import com.easypan.mappers.NoteShareMapper;
import com.easypan.service.NoteInfoService;
import com.easypan.service.NoteShareService;
import com.easypan.utils.RandomUtils;
import com.easypan.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 笔记分享信息表 业务接口实现
 */
@Service("noteShareService")
@Slf4j
public class NoteShareServiceImpl implements NoteShareService {

    @Resource
    private NoteShareMapper<NoteShare, NoteShareQuery> noteShareMapper;

    @Resource
    private NoteInfoService noteInfoService;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<NoteShare> findListByParam(NoteShareQuery param) {
        return this.noteShareMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(NoteShareQuery param) {
        return this.noteShareMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<NoteShare> findListByPage(NoteShareQuery param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<NoteShare> list = this.findListByParam(param);
        PaginationResultVO<NoteShare> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    @Override
    public Integer add(NoteShare bean) {
        return this.noteShareMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<NoteShare> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.noteShareMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(List<NoteShare> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.noteShareMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(NoteShare bean, NoteShareQuery param) {
        StringUtils.checkParam(param);
        return this.noteShareMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(NoteShareQuery param) {
        StringUtils.checkParam(param);
        return this.noteShareMapper.deleteByParam(param);
    }

    /**
     * 根据ShareId获取对象
     */
    @Override
    public NoteShare getNoteShareByShareId(String shareId) {
        return this.noteShareMapper.selectByShareId(shareId);
    }

    /**
     * 根据ShareId修改
     */
    @Override
    public Integer updateNoteShareByShareId(NoteShare bean, String shareId) {
        return this.noteShareMapper.updateByShareId(bean, shareId);
    }

    /**
     * 根据ShareId删除
     */
    @Override
    public Integer deleteNoteShareByShareId(String shareId) {
        return this.noteShareMapper.deleteByShareId(shareId);
    }

    /**
     * 创建笔记分享
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public NoteShare shareNote(String userId, String noteId, String shareTitle, Integer validType, String code) {
        // 检查笔记是否存在
        NoteInfo noteInfo = noteInfoService.getNoteInfoByNoteIdAndUserId(noteId, userId);
        if (noteInfo == null || noteInfo.getStatus() == 0) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        Date curDate = new Date();
        String shareId = RandomUtils.getRandomString(Constants.LENGTH_20);
        
        // 计算过期时间
        Date expireTime = null;
        if (validType != null && validType != 3) { // 3表示永久有效
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(curDate);
            switch (validType) {
                case 0: // 1天
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    break;
                case 1: // 7天
                    calendar.add(Calendar.DAY_OF_MONTH, 7);
                    break;
                case 2: // 30天
                    calendar.add(Calendar.DAY_OF_MONTH, 30);
                    break;
            }
            expireTime = calendar.getTime();
        }
        
        NoteShare noteShare = new NoteShare();
        noteShare.setShareId(shareId);
        noteShare.setNoteId(noteId);
        noteShare.setUserId(userId);
        noteShare.setShareTitle(StringUtils.isEmpty(shareTitle) ? noteInfo.getTitle() : shareTitle);
        noteShare.setValidType(validType == null ? 3 : validType);
        noteShare.setExpireTime(expireTime);
        noteShare.setShareTime(curDate);
        noteShare.setCode(code);
        noteShare.setShowCount(0);
        noteShare.setStatus(1);
        
        this.noteShareMapper.insert(noteShare);
        return noteShare;
    }

    /**
     * 取消分享
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelShare(String userId, String shareId) {
        NoteShare noteShare = this.getNoteShareByShareId(shareId);
        if (noteShare == null || !noteShare.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        
        NoteShare updateInfo = new NoteShare();
        updateInfo.setStatus(0);
        
        this.updateNoteShareByShareId(updateInfo, shareId);
    }

    /**
     * 获取分享详情
     */
    @Override
    public NoteShare getShareDetail(String shareId, String code) {
        NoteShare noteShare = this.getNoteShareByShareId(shareId);
        if (noteShare == null || noteShare.getStatus() == 0) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        
        // 检查是否过期
        if (noteShare.getExpireTime() != null && noteShare.getExpireTime().before(new Date())) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        
        // 检查提取码
        if (!StringUtils.isEmpty(noteShare.getCode()) && !noteShare.getCode().equals(code)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        
        return noteShare;
    }

    /**
     * 更新分享浏览次数
     */
    @Override
    public void updateShareViewCount(String shareId) {
        this.noteShareMapper.updateShowCount(shareId);
    }

    /**
     * 获取用户分享列表
     */
    @Override
    public PaginationResultVO<NoteShare> getUserShareList(String userId, NoteShareQuery query) {
        query.setUserId(userId);
        query.setStatus(1); // 只查询正常状态的分享
        query.setOrderBy("share_time desc");
        return this.findListByPage(query);
    }

    /**
     * 检查分享是否有效
     */
    @Override
    public boolean checkShareValid(String shareId) {
        NoteShare noteShare = this.getNoteShareByShareId(shareId);
        if (noteShare == null || noteShare.getStatus() == 0) {
            return false;
        }
        
        // 检查是否过期
        if (noteShare.getExpireTime() != null && noteShare.getExpireTime().before(new Date())) {
            return false;
        }
        
        return true;
    }
}