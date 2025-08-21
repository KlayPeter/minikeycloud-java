package com.easypan.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * 笔记分享信息表 数据库操作接口
 */
public interface NoteShareMapper<T, P> extends BaseMapper<T, P> {

    /**
     * 根据ShareId更新
     */
    Integer updateByShareId(
            @Param("bean")
                    T t,
            @Param("shareId")
                    String shareId);

    /**
     * 根据ShareId删除
     */
    Integer deleteByShareId(
            @Param("shareId")
                    String shareId);

    /**
     * 根据ShareId获取对象
     */
    T selectByShareId(
            @Param("shareId")
                    String shareId);

    /**
     * 更新浏览次数
     */
    void updateShowCount(
            @Param("shareId")
                    String shareId);

    /**
     * 根据笔记ID和用户ID删除分享
     */
    Integer deleteByNoteIdAndUserId(
            @Param("noteId")
                    String noteId,
            @Param("userId")
                    String userId);

    /**
     * 根据用户ID删除所有分享
     */
    Integer deleteByUserId(
            @Param("userId")
                    String userId);
}