package com.easypan.component;

import com.alibaba.fastjson.JSON;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.constants.RedisConstants;
import com.easypan.entity.dto.DownloadFileDto;
import com.easypan.entity.dto.SysSettingsDto;
import com.easypan.entity.dto.UserSpaceDto;
import com.easypan.entity.po.FileInfo;
import com.easypan.entity.po.UserInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.query.UserInfoQuery;
import com.easypan.mappers.FileInfoMapper;
import com.easypan.mappers.UserInfoMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-07-18 22:57:48
 */
@Component("redisComponent")
public class RedisComponent
{
    @Resource
    private RedisUtils redisUtils;

    @Resource
    private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;
    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    /**
     * 获取系统设置
     *
     * @return {@link SysSettingsDto }
     */
    public SysSettingsDto getSysSettings() {
        SysSettingsDto sysSettingsDto = (SysSettingsDto) redisUtils.get(RedisConstants.REDIS_KEY_SYS_SETTING);
        if (sysSettingsDto == null) {
            sysSettingsDto = new SysSettingsDto();
            redisUtils.set(RedisConstants.REDIS_KEY_SYS_SETTING, sysSettingsDto);
        }
        return sysSettingsDto;
    }

    /**
     * 保存系统设置
     *
     * @param sysSettingsDto 系统设置
     */
    public void saveSysSettings(SysSettingsDto sysSettingsDto) {
        redisUtils.set(RedisConstants.REDIS_KEY_SYS_SETTING, sysSettingsDto);
    }

    /**
     * 缓存用户空间使用情况
     *
     * @param userId       用户 ID
     * @param userSpaceDto 用户空间 DTO
     */
    public void saveUserSpaceUse(String userId, UserSpaceDto userSpaceDto) {
        redisUtils.setex(RedisConstants.REDIS_KEY_USER_SPACE_USE.concat(userId), userSpaceDto, RedisConstants.REDIS_KEY_EXPIRES_ONE_DAY.longValue());
    }

    /**
     * 获取用户空间使用情况
     *
     * @param userId 用户 ID
     * @return {@link UserSpaceDto }
     */
    public UserSpaceDto getUserSpaceUse(String userId) {
        UserSpaceDto userSpaceDto = (UserSpaceDto) redisUtils.get(RedisConstants.REDIS_KEY_USER_SPACE_USE + userId);
        if (userSpaceDto == null) {
            final Long useSpace = fileInfoMapper.selectUseSpace(userId);
            final SysSettingsDto sysSettings = getSysSettings();
            final Integer userInitUseSpace = sysSettings.getUserInitUseSpace();
            final Long totalSpace = (userInitUseSpace != null ? userInitUseSpace : 5) * Constants.MB;
            userSpaceDto = UserSpaceDto
                    .builder()
                    .useSpace(useSpace)
                    .totalSpace(totalSpace)
                    .build();
            saveUserSpaceUse(userId, userSpaceDto);
        }
        return userSpaceDto;
    }

    /**
     * 重置用户空间使用
     *
     * @param userId 用户 ID
     * @return {@link UserSpaceDto }
     */
    public UserSpaceDto resetUserSpaceUse(String userId) {
        final UserSpaceDto userSpaceDto = new UserSpaceDto();
        final Long useSpace = this.fileInfoMapper.selectUseSpace(userId);
        userSpaceDto.setUseSpace(useSpace);
        final UserInfo userInfo = this.userInfoMapper.selectByUserId(userId);
        userSpaceDto.setTotalSpace(userInfo.getTotalSpace());
        redisUtils.setex(RedisConstants.REDIS_KEY_USER_SPACE_USE + userId, userSpaceDto, RedisConstants.REDIS_KEY_EXPIRES_ONE_DAY.longValue());
        return userSpaceDto;
    }

    /**
     * 保存临时文件大小
     *
     * @param userId   用户 ID
     * @param fileId   文件 ID
     * @param fileSize 文件大小
     */
    public void saveTempFileSize(String userId, String fileId, Long fileSize) {
        final Long orgTempFileSize = getTempFileSize(userId, fileId);
        redisUtils.setex(RedisConstants.REDIS_KEY_USER_TEMP_FILE_SIZE + userId + "_" + fileId, orgTempFileSize + fileSize, RedisConstants.REDIS_KEY_EXPIRES_ONE_HOUR.longValue());
    }

    /**
     * 获取临时文件大小
     *
     * @param userId 用户 ID
     * @param fileId 文件 ID
     * @return {@link Long }
     */
    public Long getTempFileSize(String userId, String fileId) {
        return getTempFileSizeFromRedis(RedisConstants.REDIS_KEY_USER_TEMP_FILE_SIZE + userId + "_" + fileId);
    }

    private Long getTempFileSizeFromRedis(String key) {
        final Object value = redisUtils.get(key);
        if (value == null) {
            return 0L;
        }
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        return 0L;
    }

    /**
     * 保存下载码
     *
     * @param code            下载码
     * @param downloadFileDto 下载文件 DTO
     */
    public void saveDownloadCode(String code, DownloadFileDto downloadFileDto) {
        redisUtils.setex(RedisConstants.REDIS_KEY_DOWNLOAD + code, JSON.toJSONString(downloadFileDto), RedisConstants.REDIS_KEY_EXPIRES_FIVE_MIN.longValue());
    }

    /**
     * 获取下载码
     *
     * @param code 下载码
     * @return {@link DownloadFileDto }
     */
    public DownloadFileDto getDownloadCode(String code) {
        final String str = (String) redisUtils.get(RedisConstants.REDIS_KEY_DOWNLOAD + code);
        return JSON.parseObject(str, DownloadFileDto.class);
    }
}
