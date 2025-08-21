package com.easypan.utils;

import com.easypan.entity.enums.ResponseCodeEnum;
import com.easypan.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-07-21 13:22:16
 */
@Slf4j
public class HttpUtils
{
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HttpUtils.class);
    private static final int TIMEOUT_SECONDS = 10000;

    private static OkHttpClient.Builder getClientBuilder() {
        return new OkHttpClient.Builder()
                .followRedirects(false)
                .retryOnConnectionFailure(false)
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    private static Request.Builder getRequestBuilder(Map<String, String> header) {
        final Request.Builder requestBuilder = new Request.Builder();
        if (header != null) {
            header.forEach((k, v) -> requestBuilder.addHeader(k, v == null ?
                                                                 "" :
                                                                 v));
        }
        return requestBuilder;
    }

    public static String sendRequest(String url) {
        try {
            final Request request = getRequestBuilder(null)
                    .url(url)
                    .build();
            final Response response = getClientBuilder()
                    .build()
                    .newCall(request)
                    .execute();
            try (ResponseBody responseBody = response.body()) {
                final String responseStr = responseBody.string();
                log.info("请求地址url: {}, 返回信息response:{}", responseStr);
                return responseStr;
            }
        }
        catch (SocketTimeoutException | ConnectException e) {
            log.error("请求超时：{}", e.getMessage(), e);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        }
        catch (IOException e) {
            log.error("请求异常：{}", e.getMessage(), e);
            return null;
        }
    }
}
