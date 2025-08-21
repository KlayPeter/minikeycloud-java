package com.easypan.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author VectorX
 * @version 1.0.0
 * @description
 * @date 2024/07/23
 */
@Slf4j
public class JsonUtils
{

    public static String convertJson2Json(Object obj) {
        return JSON.toJSONString(obj);
    }

    public static <T> T convertJson2Obj(String json, Class<T> classz) {
        return JSONObject.parseObject(json, classz);
    }

    public static <T> List<T> convertJsonArray2List(String json, Class<T> tClass) {
        return JSONArray.parseArray(json, tClass);
    }

}
