package com.easypan.controller;

import com.easypan.entity.constants.SessionConstants;
import com.easypan.entity.dto.SessionShareDto;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.enums.ResponseCodeEnum;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.exception.BusinessException;
import com.easypan.utils.BeanCopyUtils;
import com.easypan.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
public class ABaseController
{
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ABaseController.class);

    protected static final String STATUC_SUCCESS = "success";

    protected static final String STATUC_ERROR = "error";

    protected <T> ResponseVO<T> getSuccessResponseVO(T t) {
        ResponseVO<T> responseVO = new ResponseVO<>();
        responseVO.setStatus(STATUC_SUCCESS);
        responseVO.setCode(ResponseCodeEnum.CODE_200.getCode());
        responseVO.setInfo(ResponseCodeEnum.CODE_200.getMsg());
        responseVO.setData(t);
        return responseVO;
    }

    protected <T> ResponseVO<T> getBusinessErrorResponseVO(BusinessException e, T t) {
        ResponseVO<T> vo = new ResponseVO<>();
        vo.setStatus(STATUC_ERROR);
        if (e.getCode() == null) {
            vo.setCode(ResponseCodeEnum.CODE_600.getCode());
        }
        else {
            vo.setCode(e.getCode());
        }
        vo.setInfo(e.getMessage());
        vo.setData(t);
        return vo;
    }

    protected <T> ResponseVO<T> getServerErrorResponseVO(T t) {
        ResponseVO<T> vo = new ResponseVO<>();
        vo.setStatus(STATUC_ERROR);
        vo.setCode(ResponseCodeEnum.CODE_500.getCode());
        vo.setInfo(ResponseCodeEnum.CODE_500.getMsg());
        vo.setData(t);
        return vo;
    }

    protected <S, T> PaginationResultVO<T> convert2PaginationVO(PaginationResultVO<S> result, Class<T> clazz) {
        // 浅拷贝
        final PaginationResultVO<T> resultVO = BeanCopyUtils.copy(result, PaginationResultVO.class);
        // 深拷贝
        resultVO.setList(BeanCopyUtils.copyList(result.getList(), clazz));
        return resultVO;
    }

    protected void readFile(HttpServletResponse response, String filePath) {
        log.info("开始读取文件: {}", filePath);
        
        if (!FileUtils.pathIsOk(filePath)) {
            log.error("文件路径不合法: {}", filePath);
            return;
        }
        log.info("文件路径验证通过: {}", filePath);

        File file = new File(filePath);
        if (!file.exists()) {
            log.error("文件不存在: {}", filePath);
            return;
        }
        log.info("文件存在，大小: {} bytes", file.length());

        try (FileInputStream fis = new FileInputStream(filePath);
             OutputStream os = response.getOutputStream();) {
            int len;
            final byte[] byteData = new byte[1024];
            long totalBytes = 0;
            while ((len = fis.read(byteData)) != -1) {
                os.write(byteData, 0, len);
                totalBytes += len;
            }
            os.flush();
            log.info("文件读取完成，总共读取: {} bytes", totalBytes);
        }
        catch (IOException e) {
            log.error("读取文件失败: {}", e.getMessage(), e);
        }
    }

    protected SessionWebUserDto getUserInfoFromSession(HttpSession session) {
        return (SessionWebUserDto) session.getAttribute(SessionConstants.SESSION_KEY);
    }

    protected SessionShareDto getShareInfoFromSession(HttpSession session, String shareId) {
        return (SessionShareDto) session.getAttribute(SessionConstants.SESSION_SHARE_KEY.concat(shareId));
    }

    protected void setShareInfo2Session(HttpSession session, String shareId, SessionShareDto shareSessionDto) {
        session.setAttribute(SessionConstants.SESSION_SHARE_KEY.concat(shareId), shareSessionDto);
    }
}
