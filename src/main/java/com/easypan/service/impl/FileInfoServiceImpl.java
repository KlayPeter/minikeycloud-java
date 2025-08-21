package com.easypan.service.impl;

import com.easypan.component.RedisComponent;
import com.easypan.entity.config.AppConfig;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.constants.FileFolderConstants;
import com.easypan.entity.constants.ImageConstants;
import com.easypan.entity.constants.VideoConstants;
import com.easypan.entity.dto.DownloadFileDto;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.dto.UploadFileDto;
import com.easypan.entity.dto.UserSpaceDto;
import com.easypan.entity.enums.DateTimePatternEnum;
import com.easypan.entity.enums.FileDelFlagEnum;
import com.easypan.entity.enums.FileFolderTypeEnums;
import com.easypan.entity.enums.FileStatusEnum;
import com.easypan.entity.enums.FileTypeEnums;
import com.easypan.entity.enums.PageSize;
import com.easypan.entity.enums.ResponseCodeEnum;
import com.easypan.entity.enums.UploadStatusEnum;
import com.easypan.entity.po.FileInfo;
import com.easypan.entity.po.UserInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.query.SimplePage;
import com.easypan.entity.query.UserInfoQuery;
import com.easypan.entity.vo.FileInfoVo;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.vo.UploadResultVo;
import com.easypan.exception.BusinessException;
import com.easypan.mappers.FileInfoMapper;
import com.easypan.mappers.UserInfoMapper;
import com.easypan.service.FileInfoService;
import com.easypan.utils.BeanCopyUtils;
import com.easypan.utils.CollectionUtils;
import com.easypan.utils.DateUtil;
import com.easypan.utils.FileUtils;
import com.easypan.utils.ProcessUtils;
import com.easypan.utils.RandomUtils;
import com.easypan.utils.StringUtils;
import com.easypan.utils.ThumbnailUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 文件信息表 业务接口实现
 */
