package com.easypan.mappers;

import com.easypan.entity.po.FileInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文件信息表 数据库操作接口
 */
public interface FileInfoMapper<T, P> extends BaseMapper<T, P>
{

    /**
     * 根据FileIdAndUserId更新
     */
    Integer updateByFileIdAndUserId(
            @Param("bean")
                    T t,
            @Param("fileId")
                    String fileId,
            @Param("userId")
                    String userId);

    /**
     * 根据FileIdAndUserId更新
     */
    Integer updateByFileIdAndUserIdWithOldStatus(
            @Param("bean")
                    T t,
            @Param("fileId")
                    String fileId,
            @Param("userId")
                    String userId,
            // 旧状态，乐观锁的作用
            @Param("oldStatus")
                    Integer oldStatus);

    /**
     * 根据FileIdAndUserId删除
     */
    Integer deleteByFileIdAndUserId(
            @Param("fileId")
                    String fileId,
            @Param("userId")
                    String userId);

    /**
     * 根据FileIdAndUserId获取对象
     */
    T selectByFileIdAndUserId(
            @Param("fileId")
                    String fileId,
            @Param("userId")
                    String userId);

    /**
     * 查询使用空间
     *
     * @param userId 用户 ID
     * @return {@link Long }
     */
    Long selectUseSpace(
            @Param("userId")
                    String userId);

    /**
     * 批量更新删除状态标识
     *
     * @param userId           用户 ID
     * @param delSubFileIdList delSubFileIdList
     * @param oldDelFlag       删除标识
     * @param updateFileInfo   更新文件信息
     */
    void updateDelFlagBatch(
            @Param("userId")
                    String userId,
            @Param("delSubFileIdList")
                    List<String> delSubFileIdList,
            @Param("oldDelFlag")
                    Integer oldDelFlag,
            @Param("bean")
                    FileInfo updateFileInfo);

    /**
     * 彻底删除文件
     *
     * @param userId           用户 ID
     * @param delSubFileIdList delSubFileIdList
     * @param oldDelFlag       删除标识
     */
    void delFileBatch(
            @Param("userId")
                    String userId,
            @Param("delSubFileIdList")
                    List<String> delSubFileIdList,
            @Param("oldDelFlag")
                    Integer oldDelFlag);

    /**
     * 根据UserId删除
     */
    Integer deleteByUserId(
            @Param("userId")
                    String userId);
}
