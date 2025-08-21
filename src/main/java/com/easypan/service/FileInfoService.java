package com.easypan.service;

import com.easypan.entity.dto.DownloadFileDto;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.dto.UploadFileDto;
import com.easypan.entity.po.FileInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.vo.FileInfoVo;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.vo.UploadResultVo;

import java.util.List;

/**
 * 文件信息表 业务接口
 */
public interface FileInfoService
{

	/**
	 * 根据条件查询列表
	 */
	List<FileInfo> findListByParam(FileInfoQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(FileInfoQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<FileInfo> findListByPage(FileInfoQuery param);

	/**
	 * 新增
	 */
	Integer add(FileInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<FileInfo> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<FileInfo> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(FileInfo bean, FileInfoQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(FileInfoQuery param);

	/**
	 * 根据FileIdAndUserId查询对象
	 */
	FileInfo getFileInfoByFileIdAndUserId(String fileId, String userId);

	/**
	 * 根据FileIdAndUserId修改
	 */
	Integer updateFileInfoByFileIdAndUserId(FileInfo bean, String fileId, String userId);

	/**
	 * 根据FileIdAndUserId删除
	 */
	Integer deleteFileInfoByFileIdAndUserId(String fileId, String userId);

	/**
	 * 上传文件
	 *
	 * @param userDto       用户 dto
	 * @param uploadFileDto 上传文件 dto
	 */
	UploadResultVo uploadFile(SessionWebUserDto userDto, UploadFileDto uploadFileDto);

	/**
	 * 新建文件夹
	 *
	 * @param userId   用户 ID
	 * @param filePid  文件 PID
	 * @param fileName 文件名
	 * @return {@link FileInfoVo }
	 */
	FileInfoVo newFolder(String userId, String filePid, String fileName);

	/**
	 * 重命名
	 *
	 * @param userId   用户 ID
	 * @param fileId   文件 ID
	 * @param fileName 文件名
	 * @return {@link FileInfoVo }
	 */
	FileInfoVo rename(String userId, String fileId, String fileName);

	/**
	 * 加载所有文件夹
	 *
	 * @param userId         用户 ID
	 * @param filePid        文件 PID
	 * @param currentFileIds 当前文件 ID
	 * @return {@link List }<{@link FileInfoVo }>
	 */
	List<FileInfoVo> loadAllFolder(String userId, String filePid, String currentFileIds);

	/**
	 * 更改文件夹
	 *
	 * @param userId  用户 ID
	 * @param fileIds 文件 ID
	 * @param filePid 文件 PID
	 */
	void changeFileFolder(String userId, String fileIds, String filePid);

	/**
	 * 创建下载链接
	 *
	 * @param userId 用户 ID
	 * @param fileId 文件 ID
	 * @return
	 */
	DownloadFileDto createDownloadUrl(String userId, String fileId);

	/**
	 * 删除文件
	 *
	 * @param userId  用户 ID
	 * @param fileIds 文件 ID
	 */
	void delFile(String userId, String fileIds);

	/**
	 * 恢复文件
	 *
	 * @param userId  用户 ID
	 * @param fileIds 文件 ID
	 */
	void recoveryFileBatch(String userId, String fileIds);

	/**
	 * 删除文件
	 *
	 * @param userId  用户 ID
	 * @param fileIds 文件 ID
	 * @param adminOp
	 */
	void delFileBatch(String userId, String fileIds, boolean adminOp);

	/**
	 * 检查根文件 PID
	 *
	 * @param rootFilePid 根文件 PID
	 * @param userId      用户 ID
	 * @param fileId      文件 ID
	 */
	void checkRootFilePid(String rootFilePid, String userId, String fileId);

	/**
	 * 保存分享
	 *
	 * @param fileId        文件 ID
	 * @param shareFileIds  分享文件 ID's
	 * @param myFolderId    我的文件夹 ID
	 * @param shareUserId   分享用户 ID
	 * @param currentUserId 用户 ID
	 */
	void saveShare(String fileId, String shareFileIds, String myFolderId, String shareUserId, String currentUserId);
}
