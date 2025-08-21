package com.easypan.service;

import com.easypan.entity.po.NoteInfo;
import com.easypan.entity.query.NoteInfoQuery;
import com.easypan.entity.vo.PaginationResultVO;

import java.util.List;

/**
 * 笔记信息表 业务接口
 */
public interface NoteInfoService {

    /**
     * 根据条件查询列表
     */
    List<NoteInfo> findListByParam(NoteInfoQuery param);

    /**
     * 根据条件查询列表
     */
    Integer findCountByParam(NoteInfoQuery param);

    /**
     * 分页查询
     */
    PaginationResultVO<NoteInfo> findListByPage(NoteInfoQuery param);

    /**
     * 新增
     */
    Integer add(NoteInfo bean);

    /**
     * 批量新增
     */
    Integer addBatch(List<NoteInfo> listBean);

    /**
     * 批量新增/修改
     */
    Integer addOrUpdateBatch(List<NoteInfo> listBean);

    /**
     * 多条件更新
     */
    Integer updateByParam(NoteInfo bean, NoteInfoQuery param);

    /**
     * 多条件删除
     */
    Integer deleteByParam(NoteInfoQuery param);

    /**
     * 根据NoteIdAndUserId查询对象
     */
    NoteInfo getNoteInfoByNoteIdAndUserId(String noteId, String userId);

    /**
     * 根据NoteIdAndUserId修改
     */
    Integer updateNoteInfoByNoteIdAndUserId(NoteInfo bean, String noteId, String userId);

    /**
     * 根据NoteIdAndUserId删除
     */
    Integer deleteNoteInfoByNoteIdAndUserId(String noteId, String userId);

    /**
     * 创建新笔记
     */
    NoteInfo createNote(String userId, String title, String content, Integer contentType);

    /**
     * 更新笔记内容
     */
    void updateNoteContent(String userId, String noteId, String title, String content, Integer contentType);

    /**
     * 删除笔记
     */
    void deleteNote(String userId, String noteId);

    /**
     * 获取笔记详情（包含查看次数更新）
     */
    NoteInfo getNoteDetail(String noteId, String userId, boolean updateViewCount);

    /**
     * 获取用户笔记列表
     */
    PaginationResultVO<NoteInfo> getUserNoteList(String userId, NoteInfoQuery query);

    /**
     * 搜索笔记
     */
    PaginationResultVO<NoteInfo> searchNotes(String userId, String keyword, NoteInfoQuery query);
}