package com.easypan.service;

import com.easypan.entity.po.NoteShare;
import com.easypan.entity.query.NoteShareQuery;
import com.easypan.entity.vo.PaginationResultVO;

import java.util.List;

/**
 * 笔记分享信息表 业务接口
 */
public interface NoteShareService {

    /**
     * 根据条件查询列表
     */
    List<NoteShare> findListByParam(NoteShareQuery param);

    /**
     * 根据条件查询列表
     */
    Integer findCountByParam(NoteShareQuery param);

    /**
     * 分页查询
     */
    PaginationResultVO<NoteShare> findListByPage(NoteShareQuery param);

    /**
     * 新增
     */
    Integer add(NoteShare bean);

    /**
     * 批量新增
     */
    Integer addBatch(List<NoteShare> listBean);

    /**
     * 批量新增/修改
     */
    Integer addOrUpdateBatch(List<NoteShare> listBean);

    /**
     * 多条件更新
     */
    Integer updateByParam(NoteShare bean, NoteShareQuery param);

    /**
     * 多条件删除
     */
    Integer deleteByParam(NoteShareQuery param);

    /**
     * 根据ShareId查询对象
     */
    NoteShare getNoteShareByShareId(String shareId);

    /**
     * 根据ShareId修改
     */
    Integer updateNoteShareByShareId(NoteShare bean, String shareId);

    /**
     * 根据ShareId删除
     */
    Integer deleteNoteShareByShareId(String shareId);

    /**
     * 创建笔记分享
     */
    NoteShare shareNote(String userId, String noteId, String shareTitle, Integer validType, String code);

    /**
     * 取消分享
     */
    void cancelShare(String userId, String shareId);

    /**
     * 获取分享详情
     */
    NoteShare getShareDetail(String shareId, String code);

    /**
     * 更新分享浏览次数
     */
    void updateShareViewCount(String shareId);

    /**
     * 获取用户分享列表
     */
    PaginationResultVO<NoteShare> getUserShareList(String userId, NoteShareQuery query);

    /**
     * 检查分享是否有效
     */
    boolean checkShareValid(String shareId);
}