@Service("fileInfoService")
@Slf4j
public class FileInfoServiceImpl implements FileInfoService
{
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FileInfoServiceImpl.class);

    @Resource
    private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private AppConfig appConfig;

    // 要保证异步方法在事务中生效，就不能通过this 直接调用，否则不会交给Spring管理。
    @Resource
    // 需要自己注入自己，但是直接注入的话，会报循环依赖的错误。 @Lazy专为解决循环依赖而生
    @Lazy
    private FileInfoServiceImpl fileInfoService;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<FileInfo> findListByParam(final FileInfoQuery param) {
        return this.fileInfoMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(final FileInfoQuery param) {
        return this.fileInfoMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<FileInfo> findListByPage(final FileInfoQuery param) {
        final int count = this.findCountByParam(param);
        final int pageSize = param.getPageSize() == null ?
                             PageSize.SIZE15.getSize() :
                             param.getPageSize();

        final SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        final List<FileInfo> list = this.findListByParam(param);
        return (PaginationResultVO<FileInfo>) new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
    }

    /**
     * 新增
     */
    @Override
    public Integer add(final FileInfo bean) {
        return this.fileInfoMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(final List<FileInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.fileInfoMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(final List<FileInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.fileInfoMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(final FileInfo bean, final FileInfoQuery param) {
        StringUtils.checkParam(param);
        return this.fileInfoMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(final FileInfoQuery param) {
        StringUtils.checkParam(param);
        return this.fileInfoMapper.deleteByParam(param);
    }

    /**
     * 根据FileIdAndUserId获取对象
     */
    @Override
    public FileInfo getFileInfoByFileIdAndUserId(final String fileId, final String userId) {
        return this.fileInfoMapper.selectByFileIdAndUserId(fileId, userId);
    }

    /**
     * 根据FileIdAndUserId修改
     */
    @Override
    public Integer updateFileInfoByFileIdAndUserId(final FileInfo bean, final String fileId, final String userId) {
        return this.fileInfoMapper.updateByFileIdAndUserId(bean, fileId, userId);
    }

    /**
     * 根据FileIdAndUserId删除
     */
    @Override
    public Integer deleteFileInfoByFileIdAndUserId(final String fileId, final String userId) {
        return this.fileInfoMapper.deleteByFileIdAndUserId(fileId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UploadResultVo uploadFile(final SessionWebUserDto userDto, final UploadFileDto uploadFileDto) {
        String fileId = uploadFileDto.getFileId();
        final MultipartFile file = uploadFileDto.getFile();
        String fileName = uploadFileDto.getFileName();
        final String filePid = uploadFileDto.getFilePid();
        final String fileMd5 = uploadFileDto.getFileMd5();
        final Integer chunkIndex = uploadFileDto.getChunkIndex();
        final Integer chunkTotal = uploadFileDto.getChunks();
        final String userId = userDto.getUserId();

        // 初始化返回结果
        final UploadResultVo resultVo = new UploadResultVo();
        if (StringUtils.isEmpty(fileId)) {
            fileId = RandomUtils.getRandomString(Constants.LENGTH_10);
        }
        resultVo.setFileId(fileId);

        final Date curDate = new Date();
        final UserSpaceDto userSpaceDto = redisComponent.getUserSpaceUse(userId);
        final Long useSpace = userSpaceDto.getUseSpace();
        final Long totalSpace = userSpaceDto.getTotalSpace();

        boolean uploadSuccess = true;
        File tempFileFolderPath = null;
        try {
            // 第一个分片
            if (chunkIndex == 0) {
                final List<FileInfo> fileInfoList = this.fileInfoMapper.selectList(FileInfoQuery
                        .builder()
                        .fileMd5(fileMd5)
                        .status(FileStatusEnum.USING.getStatus())
                        .simplePage(new SimplePage(0, 1))
                        .build());
                /** 秒传 **/
                // 逻辑理解：如果文件已存在，则不进行真实的物理存储，但是需要记录文件上传信息，同时更新用户使用空间
                if (!CollectionUtils.isEmpty(fileInfoList)) {
                    final FileInfo fileInfo = fileInfoList.get(0);

                    // 判断文件大小
                    final Long fileSize = fileInfo.getFileSize();
                    if (fileSize + useSpace > totalSpace) {
                        throw new BusinessException(ResponseCodeEnum.CODE_904);
                    }

                    // 文件重命名
                    fileName = autoRename(userId, filePid, fileName);

                    // 入库
                    fileInfo
                            .setUserId(userId)
                            .setFileId(fileId)
                            .setFilePid(filePid)
                            .setFileMd5(fileMd5)
                            .setFileName(fileName)
                            .setCreateTime(curDate)
                            .setLastUpdateTime(curDate)
                            .setStatus(FileStatusEnum.USING.getStatus())
                            .setDelFlag(FileDelFlagEnum.USING.getFlag());
                    this.fileInfoMapper.insert(fileInfo);

                    // 更新用户使用空间
                    updateUserSpace(userId, userSpaceDto, fileSize);

                    // 文件上传状态
                    resultVo.setStatus(UploadStatusEnum.UPLOAD_SECONDS.getCode());

                    return resultVo;
                }
            }

            /** 分片上传 **/
            // 判断磁盘空间大小
            // 当前文件大小 + 临时文件大小 + 用户已使用空间大小 > 用户总空间大小
            // Q：为何要有临时文件大小？
            // A：因为文件是分片上传的，不会立即更新到数据库，所以需要临时记录文件大小到缓存中，当文件上传完成后，再更新到数据库中
            // 因此这里大小从缓存中获取，然后对磁盘空间大小进行判断
            final Long tempFileSize = redisComponent.getTempFileSize(userId, fileId);
            if (file.getSize() + tempFileSize + useSpace > totalSpace) {
                throw new BusinessException(ResponseCodeEnum.CODE_904);
            }

            // 暂存临时目录
            tempFileFolderPath = getTempFileFolderPath(fileId, userId);
            if (!tempFileFolderPath.exists()) {
                tempFileFolderPath.mkdirs();
            }
            final File tempFile = new File(tempFileFolderPath.getPath(), String.valueOf(chunkIndex));
            file.transferTo(tempFile);

            // 缓存临时文件大小
            redisComponent.saveTempFileSize(userId, fileId, file.getSize());

            // 如果不是最后一个分片
            if (chunkIndex < chunkTotal - 1) {
                // 文件上传状态
                resultVo.setStatus(UploadStatusEnum.UPLOADING.getCode());
                return resultVo;
            }

            /** 最后一个分片 **/

            final String month = DateUtil.format(new Date(), DateTimePatternEnum.YYYY_MM.getPattern());
            final String fileSuffix = FileUtils.getFileSuffix(fileName);
            final FileTypeEnums fileTypeEnums = FileTypeEnums.getFileTypeBySuffix(fileSuffix);
            final String realFileName = getCurrentUserFolderName(fileId, userId) + fileSuffix;
            fileName = autoRename(userId, filePid, fileName);

            // 入库
            final FileInfo fileInfo = new FileInfo()
                    .setUserId(userId)
                    .setFileId(fileId)
                    .setFilePid(filePid)
                    .setFileMd5(fileMd5)
                    .setFileName(fileName)
                    .setFilePath(month + File.separator + realFileName)
                    .setFileType(fileTypeEnums.getType())
                    .setFolderType(FileFolderTypeEnums.FILE.getType())
                    .setFileCategory(fileTypeEnums
                            .getCategory()
                            .getCategory())
                    .setCreateTime(curDate)
                    .setLastUpdateTime(curDate)
                    .setStatus(FileStatusEnum.TRANSFER.getStatus())
                    .setDelFlag(FileDelFlagEnum.USING.getFlag());
            this.fileInfoMapper.insert(fileInfo);

            // 更新用户使用空间
            // 这里的 totalSize 是所有分片的文件大小之和，即文件的总大小
            final Long totalSize = redisComponent.getTempFileSize(userId, fileId);
            updateUserSpace(userId, userSpaceDto, totalSize);

            // 文件上传状态
            resultVo.setStatus(UploadStatusEnum.UPLOAD_FINISH.getCode());

            // 异步转码
            String finalFileId = fileId;
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization()
            {
                @Override
                public void afterCommit() {
                    fileInfoService.transferFile(userId, finalFileId);
                }
            });

            return resultVo;
        }
        catch (Exception e) {
            log.error("上传文件失败：{}", e.getMessage(), e);
            uploadSuccess = false;
        }
        finally {
            if (!uploadSuccess && Objects.nonNull(tempFileFolderPath)) {
                FileUtils.deleteDirectory(tempFileFolderPath.getPath());
            }
        }
        return resultVo;
    }

    /**
     * 新建文件夹
     *
     * @param userId   用户 ID
     * @param filePid  文件 PID
     * @param fileName 文件名
     * @return {@link FileInfoVo }
     */
    @Override
    public FileInfoVo newFolder(String userId, String filePid, String fileName) {
        // 检查文件名
        checkFileName(userId, filePid, fileName, FileFolderTypeEnums.FOLDER.getType());

        // 入库
        return saveNewFolder(userId, filePid, fileName);
    }

    @Override
    public FileInfoVo rename(String userId, String fileId, String fileName) {
        final FileInfo fileInfo = this.fileInfoMapper.selectByFileIdAndUserId(fileId, userId);
        if (Objects.isNull(fileInfo)) {
            throw new BusinessException("文件不存在");
        }

        // 检查同级目录下是否存在同名文件名
        final String fileSuffix = FileUtils.getFileSuffix(fileInfo.getFileName());
        checkFileName(userId, fileInfo.getFilePid(), fileName.concat(fileSuffix), fileInfo.getFolderType());

        // 如果是文件，则需要加上文件后缀
        if (FileFolderTypeEnums.FILE
                .getType()
                .equals(fileInfo.getFolderType())) {
            fileName += fileSuffix;
        }

        // 更新文件名
        this.fileInfoMapper.updateByFileIdAndUserId(fileInfo
                .setFileName(fileName)
                .setLastUpdateTime(new Date()), fileId, userId);

        // 返回文件信息
        return BeanCopyUtils.copy(fileInfo, FileInfoVo.class);
    }

    @Override
    public List<FileInfoVo> loadAllFolder(String userId, String filePid, String currentFileIds) {
        final FileInfoQuery fileInfoQuery = FileInfoQuery
                .builder()
                .userId(userId)
                .filePid(filePid)
                .folderType(FileFolderTypeEnums.FOLDER.getType())
                .orderBy("create_time asc")
                .delFlag(FileDelFlagEnum.USING.getFlag())
                .build();
        if (StringUtils.isNotEmpty(currentFileIds)) {
            fileInfoQuery.setExcludedFileIdArray(currentFileIds.split(","));
        }
        final List<FileInfo> fileInfoList = this.findListByParam(fileInfoQuery);
        return BeanCopyUtils.copyList(fileInfoList, FileInfoVo.class);
    }

    @Override
    public void changeFileFolder(String userId, String fileIds, String filePid) {
        if (fileIds.contains(filePid)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        // 目标文件夹存在性校验
        if (!Constants.ZERO_STR.equals(filePid)) {
            final FileInfo fileInfo = this.getFileInfoByFileIdAndUserId(filePid, userId);
            if (Objects.isNull(fileInfo) || !FileDelFlagEnum.USING
                    .getFlag()
                    .equals(fileInfo.getDelFlag())) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
        }

        // 查询目标文件夹下的文件名
        final List<String> fileNameInTargetFolderList = this
                .findListByParam(FileInfoQuery
                        .builder()
                        .userId(userId)
                        .filePid(filePid)
                        .build())
                .stream()
                .map(FileInfo::getFileName)
                .collect(Collectors.toList());

        // 查询所选文件信息
        final List<FileInfo> selectedFileInfoList = this.findListByParam(FileInfoQuery
                .builder()
                .userId(userId)
                .fileIdArray(fileIds.split(","))
                .build());

        // 批量更新所选文件父级目录
        selectedFileInfoList.forEach(fileInfo -> {
            // 更新文件父级ID
            fileInfo.setFilePid(filePid);
            // 存在同名文件则重命名
            final boolean sameName = fileNameInTargetFolderList.contains(fileInfo.getFileName());
            if (sameName) {
                fileInfo.setFileName(FileUtils.rename(fileInfo.getFileName()));
            }
        });
        this.addOrUpdateBatch(selectedFileInfoList);
    }

    @Override
    public DownloadFileDto createDownloadUrl(String userId, String fileId) {
        // 前置验证
        FileInfo fileInfo = fileInfoMapper.selectByFileIdAndUserId(fileId, userId);
        if (Objects.isNull(fileInfo)) {
            throw new BusinessException("文件不存在");
        }
        if (!FileFolderTypeEnums.FILE
                .getType()
                .equals(fileInfo.getFolderType())) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        // 构建下载文件DTO
        final String code = RandomUtils.getRandomString(Constants.LENGTH_50);
        final DownloadFileDto downloadFileDto = DownloadFileDto
                .builder()
                .downloadCode(code)
                .fileName(fileInfo.getFileName())
                .filePath(fileInfo.getFilePath())
                .build();

        // 保存下载码
        redisComponent.saveDownloadCode(code, downloadFileDto);

        return downloadFileDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delFile(String userId, String fileIds) {
        // 1、根据文件 ID‘s 查找文件自身信息
        final String[] fileIdArray = fileIds.split(",");
        final List<FileInfo> fileInfoList = this.findListByParam(FileInfoQuery
                .builder()
                .userId(userId)
                .fileIdArray(fileIdArray)
                .delFlag(FileDelFlagEnum.USING.getFlag())
                .build());
        if (CollectionUtils.isEmpty(fileInfoList)) {
            return;
        }

        // 2、当所选文件中存在文件夹时，将目录的 ID‘s 作为子文件的 PID’s，递归查找所有子文件信息
        final List<String> delSubFileIdList = new ArrayList<>();
        fileInfoList.stream()
                    // 筛选文件夹
                    .filter(fileInfo -> FileFolderTypeEnums.FOLDER
                            .getType()
                            .equals(fileInfo.getFolderType()))
                    // 递归查找子文件
                    .forEach(fileInfo -> findSubFolderFileIdList(delSubFileIdList, userId, fileInfo.getFileId(), FileDelFlagEnum.USING.getFlag()));

        // 3、删除所有子文件，这里为“假删除”（即更新文件的 delflag 状态为 删除 状态）
        if (CollectionUtils.isNotEmpty(delSubFileIdList)) {
            final FileInfo updateFileInfo = new FileInfo().setDelFlag(FileDelFlagEnum.DEL.getFlag());
            this.fileInfoMapper.updateDelFlagBatch(userId, delSubFileIdList, FileDelFlagEnum.USING.getFlag(), updateFileInfo);
        }

        // 4、删除所选文件，也为“假删除”（即更新文件的 delflag 状态为 回收站 状态）
        final FileInfo updateFileInfo = new FileInfo()
                .setDelFlag(FileDelFlagEnum.RECYCLE.getFlag())
                .setRecoveryTime(new Date());
        this.fileInfoMapper.updateDelFlagBatch(userId, Arrays.asList(fileIdArray), FileDelFlagEnum.USING.getFlag(), updateFileInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recoveryFileBatch(String userId, String fileIds) {
        // 查询用户回收站的所选文件信息
        final String[] fileIdArray = fileIds.split(",");
        final List<FileInfo> fileInfoList = this.findListByParam(FileInfoQuery
                .builder()
                .userId(userId)
                .delFlag(FileDelFlagEnum.RECYCLE.getFlag())
                .fileIdArray(fileIdArray)
                .build());

        // 所选文件子目录ID集合
        List<String> delSubFileIdList = new ArrayList<>();
        fileInfoList
                .stream()
                .filter(fileInfo -> FileFolderTypeEnums.FOLDER
                        .getType()
                        .equals(fileInfo.getFolderType()))
                .forEach(fileInfo -> findSubFolderFileIdList(delSubFileIdList, userId, fileInfo.getFileId(), FileDelFlagEnum.DEL.getFlag()));

        // 查找所有根目录文件
        final List<String> allRootFileNameList = this
                .findListByParam(FileInfoQuery
                        .builder()
                        .userId(userId)
                        .delFlag(FileDelFlagEnum.USING.getFlag())
                        .filePid(Constants.ZERO_STR)
                        .build())
                .stream()
                .map(FileInfo::getFileName)
                .collect(Collectors.toList());

        // 将目录下所有删除文件更新为使用中
        if (CollectionUtils.isNotEmpty(delSubFileIdList)) {
            final FileInfo updateFileInfo = new FileInfo().setDelFlag(FileDelFlagEnum.USING.getFlag());
            fileInfoMapper.updateDelFlagBatch(userId, delSubFileIdList, FileDelFlagEnum.DEL.getFlag(), updateFileInfo);
        }

        // 将所选文件更新为使用中，父目录更新到根目录
        final List<String> fileIdList = Arrays.asList(fileIdArray);
        final FileInfo updateFileInfo = new FileInfo()
                .setDelFlag(FileDelFlagEnum.USING.getFlag())
                .setFilePid(Constants.ZERO_STR)
                .setLastUpdateTime(new Date());
        fileInfoMapper.updateDelFlagBatch(userId, fileIdList, FileDelFlagEnum.RECYCLE.getFlag(), updateFileInfo);

        // 文件重命名
        for (FileInfo fileInfo : fileInfoList) {
            final String fileName = fileInfo.getFileName();
            if (allRootFileNameList.contains(fileName)) {
                final FileInfo updateFileInfo1 = new FileInfo().setFileName(FileUtils.rename(fileName));
                fileInfoMapper.updateByFileIdAndUserId(updateFileInfo1, fileInfo.getFileId(), userId);
            }
        }
    }

    @Override
    public void delFileBatch(String userId, String fileIds, boolean adminOp) {
        // 查询用户回收站的所选文件信息
        final String[] fileIdArray = fileIds.split(",");
        final FileInfoQuery query = FileInfoQuery
                .builder()
                .userId(userId)
                .fileIdArray(fileIdArray)
                .build();
        if (!adminOp) {
            query.setDelFlag(FileDelFlagEnum.RECYCLE.getFlag());
        }
        final List<FileInfo> fileInfoList = this.findListByParam(query);

        // 所选文件子目录ID集合
        List<String> delSubFileIdList = new ArrayList<>();
        fileInfoList
                .stream()
                .filter(fileInfo -> FileFolderTypeEnums.FOLDER
                        .getType()
                        .equals(fileInfo.getFolderType()))
                .forEach(fileInfo -> findSubFolderFileIdList(delSubFileIdList, userId, fileInfo.getFileId(), FileDelFlagEnum.DEL.getFlag()));

        // 将目录下所有文件彻底删除
        if (CollectionUtils.isNotEmpty(delSubFileIdList)) {
            fileInfoMapper.delFileBatch(userId, delSubFileIdList, adminOp ?
                                                                  null :
                                                                  FileDelFlagEnum.DEL.getFlag());
        }

        // 将所选文件彻底删除
        fileInfoMapper.delFileBatch(userId, Arrays.asList(fileIdArray), adminOp ?
                                                                        null :
                                                                        FileDelFlagEnum.RECYCLE.getFlag());

        // 查询并更新用户空间
        final Long useSpace = fileInfoMapper.selectUseSpace(userId);
        userInfoMapper.updateUserSpace(userId, useSpace, null);

        // 更新用户已使用空间至Redis缓存
        UserSpaceDto userSpaceDto = new UserSpaceDto();
        userSpaceDto.setUseSpace(useSpace);
        redisComponent.saveUserSpaceUse(userId, userSpaceDto);
    }

    @Override
    public void checkRootFilePid(String rootFilePid, String userId, String fileId) {
        if (StringUtils.isEmpty(rootFilePid)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        if (rootFilePid.equals(fileId)) {
            return;
        }

        checkFilePid(rootFilePid, fileId, userId);
    }

    @Override
    public void saveShare(String fileId, String shareFileIds, String myFolderId, String shareUserId, String currentUserId) {
        // 1、目标目录文件名列表
        final List<String> currentFileList = this.fileInfoMapper
                .selectList(FileInfoQuery
                        .builder()
                        .userId(currentUserId)
                        .filePid(myFolderId)
                        .build())
                .stream()
                .map(FileInfo::getFileName)
                .collect(Collectors.toList());

        // 2、分享文件列表
        final List<FileInfo> shareFileInfoList = this.fileInfoMapper.selectList(FileInfoQuery
                .builder()
                .userId(shareUserId)
                .fileIdArray(shareFileIds.split(","))
                .build());

        // 3、递归查询子目录文件
        final List<FileInfo> copyFileList = new ArrayList<>();
        final Date curDate = new Date();
        shareFileInfoList.forEach(shareFileInfo -> {
            final String fileName = shareFileInfo.getFileName();
            // 重命名
            if (currentFileList.contains(fileName)) {
                shareFileInfo.setFileName(FileUtils.rename(fileName));
            }
            findAllSubFile(copyFileList, shareFileInfo, shareUserId, currentUserId, myFolderId, curDate);
        });

        // 4、保存入库
        this.fileInfoMapper.insertBatch(copyFileList);
    }

    /**
     * 查找所有子文件
     *
     * @param copyFileList   复制文件列表
     * @param fileInfo       文件信息
     * @param srcUserId      源用户 ID
     * @param targetUserId   目标用户 ID
     * @param targetFolderId 目标文件夹 ID
     * @param curDate        当前日期
     */
    private void findAllSubFile(List<FileInfo> copyFileList, FileInfo fileInfo, String srcUserId, String targetUserId, String targetFolderId, Date curDate) {
        final String srcFileId = fileInfo.getFileId();
        final String targetFileId = RandomUtils.getRandomString(Constants.LENGTH_10);
        copyFileList.add(fileInfo
                .setCreateTime(curDate)
                .setLastUpdateTime(curDate)
                .setUserId(targetUserId)
                .setFilePid(targetFolderId)
                .setFileId(targetFileId));
        if (FileFolderTypeEnums.FOLDER
                .getType()
                .compareTo(fileInfo.getFolderType()) == 0) {
            this.fileInfoMapper
                    .selectList(FileInfoQuery
                            .builder()
                            .userId(srcUserId)
                            .filePid(srcFileId)
                            .build())
                    .forEach(subFileInfo -> findAllSubFile(copyFileList, subFileInfo, srcUserId, targetUserId, targetFileId, curDate));
        }
    }

    private void checkFilePid(String rootFilePid, String fileId, String userId) {
        final FileInfo fileInfo = this.getFileInfoByFileIdAndUserId(fileId, userId);
        if (Objects.isNull(fileInfo)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 根目录
        if (Constants.ZERO_STR.equals(fileInfo.getFilePid())) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 直接父目录
        if (fileInfo
                .getFilePid()
                .equals(rootFilePid)) {
            return;
        }
        // 递归验证父目录
        checkFilePid(rootFilePid, fileInfo.getFilePid(), userId);
    }

    /**
     * 递归查找文件夹下所有子文件的 ID 集合
     *
     * @param subFileIdList 子文件夹文件 ID 集合
     * @param userId        用户 ID
     * @param fileId        文件 ID
     */
    private void findSubFolderFileIdList(List<String> subFileIdList, String userId, String fileId, Integer delFlag) {
        // 查找文件夹下的子文件，包括文件和文件夹
        final List<FileInfo> fileInfoList = this.findListByParam(FileInfoQuery
                .builder()
                .userId(userId)
                .filePid(fileId)
                .delFlag(delFlag)
                .build());
        fileInfoList.forEach(fileInfo -> {
            // 如果是文件，则直接添加到集合中，并到此为止
            subFileIdList.add(fileInfo.getFileId());
            // 如果是文件夹，还需要递归查找
            if (FileFolderTypeEnums.FOLDER
                    .getType()
                    .equals(fileInfo.getFolderType())) {
                findSubFolderFileIdList(subFileIdList, userId, fileInfo.getFileId(), delFlag);
            }
        });
    }

    /**
     * 保存新文件夹
     *
     * @param userId   用户 ID
     * @param filePid  文件 PID
     * @param fileName 文件名
     */
    private FileInfoVo saveNewFolder(String userId, String filePid, String fileName) {
        final Date curDate = new Date();
        final FileInfo fileInfo = new FileInfo()
                .setFileId(RandomUtils.getRandomString(Constants.LENGTH_10))
                .setUserId(userId)
                .setFilePid(filePid)
                .setFileName(fileName)
                .setFolderType(FileFolderTypeEnums.FOLDER.getType())
                .setCreateTime(curDate)
                .setLastUpdateTime(curDate)
                .setStatus(FileStatusEnum.USING.getStatus())
                .setDelFlag(FileDelFlagEnum.USING.getFlag());
        this.fileInfoMapper.insert(fileInfo);

        return BeanCopyUtils.copy(fileInfo, FileInfoVo.class);
    }

    /**
     * 检查文件名
     *
     * @param userId     用户 ID
     * @param filePid    文件 PID
     * @param fileName   文件名
     * @param folderType 文件夹类型
     */
    private void checkFileName(String userId, String filePid, String fileName, Integer folderType) {
        final Integer count = this.fileInfoMapper.selectCount(FileInfoQuery
                .builder()
                .userId(userId)
                .filePid(filePid)
                .fileName(fileName)
                .folderType(folderType)
                .delFlag(FileDelFlagEnum.USING.getFlag())
                .build());
        if (count > 0) {
            throw new BusinessException("此目录下已经存在同名文件，请修改名称");
        }
    }

    @Async
    public void transferFile(String userId, String fileId) {
        FileInfo fileInfo = fileInfoMapper.selectByFileIdAndUserId(fileId, userId);
        if (Objects.isNull(fileInfo) || StringUtils.isEmpty(fileInfo.getFilePath())) {
            return;
        }

        /* 临时文件 */
        final String tempFileFolderPath = getTempFileFolderPath(fileId, userId).getPath();

        /* 目标文件 */
        final String month = DateUtil.format(fileInfo.getCreateTime(), DateTimePatternEnum.YYYY_MM.getPattern());
        final File realFileFolderPath = getRealFileFolderPath(month);
        if (!realFileFolderPath.exists()) {
            realFileFolderPath.mkdirs();
        }
        final String currentUserFolderName = getCurrentUserFolderName(fileId, userId);
        final String fileSuffix = FileUtils.getFileSuffix(fileInfo.getFileName());
        final String realFileName = currentUserFolderName.concat(fileSuffix);
        final String targetFilePath = Paths
                .get(realFileFolderPath.getPath(), realFileName)
                .toString();

        boolean transferSuccess = true;
        String cover = null;
        try {
            /* 合并文件 */
            mergeFile(tempFileFolderPath, targetFilePath, true);

            /* 处理文件 */
            final int type = FileTypeEnums
                    .getFileTypeBySuffix(fileSuffix)
                    .getType();
            if (type == FileTypeEnums.VIDEO.getType()) {
                // 切割视频
                cutFile4Video(fileId, targetFilePath);

                // 生成缩略图
                // 这里之所以不用 File.separator 代替 "/"，是因为这里是网页地址，而非实体文件路径
                cover = month + "/" + currentUserFolderName.concat(ImageConstants.IMAGE_SUFFIX_PNG);
                final String coverPath = Paths
                        .get(realFileFolderPath.getPath(), currentUserFolderName.concat(ImageConstants.IMAGE_SUFFIX_PNG))
                        .toString();
                ThumbnailUtils.generateVideoThumbnail(new File(targetFilePath), ImageConstants.IMAGE_WIDTH_150, new File(coverPath));
            }
            else if (type == FileTypeEnums.IMAGE.getType()) {
                // 生成缩略图
                cover = month + "/" + realFileName.replace(".", "_.");
                final String coverPath = Paths
                        .get(realFileFolderPath.getPath(), realFileName.replace(".", "_."))
                        .toString();
                final Boolean created = ThumbnailUtils.generatePictureThumbnail(new File(targetFilePath), ImageConstants.IMAGE_WIDTH_150, new File(coverPath), false);
                if (!created) {
                    FileUtils.copyFile(new File(targetFilePath), new File(coverPath));
                }
            }
        }
        catch (Exception e) {
            log.error("合并文件失败：{}", e.getMessage(), e);
            transferSuccess = false;
        }
        finally {
            /* 文件信息入库 */
            fileInfo = new FileInfo()
                    .setFileSize(new File(targetFilePath).length())
                    .setFileCover(cover)
                    .setStatus(transferSuccess ?
                               FileStatusEnum.USING.getStatus() :
                               FileStatusEnum.TRANSFER_FAIL.getStatus());
            this.fileInfoMapper.updateByFileIdAndUserIdWithOldStatus(fileInfo, fileId, userId, FileStatusEnum.TRANSFER.getStatus());
        }
    }

    /**
     * 切割视频
     *
     * @param fileId        文件 ID
     * @param videoFilePath 目标文件路径
     */
    private void cutFile4Video(String fileId, String videoFilePath) {
        // 创建ts目录
        final File tsFolder = new File(videoFilePath.substring(0, videoFilePath.lastIndexOf(".")));
        if (!tsFolder.exists()) {
            tsFolder.mkdirs();
        }

        // 将 MP4 格式的视频文件转换为 Annex B 格式，同时保持视频和音频的原始质量，因为它们都是通过 copy 进行处理的
        final StringBuilder CMD_TRANSFER_2TS = new StringBuilder("ffmpeg ")
                // -y: 表示在输出文件存在的情况下自动覆盖，无需提示。即使命令推荐会覆盖文件信息，仍会强制执行。
                .append("-y ")
                // -i: 输入文件的参数，通常在实际使用中会被替换为一个具体的文件路径（比如输入的视频文件）。-i 是用来指定输入文件的选项。
                .append("-i %s ")
                // -acodec copy: 音频编解码器参数，和 -vcodec copy 类似，copy 表示直接复制输入文件中的音频流。这同样可以提高处理速度并保持音频质量。
                .append("-acodec copy ")
                // -vcodec copy: 视频编解码器参数，copy 表示直接复制输入文件中的视频流，而不进行解码和重新编码。这使得处理速度更快，也不会损失视频质量。
                .append("-vcodec copy ")
                // -c:v libx265: 指定使用 libx265 编码器将视频编码为 HEVC（H.265）格式。这意味着视频流将会被重新编码，而不是像 -vcodec copy 一样进行简单的复制
                // .append("-c:v libx265 ")
                // -bsf:v hevc_mp4toannexb: 这是一个视频比特流过滤器（Video BitStream Filter），hevc_mp4toannexb 是指将 HEVC（H.265）视频流从 MP4 格式转换为 Annex B 格式。这一步骤是为了确保输出文件能在支持 Annex B 的播放环境中正确播放。
                // .append("-bsf:v hevc_mp4toannexb ")
                .append("-bsf:v h264_mp4toannexb ")
                // %s: 输出文件的参数，在具体的使用中会被替换为实际文件的路径（比如要生成的视频文件）。
                .append("%s");

        // 将一个视频文件分割成多个 30 秒的 TS 格式片段，同时生成一个包含这些片段文件列表的索引文件
        final StringBuilder CMD_CUT_TS = new StringBuilder("ffmpeg ")
                // -i：设置输入文件参数，通常会在实际使用中替换成一个具体的文件路径（即输入要处理的视频文件）。
                .append("-i %s ")
                // -c copy: 选用编解码器，这里 copy 表示直接复制输入文件中的音频和视频流，而不对它们进行重新编码。这可以加快处理速度，并保持原始质量。
                .append("-c copy ")
                // -map 0: 这个选项用于指定要包括哪些流。0 表示选择输入文件的所有流（视频、音频、字幕等）。
                .append("-map 0 ")
                // -f segment: 设置输出格式为 segment（段），表示要对输入文件进行分段处理。使用此格式时，FFmpeg 将分割输入文件，生成多个输出片段。
                .append("-f segment ")
                // -segment_list: 这个选项用于指定一个文件，用于存储分段的列表或索引。这个文件将列出所有生成的段文件（例如文件名或路径）。
                .append("-segment_list %s ")
                // -segment_time 30: 这是一个时间参数，用于指定每个输出段的时长（以秒为单位）。在这里，30 表示每个输出片段将时长设定为 30 秒。
                .append("-segment_time 30 ")
                // 这是输出文件的名称和路径，其中的 %s 在实际使用时将被替换为输出目录和根文件名，%%4d 是一个格式说明符，表示将生成的文件名中包含四位数字，这四位数字会根据文件的顺序自动填充。例如，如果根文件名为 output，则生成的文件可能是这样的：
                // output_0001.ts
                // output_0002.ts
                // output_0003.ts
                // 依此类推。
                .append("%s/%s_%%4d.ts");

        // index.ts 目录
        final String tsPath = tsFolder.getPath() + File.separator + VideoConstants.TS_NAME;
        // index.m3u8 目录
        final String m3u8Path = tsFolder.getPath() + File.separator + VideoConstants.M3U8_NAME;

        // 生成 .ts
        final String cmd1 = String.format(CMD_TRANSFER_2TS.toString(), videoFilePath, tsPath);
        ProcessUtils.executeCommand(cmd1, true);

        // 生成索引文件 .m3u8 和 切片 .ts
        final String cmd2 = String.format(CMD_CUT_TS.toString(), tsPath, m3u8Path, tsFolder.getPath(), fileId);
        ProcessUtils.executeCommand(cmd2, true);

        // 删除 index.ts 文件
        new File(tsPath).delete();
    }

    /**
     * 合并文件
     *
     * @param sourcePath   源路径
     * @param targetPath   目标路径
     * @param deleteSource 是否删除源路径文件
     */
    private void mergeFile(String sourcePath, String targetPath, boolean deleteSource) {
        final File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            final String msg = MessageFormat.format("合并文件失败：源文件目录 {0} 不存在", sourceFile.getPath());
            log.error(msg);
            throw new BusinessException(msg);
        }

        try {
            int len;
            final byte[] buffer = new byte[1024 * 10];
            try (RandomAccessFile writeFile = new RandomAccessFile(targetPath, "rw")) {
                for (File file : Objects.requireNonNull(sourceFile.listFiles())) {
                    try (RandomAccessFile readFile = new RandomAccessFile(file, "r")) {
                        while ((len = readFile.read(buffer)) != -1) {
                            writeFile.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            final String msg = MessageFormat.format("合并文件失败：{0}", e.getMessage());
            log.error(msg, e);
            throw new BusinessException(msg);
        }
        finally {
            if (deleteSource) {
                FileUtils.deleteDirectory(sourceFile.getPath());
            }
        }
    }

    /**
     * 自动重命名
     *
     * @param userId   用户 ID
     * @param filePid  文件 PID
     * @param fileName 文件名
     * @return {@link String }
     */
    private String autoRename(String userId, String filePid, String fileName) {
        final Integer count = this.fileInfoMapper.selectCount(FileInfoQuery
                .builder()
                .userId(userId)
                .filePid(filePid)
                .fileName(fileName)
                .delFlag(FileDelFlagEnum.USING.getFlag())
                .build());
        if (count <= 0) {
            return fileName;
        }
        return FileUtils.rename(fileName);
    }

    /**
     * 更新用户空间
     *
     * @param userId       用户ID
     * @param userSpaceDto 用户空间 DTO
     * @param fileSize     文件大小
     */
    private void updateUserSpace(String userId, UserSpaceDto userSpaceDto, Long fileSize) {
        // 更新用户已使用空间至用户信息表
        final Integer count = this.userInfoMapper.updateUserSpace(userId, fileSize, null);
        if (count == 0) {
            throw new BusinessException(ResponseCodeEnum.CODE_904);
        }

        // 更新用户已使用空间至Redis缓存
        userSpaceDto.setUseSpace(userSpaceDto.getUseSpace() + fileSize);
        redisComponent.saveUserSpaceUse(userId, userSpaceDto);
    }

    /**
     * 获取临时文件夹路径
     *
     * @param fileId 文件 ID
     * @param userId 用户 ID
     * @return {@link File }
     */
    private File getTempFileFolderPath(String fileId, String userId) {
        final String tempFolderPath = appConfig.getProjectFolder() + FileFolderConstants.FILE_FOLDER_TEMP;
        return new File(tempFolderPath, getCurrentUserFolderName(fileId, userId));
    }

    /**
     * 获取真实文件夹路径
     *
     * @param month 月份
     * @return {@link File }
     */
    private File getRealFileFolderPath(String month) {
        final String fileFolderPath = appConfig.getProjectFolder() + FileFolderConstants.FILE_FOLDER_FILE;
        return new File(fileFolderPath, month);
    }

    /**
     * 获取当前用户文件夹名称
     *
     * @param fileId 文件 ID
     * @param userId 用户 ID
     * @return {@link String }
     */
    private String getCurrentUserFolderName(String fileId, String userId) {
        return userId + "_" + fileId;
    }

}
