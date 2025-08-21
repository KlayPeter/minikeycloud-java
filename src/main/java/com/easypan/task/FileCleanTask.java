package com.easypan.task;

import com.easypan.entity.constants.Constants;
import com.easypan.entity.enums.FileDelFlagEnum;
import com.easypan.entity.po.FileInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.service.FileInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-08-29 15:35:32
 */
@Component
@Slf4j
public class FileCleanTask
{
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FileCleanTask.class);

    @Resource
    private FileInfoService fileInfoService;

    // 调试用
    // @Scheduled(fixedDelay = 1000 * 10)
    @Scheduled(fixedDelay = 864000000) // 10天 = 1000 * 60 * 60 * 24 * 10
    public void execute() {
        log.info("回收站文件清理任务执行中...");
        final List<FileInfo> fileInfoList = fileInfoService.findListByParam(FileInfoQuery
                .builder()
                .delFlag(FileDelFlagEnum.RECYCLE.getFlag())
                .queryExpireTime(true)
                .build());
        log.info("回收站文件清理任务执行中，共找到{}个文件", fileInfoList.size());
        final Map<String, List<String>> map = fileInfoList
                .stream()
                .collect(Collectors.groupingBy(FileInfo::getUserId, Collectors.mapping(FileInfo::getFileId, Collectors.toList())));
        map.forEach((userId, fileIds) -> fileInfoService.delFileBatch(userId, String.join(",", fileIds), false));
        log.info("回收站文件清理任务执行完毕");
    }
}
