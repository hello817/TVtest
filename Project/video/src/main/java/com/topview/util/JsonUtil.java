package com.topview.util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * JSON 工具类，用于请求体解析和响应输出
 */
public class JsonUtil {

    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")  // 统一日期格式
            .create();

    /**
     * 将对象转换为 JSON 字符串
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * 从 Reader 中读取 JSON 并转换为指定类型的对象
     * 用于解析请求体
     */
    public static <T> T fromJson(BufferedReader reader, Class<T> clazz) {
        return gson.fromJson(reader, clazz);
    }

    /**
     * 从字符串解析 JSON
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    /**
     * 向客户端输出 JSON 响应
     * @param response HttpServletResponse 对象
     * @param obj 要输出的对象（可以是 Result 或普通 POJO）
     */
    public static void writeJson(HttpServletResponse response, Object obj) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(toJson(obj));
        writer.flush();
    }

    /**
     * 读取请求体中的 JSON 字符串（备用方法）
     */
    public static String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
