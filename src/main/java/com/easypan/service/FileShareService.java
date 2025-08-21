package com.easypan.service;

import com.easypan.entity.dto.SessionShareDto;
import com.easypan.entity.po.FileShare;
import com.easypan.entity.query.FileShareQuery;
import com.easypan.entity.vo.PaginationResultVO;

import java.util.List;

/**
 * 文件分享信息 业务接口
 */
public interface FileShareService
{

    /**
     * 根据条件查询列表
     */
    List<FileShare> findListByParam(FileShareQuery param);

    /**
     * 根据条件查询列表
     */
    Integer findCountByParam(FileShareQuery param);

    /**
     * 分页查询
     */
    PaginationResultVO<FileShare> findListByPage(FileShareQuery param);

    /**
     * 新增
     */
    Integer add(FileShare bean);

    /**
     * 批量新增
     */
    Integer addBatch(List<FileShare> listBean);

    /**
     * 批量新增/修改
     */
    Integer addOrUpdateBatch(List<FileShare> listBean);

    /**
     * 多条件更新
     */
    Integer updateByParam(FileShare bean, FileShareQuery param);

    /**
     * 多条件删除
     */
    Integer deleteByParam(FileShareQuery param);

    /**
     * 根据ShareId查询对象
     */
    FileShare getFileShareByShareId(String shareId);

    /**
     * 根据ShareId修改
     */
    Integer updateFileShareByShareId(FileShare bean, String shareId);

    /**
     * 根据ShareId删除
     */
    Integer deleteFileShareByShareId(String shareId);

    /**
     * 保存共享信息
     *
     * @param fileShare fileShare
     */
    void saveShare(FileShare fileShare);

    /**
     * 批量删除文件共享
     *
     * @param shareIds 共享 ID
     * @param userId   用户 ID
     */
    void deleteFileShareBatch(List<String> shareIds, String userId);

    /**
     * 检查分享码
     *
     * @param shareId 分享 ID
     * @param code    分享码
     * @return {@link SessionShareDto }
     */
    SessionShareDto checkShareCode(String shareId, String code);
}
