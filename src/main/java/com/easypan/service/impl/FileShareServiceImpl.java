package com.easypan.service.impl;

import com.easypan.entity.constants.Constants;
import com.easypan.entity.constants.VerificationCodeConstants;
import com.easypan.entity.dto.SessionShareDto;
import com.easypan.entity.enums.PageSize;
import com.easypan.entity.enums.ResponseCodeEnum;
import com.easypan.entity.enums.ShareValidTypeEnums;
import com.easypan.entity.po.FileShare;
import com.easypan.entity.query.FileShareQuery;
import com.easypan.entity.query.SimplePage;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.exception.BusinessException;
import com.easypan.mappers.FileShareMapper;
import com.easypan.service.FileShareService;
import com.easypan.utils.BeanCopyUtils;
import com.easypan.utils.DateUtil;
import com.easypan.utils.RandomUtils;
import com.easypan.utils.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 文件分享信息 业务接口实现
 */
@Service("fileShareService")
public class FileShareServiceImpl implements FileShareService
{

    @Resource
    private FileShareMapper<FileShare, FileShareQuery> fileShareMapper;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<FileShare> findListByParam(FileShareQuery param) {
        return this.fileShareMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(FileShareQuery param) {
        return this.fileShareMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<FileShare> findListByPage(FileShareQuery param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ?
                       PageSize.SIZE15.getSize() :
                       param.getPageSize();

        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<FileShare> list = this.findListByParam(param);
        return (PaginationResultVO<FileShare>) new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
    }

    /**
     * 新增
     */
    @Override
    public Integer add(FileShare bean) {
        return this.fileShareMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<FileShare> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.fileShareMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(List<FileShare> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.fileShareMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(FileShare bean, FileShareQuery param) {
        StringUtils.checkParam(param);
        return this.fileShareMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(FileShareQuery param) {
        StringUtils.checkParam(param);
        return this.fileShareMapper.deleteByParam(param);
    }

    /**
     * 根据ShareId获取对象
     */
    @Override
    public FileShare getFileShareByShareId(String shareId) {
        return this.fileShareMapper.selectByShareId(shareId);
    }

    /**
     * 根据ShareId修改
     */
    @Override
    public Integer updateFileShareByShareId(FileShare bean, String shareId) {
        return this.fileShareMapper.updateByShareId(bean, shareId);
    }

    /**
     * 根据ShareId删除
     */
    @Override
    public Integer deleteFileShareByShareId(String shareId) {
        return this.fileShareMapper.deleteByShareId(shareId);
    }

    @Override
    public void saveShare(FileShare fileShare) {
        final ShareValidTypeEnums type = ShareValidTypeEnums.getByType(fileShare.getValidType());
        if (Objects.isNull(type)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (!ShareValidTypeEnums.FOREVER
                .getType()
                .equals(type.getType())) {
            fileShare.setExpireTime(DateUtil.getAfterDay(type.getDays()));
        }
        fileShare.setShareId(RandomUtils.getRandomNumber(Constants.LENGTH_20));
        fileShare.setCode(RandomUtils.getRandomString(VerificationCodeConstants.LENGTH_CODE_5));
        fileShare.setShowCount(0);
        fileShare.setShareTime(new Date());
        this.fileShareMapper.insert(fileShare);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFileShareBatch(List<String> shareIds, String userId) {
        final Integer count = this.fileShareMapper.deleteFileShareBatch(shareIds, userId);
        if (count != shareIds.size()) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }

    @Override
    public SessionShareDto checkShareCode(String shareId, String code) {
        // 1、根据 shareId 获取分享信息
        FileShare fileShare = this.fileShareMapper.selectByShareId(shareId);
        if (Objects.isNull(fileShare) || DateUtil.isExpire(fileShare.getExpireTime())) {
            throw new BusinessException(ResponseCodeEnum.CODE_902);
        }

        // 2、校验分享码
        if (!fileShare
                .getCode()
                .equals(code)) {
            throw new BusinessException("提取码错误");
        }

        // 3、更新分享次数
        fileShareMapper.updateShareShowCount(shareId);

        // 4、返回分享信息
        final SessionShareDto sessionShareDto = BeanCopyUtils.copy(fileShare, SessionShareDto.class);
        Objects.requireNonNull(sessionShareDto);
        sessionShareDto.setShareUserId(fileShare.getUserId());
        return sessionShareDto;
    }
}
