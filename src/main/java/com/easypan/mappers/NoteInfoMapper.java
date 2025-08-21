package com.easypan.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * 笔记信息表 数据库操作接口
 */
public interface NoteInfoMapper<T, P> extends BaseMapper<T, P> {

    /**
     * 根据NoteIdAndUserId更新
     */
    Integer updateByNoteIdAndUserId(
            @Param("bean")
                    T t,
            @Param("noteId")
                    String noteId,
            @Param("userId")
                    String userId);

    /**
     * 根据NoteIdAndUserId删除
     */
    Integer deleteByNoteIdAndUserId(
            @Param("noteId")
                    String noteId,
            @Param("userId")
                    String userId);

    /**
     * 根据NoteIdAndUserId获取对象
     */
    T selectByNoteIdAndUserId(
            @Param("noteId")
                    String noteId,
            @Param("userId")
                    String userId);

    /**
     * 更新查看次数
     */
    void updateViewCount(
            @Param("noteId")
                    String noteId,
            @Param("userId")
                    String userId);

    /**
     * 根据用户ID删除所有笔记
     */
    Integer deleteByUserId(
            @Param("userId")
                    String userId);
}