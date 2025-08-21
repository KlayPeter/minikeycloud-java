package com.easypan.service.impl;

import com.easypan.entity.constants.Constants;
import com.easypan.entity.enums.PageSize;
import com.easypan.entity.enums.ResponseCodeEnum;
import com.easypan.entity.po.NoteInfo;
import com.easypan.entity.query.NoteInfoQuery;
import com.easypan.entity.query.SimplePage;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.exception.BusinessException;
import com.easypan.mappers.NoteInfoMapper;
import com.easypan.service.NoteInfoService;
import com.easypan.utils.RandomUtils;
import com.easypan.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 笔记信息表 业务接口实现
 */
@Service("noteInfoService")
@Slf4j
public class NoteInfoServiceImpl implements NoteInfoService {

    @Resource
    private NoteInfoMapper<NoteInfo, NoteInfoQuery> noteInfoMapper;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<NoteInfo> findListByParam(NoteInfoQuery param) {
        return this.noteInfoMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(NoteInfoQuery param) {
        return this.noteInfoMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<NoteInfo> findListByPage(NoteInfoQuery param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<NoteInfo> list = this.findListByParam(param);
        PaginationResultVO<NoteInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    @Override
    public Integer add(NoteInfo bean) {
        return this.noteInfoMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<NoteInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.noteInfoMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(List<NoteInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.noteInfoMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(NoteInfo bean, NoteInfoQuery param) {
        StringUtils.checkParam(param);
        return this.noteInfoMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(NoteInfoQuery param) {
        StringUtils.checkParam(param);
        return this.noteInfoMapper.deleteByParam(param);
    }

    /**
     * 根据NoteIdAndUserId获取对象
     */
    @Override
    public NoteInfo getNoteInfoByNoteIdAndUserId(String noteId, String userId) {
        return this.noteInfoMapper.selectByNoteIdAndUserId(noteId, userId);
    }

    /**
     * 根据NoteIdAndUserId修改
     */
    @Override
    public Integer updateNoteInfoByNoteIdAndUserId(NoteInfo bean, String noteId, String userId) {
        return this.noteInfoMapper.updateByNoteIdAndUserId(bean, noteId, userId);
    }

    /**
     * 根据NoteIdAndUserId删除
     */
    @Override
    public Integer deleteNoteInfoByNoteIdAndUserId(String noteId, String userId) {
        return this.noteInfoMapper.deleteByNoteIdAndUserId(noteId, userId);
    }

    /**
     * 创建新笔记
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public NoteInfo createNote(String userId, String title, String content, Integer contentType) {
        if (StringUtils.isEmpty(title)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        
        Date curDate = new Date();
        String noteId = RandomUtils.getRandomString(Constants.LENGTH_10);
        
        NoteInfo noteInfo = new NoteInfo();
        noteInfo.setNoteId(noteId);
        noteInfo.setUserId(userId);
        noteInfo.setTitle(title);
        noteInfo.setContent(content);
        noteInfo.setContentType(contentType == null ? 1 : contentType); // 默认markdown
        noteInfo.setCreateTime(curDate);
        noteInfo.setLastUpdateTime(curDate);
        noteInfo.setStatus(1); // 正常状态
        noteInfo.setIsPublic(0); // 默认私有
        noteInfo.setViewCount(0);
        
        this.noteInfoMapper.insert(noteInfo);
        return noteInfo;
    }

    /**
     * 更新笔记内容
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNoteContent(String userId, String noteId, String title, String content, Integer contentType) {
        NoteInfo noteInfo = this.getNoteInfoByNoteIdAndUserId(noteId, userId);
        if (noteInfo == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        
        NoteInfo updateInfo = new NoteInfo();
        updateInfo.setTitle(title);
        updateInfo.setContent(content);
        updateInfo.setContentType(contentType);
        updateInfo.setLastUpdateTime(new Date());
        
        this.updateNoteInfoByNoteIdAndUserId(updateInfo, noteId, userId);
    }

    /**
     * 删除笔记
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNote(String userId, String noteId) {
        NoteInfo noteInfo = this.getNoteInfoByNoteIdAndUserId(noteId, userId);
        if (noteInfo == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        
        // 软删除
        NoteInfo updateInfo = new NoteInfo();
        updateInfo.setStatus(0);
        updateInfo.setLastUpdateTime(new Date());
        
        this.updateNoteInfoByNoteIdAndUserId(updateInfo, noteId, userId);
    }

    /**
     * 获取笔记详情（包含查看次数更新）
     */
    @Override
    public NoteInfo getNoteDetail(String noteId, String userId, boolean updateViewCount) {
        NoteInfo noteInfo = this.getNoteInfoByNoteIdAndUserId(noteId, userId);
        if (noteInfo == null || noteInfo.getStatus() == 0) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        
        if (updateViewCount) {
            this.noteInfoMapper.updateViewCount(noteId, userId);
        }
        
        return noteInfo;
    }

    /**
     * 获取用户笔记列表
     */
    @Override
    public PaginationResultVO<NoteInfo> getUserNoteList(String userId, NoteInfoQuery query) {
        query.setUserId(userId);
        query.setStatus(1); // 只查询正常状态的笔记
        query.setOrderBy("last_update_time desc");
        return this.findListByPage(query);
    }

    /**
     * 搜索笔记
     */
    @Override
    public PaginationResultVO<NoteInfo> searchNotes(String userId, String keyword, NoteInfoQuery query) {
        query.setUserId(userId);
        query.setStatus(1); // 只查询正常状态的笔记
        query.setTitleFuzzy(keyword);
        query.setOrderBy("last_update_time desc");
        return this.findListByPage(query);
    }
}