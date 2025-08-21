package com.easypan.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author VectorX
 * @version V1.0
 * @description 编码实用程序
 * @date 2024-07-28 15:19:44
 */
public class EncodeUtils
{
    public static String encodeByMD5(String originStr) {
        return StringUtils.isEmpty(originStr) ?
               null :
               DigestUtils.md5Hex(originStr);
    }
}
