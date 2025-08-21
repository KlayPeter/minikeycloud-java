package com.easypan.mappers;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文件分享信息 数据库操作接口
 */
public interface FileShareMapper<T, P> extends BaseMapper<T, P>
{

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
     * 批量删除文件共享
     *
     * @param shareIds 共享 ID
     * @param userId   用户 ID
     */
    Integer deleteFileShareBatch(
            @Param("shareIds")
                    List<String> shareIds,
            @Param("userId")
                    String userId);

    /**
     * 更新分享次数
     *
     * @param shareId 分享 ID
     */
    void updateShareShowCount(
            @Param("shareId")
                    String shareId);
}
