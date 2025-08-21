package com.easypan.aspect;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.entity.constants.SessionConstants;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.enums.ResponseCodeEnum;
import com.easypan.exception.BusinessException;
import com.easypan.utils.StringUtils;
import com.easypan.utils.VerifyUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author VectorX
 * @version V1.0
 * @description 自定义切面，用于全局操作
 * @date 2024-07-19 22:07:11
 */
// 声明切面（切入点和通知的结合。告诉Spring的AOP：要对哪个方法，做什么样的增强）
@Aspect
// 要让 Spring 容器扫描到
@Component("globalOperationAspect")
@Slf4j
public class GlobalOperationAspect
{
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalOperationAspect.class);

    private static final String TYPE_STRING = String.class.getName();
    private static final String TYPE_INTEGER = Integer.class.getName();
    private static final String TYPE_LONG = Long.class.getName();

    // 声明切入点（要对哪些连接点进行拦截的定义，即要增强的方法）
    @Pointcut("@annotation(com.easypan.annotation.GlobalInterceptor)")
    private void requestInterceptor() {

    }

    // 前置通知（拦截到连接点之后要做的事情。如何增强，额外添加上去的功能和能力）
    @Before("requestInterceptor()")
    public void interceptorDo(
            // 连接点（能够被拦截到的点，在Spring里指的是方法。能增强的方法）
            JoinPoint joinPoint) {
        // 获取方法名（移到try块外面，这样catch块也能访问）
        final String methodName = joinPoint
                .getSignature()
                .getName();
        try {
            final Object target = joinPoint.getTarget();
            // 获取参数值列表
            final Object[] args = joinPoint.getArgs();
            // 获取参数类型列表
            final Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature())
                    .getMethod()
                    .getParameterTypes();
            // 获取方法
            final Method method = target
                    .getClass()
                    .getMethod(methodName, parameterTypes);
            // 获取方法注解
            final GlobalInterceptor interceptor = method.getAnnotation(GlobalInterceptor.class);
            if (interceptor == null) {
                return;
            }

            /** 校验登录 **/
            if (interceptor.checkLogin() || interceptor.checkAdmin()) {
                checkLogin(interceptor.checkAdmin());
            }

            /** 校验参数 **/
            if (interceptor.checkParams()) {
                validateParams(method, args);
            }
        }
        catch (BusinessException e) {
            log.error("全局拦截器业务异常 - 方法: {}, 异常信息: {}", methodName, e.getMessage(), e);
            throw e;
        }
        catch (Exception e) {
            log.error("全局拦截器系统异常 - 方法: {}, 异常类型: {}, 异常信息: {}, 堆栈信息: ", methodName, e.getClass().getSimpleName(), e.getMessage(), e);
            // 不要统一转换为500错误，让原始异常传递到全局异常处理器
            throw new RuntimeException(e);
        }

    }

    /**
     * 校验登录
     *
     * @param checkAdmin 校验管理员
     */
    private void checkLogin(Boolean checkAdmin) {
        // 获取当前请求的ServletRequestAttributes
        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // 获取当前请求的HttpServletRequest
        final HttpServletRequest request = requestAttributes.getRequest();
        // 获取当前请求的HttpSession
        final HttpSession session = request.getSession();
        // 从session中获取SessionWebUserDto对象
        final SessionWebUserDto userDto = (SessionWebUserDto) session.getAttribute(SessionConstants.SESSION_KEY);
        // 如果session中没有SessionWebUserDto对象，则抛出业务异常
        if (userDto == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }

        // 如果需要检查是否为管理员，并且当前用户不是管理员，则抛出业务异常
        if (checkAdmin && !userDto.getIsAdmin()) {
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
    }

    /**
     * 验证参数
     *
     * @param method 方法
     * @param args   参数
     */
    private void validateParams(Method method, Object[] args) {
        // 获取参数名列表
        final Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            // 获取参数名
            final Parameter parameter = parameters[i];
            // 获取参数值
            final Object value = args[i];
            // 获取参数注解
            final VerifyParam verifyParam = parameter.getAnnotation(VerifyParam.class);
            if (verifyParam == null) {
                continue;
            }

            // 基本数据类型
            final String typeName = parameter
                    .getParameterizedType()
                    .getTypeName();
            if (TYPE_STRING.equals(typeName) || TYPE_INTEGER.equals(typeName) || TYPE_LONG.equals(typeName)) {
                checkBaseDataTypeValue(value, verifyParam);
            }
            else {
                checkReferenceTypeValue(value, parameter);
            }
        }
    }

    /**
     * 校验基本数据类型值
     *
     * @param value       值
     * @param verifyParam 验证参数
     */
    private void checkBaseDataTypeValue(Object value, VerifyParam verifyParam) {
        if (!verifyParam.required()) {
            return;
        }

        // 校验空
        if (value == null || StringUtils.isEmpty(value.toString())) {
            log.error("参数不能为空");
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        // 长度校验
        final int length = value
                .toString()
                .length();
        if ((verifyParam.minLength() != -1 && verifyParam.minLength() > length) || (verifyParam.maxLength() != -1 && verifyParam.maxLength() < length)) {
            log.error("参数长度不符合要求：{}", length);
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        // 具体规则校验——正则表达式
        if (!StringUtils.isEmpty(verifyParam
                .regex()
                .getRegex()) && !VerifyUtils.verify(verifyParam.regex(), String.valueOf(value))) {
            log.error("参数不满足正则表达式");
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }

    /**
     * 检查引用类型
     *
     * @param value     值
     * @param parameter 参数
     */
    private void checkReferenceTypeValue(Object value, Parameter parameter) {
        try {
            // 获取参数类名
            final String typeName = parameter
                    .getParameterizedType()
                    .getTypeName();
            // 获取参数类
            final Class<?> aClass = Class.forName(typeName);
            // 获取参数类的属性
            final Field[] fields = aClass.getFields();
            for (Field field : fields) {
                final VerifyParam verifyParam = field.getAnnotation(VerifyParam.class);
                if (verifyParam == null) {
                    continue;
                }
                field.setAccessible(true);
                // 获取属性值
                final Object resultValue = field.get(value);
                // 校验基本数据类型值
                checkBaseDataTypeValue(resultValue, verifyParam);
            }
        }
        catch (Exception e) {
            log.error("校验参数失败：{}", e.getMessage(), e);
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }

}
