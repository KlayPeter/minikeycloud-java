package com.easypan.controller;

import com.easypan.component.RedisComponent;
import com.easypan.entity.config.AppConfig;
import com.easypan.entity.constants.FileFolderConstants;
import com.easypan.entity.constants.VideoConstants;
import com.easypan.entity.dto.DownloadFileDto;
import com.easypan.entity.enums.FileCategoryEnums;
import com.easypan.entity.enums.FileFolderTypeEnums;
import com.easypan.entity.po.FileInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.vo.FileInfoVo;
import com.easypan.entity.vo.FolderInfoVO;
import com.easypan.service.FileInfoService;
import com.easypan.utils.BeanCopyUtils;
import com.easypan.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-07-30 18:59:51
 */
@Slf4j
public class CommonFileController extends ABaseController
{
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CommonFileController.class);

    @Resource
    protected FileInfoService fileInfoService;

    @Resource
    protected AppConfig appConfig;

    @Resource
    protected RedisComponent redisComponent;

    /**
     * 获取图像
     *
     * @param response    响应
     * @param imageFolder 图像文件夹
     * @param imageName   图像名称
     */
    protected void getImage(HttpServletResponse response, String imageFolder, String imageName) {
        // 设置主体对象的媒体类型
        final String imageSuffix = FileUtils
                .getFileSuffix(imageName)
                .replace(".", "");
        response.setContentType("image/".concat(imageSuffix));

        // Cache-Control 操作缓存，30天过期
        response.setHeader("Cache-Control", "max-age=".concat(String.valueOf(60 * 60 * 24 * 30)));

        // 读取文件
        final String filePath = appConfig
                .getProjectFolder()
                .concat(FileFolderConstants.FILE_FOLDER_FILE)
                .concat(imageFolder)
                .concat(File.separator)
                .concat(imageName);
        readFile(response, filePath);
    }

    /**
     * 获取文件信息
     *
     * @param response 响应
     * @param fileId   文件 ID
     * @param userId   用户 ID
     */
    protected void getFileInfo(HttpServletResponse response, String fileId, String userId) {
        String realFilePath;

        // ts 分片文件
        if (fileId.endsWith(".ts")) {
            final String realFileId = fileId.split("_")[0];
            final FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(realFileId, userId);
            if (Objects.isNull(fileInfo)) {
                return;
            }

            realFilePath = appConfig
                    .getProjectFolder()
                    .concat(FileFolderConstants.FILE_FOLDER_FILE)
                    .concat(FileUtils.getFileNameNoSuffix(fileInfo.getFilePath()))
                    .concat(File.separator)
                    .concat(fileId);
        }
        else {
            final FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(fileId, userId);
            if (Objects.isNull(fileInfo)) {
                return;
            }

            // 视频
            if (FileCategoryEnums.VIDEO
                    .getCategory()
                    .equals(fileInfo.getFileCategory())) {
                // m3u8 索引文件
                realFilePath = appConfig
                        .getProjectFolder()
                        .concat(FileFolderConstants.FILE_FOLDER_FILE)
                        .concat(FileUtils.getFileNameNoSuffix(fileInfo.getFilePath()))
                        .concat(File.separator)
                        .concat(VideoConstants.M3U8_NAME);
            }
            // 其他
            else {
                realFilePath = appConfig
                        .getProjectFolder()
                        .concat(FileFolderConstants.FILE_FOLDER_FILE)
                        .concat(fileInfo.getFilePath());
            }
            log.info("检查文件是否存在: {}", realFilePath);
            File file = new File(realFilePath);
            boolean exists = file.exists();
            log.info("File.exists()结果: {}", exists);
            log.info("文件路径字节长度: {}", realFilePath.getBytes().length);
            log.info("文件绝对路径: {}", file.getAbsolutePath());
            if (FileUtils.notExists(realFilePath)) {
                log.error("文件不存在，提前返回: {}", realFilePath);
                return;
            }
            log.info("文件存在检查通过: {}", realFilePath);
        }
        
        log.info("读取文件：{}", realFilePath);
        // 读取文件到响应流
        readFile(response, realFilePath);
    }

    /**
     * 获取文件夹信息
     *
     * @param path   路径
     * @param userId 用户 ID
     * @return {@link List }<{@link FileInfoVo }>
     */
    protected List<FolderInfoVO> getFolderInfo(String path, String userId) {
        final String[] fileIdArray = path.split("/");
        final List<FileInfo> fileInfoList = this.fileInfoService.findListByParam(FileInfoQuery
                .builder()
                .userId(userId)
                .folderType(FileFolderTypeEnums.FOLDER.getType())
                .fileIdArray(fileIdArray)
                .orderBy("field(file_id, '" + StringUtils.join(fileIdArray, "','") + "')")
                .build());
        return BeanCopyUtils.copyList(fileInfoList, FolderInfoVO.class);
    }

    protected void download(HttpServletRequest request, HttpServletResponse response, String code) throws UnsupportedEncodingException {
        final DownloadFileDto downloadFileDto = redisComponent.getDownloadCode(code);
        if (Objects.isNull(downloadFileDto)) {
            return;
        }

        // 获取文件路径和名称
        final String filePath = appConfig
                .getProjectFolder()
                .concat(FileFolderConstants.FILE_FOLDER_FILE)
                .concat(downloadFileDto.getFilePath());
        String fileName = downloadFileDto.getFileName();

        // 下载装配准备
        response.setContentType("application/x-msdownload; charset=UTF-8");
        if (request
                .getHeader("User-Agent")
                .toLowerCase()
                .indexOf("msie") > 0) {//IE浏览器
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name());
        }
        else {
            fileName = new String(fileName.getBytes(StandardCharsets.UTF_8.name()), StandardCharsets.ISO_8859_1.name());
        }
        response.setHeader("Content-Disposition", "attachment;filename=\""
                .concat(fileName)
                .concat("\""));

        // 响应写文件流
        readFile(response, filePath);
    }
}
