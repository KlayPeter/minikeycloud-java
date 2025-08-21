package com.easypan.controller;
import com.easypan.entity.enums.ResponseCodeEnum;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.exception.BusinessException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class AGlobalExceptionHandlerController extends ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(AGlobalExceptionHandlerController.class);

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ResponseVO> handleException(Exception e, HttpServletRequest request) {
        logger.error("请求错误，请求地址{},错误信息:", request.getRequestURL(), e);
        ResponseVO ajaxResponse = new ResponseVO();
        HttpStatus httpStatus;
        
        //404
        if (e instanceof NoHandlerFoundException) {
            ajaxResponse.setCode(ResponseCodeEnum.CODE_404.getCode());
            ajaxResponse.setInfo(ResponseCodeEnum.CODE_404.getMsg());
            ajaxResponse.setStatus(STATUC_ERROR);
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (e instanceof BusinessException) {
            //业务错误
            BusinessException biz = (BusinessException) e;
            Integer code = biz.getCode() == null ? ResponseCodeEnum.CODE_600.getCode() : biz.getCode();
            ajaxResponse.setCode(code);
            ajaxResponse.setInfo(biz.getMessage());
            ajaxResponse.setStatus(STATUC_ERROR);
            
            // 根据业务错误代码设置HTTP状态码
            if (code.equals(ResponseCodeEnum.CODE_901.getCode())) {
                httpStatus = HttpStatus.UNAUTHORIZED; // 401 未授权
            } else if (code.equals(ResponseCodeEnum.CODE_404.getCode())) {
                httpStatus = HttpStatus.NOT_FOUND; // 404 未找到
            } else if (code.equals(ResponseCodeEnum.CODE_600.getCode()) || code.equals(ResponseCodeEnum.CODE_601.getCode())) {
                httpStatus = HttpStatus.BAD_REQUEST; // 400 请求错误
            } else {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR; // 500 服务器错误
            }
        } else if (e instanceof BindException|| e instanceof MethodArgumentTypeMismatchException) {
            //参数类型错误
            ajaxResponse.setCode(ResponseCodeEnum.CODE_600.getCode());
            ajaxResponse.setInfo(ResponseCodeEnum.CODE_600.getMsg());
            ajaxResponse.setStatus(STATUC_ERROR);
            httpStatus = HttpStatus.BAD_REQUEST; // 400 请求错误
        } else if (e instanceof DuplicateKeyException) {
            //主键冲突
            ajaxResponse.setCode(ResponseCodeEnum.CODE_601.getCode());
            ajaxResponse.setInfo(ResponseCodeEnum.CODE_601.getMsg());
            ajaxResponse.setStatus(STATUC_ERROR);
            httpStatus = HttpStatus.CONFLICT; // 409 冲突
        } else {
            ajaxResponse.setCode(ResponseCodeEnum.CODE_500.getCode());
            ajaxResponse.setInfo(ResponseCodeEnum.CODE_500.getMsg());
            ajaxResponse.setStatus(STATUC_ERROR);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR; // 500 服务器错误
        }
        
        return new ResponseEntity<>(ajaxResponse, httpStatus);
    }
}
