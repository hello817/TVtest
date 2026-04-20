package com.topview.client;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * 纯控制台交互客户端，用于测试 StoneVideo 后端 API
 * 使用 java.net.HttpURLConnection 发送 HTTP 请求
 */
public class ConsoleClient {

    private static final String BASE_URL = "http://localhost:8802/StoneVideo/api"; // 根据你的部署修改
    private static final Gson gson = new Gson();
    private static String authToken = null;      // 登录后保存 Token
    private static Long currentUserId = null;    // 当前登录用户 ID（可选）
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  欢迎来到赛博搬石大王 - 控制台客户端");
        System.out.println("========================================");

        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": register(); break;
                case "2": login(); break;
                case "3": listVideos(); break;
                case "4": viewVideo(); break;
                case "5": uploadVideo(); break;
                case "6": postComment(); break;
                case "7": viewComments(); break;
                case "8": deleteComment(); break;
                case "9": logout(); break;
                case "0": exit(); return;
                default: System.out.println("无效选项，请重新输入");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n========== 主菜单 ==========");
        if (authToken == null) {
            System.out.println("[未登录] 请先登录或注册");
            System.out.println("1. 注册");
            System.out.println("2. 登录");
        } else {
            System.out.println("[已登录] Token: " + authToken.substring(0, 8) + "...");
            System.out.println("3. 查看视频列表");
            System.out.println("4. 观看单个视频");
            System.out.println("5. 上传视频（需登录）");
            System.out.println("6. 发表评论");
            System.out.println("7. 查看视频评论");
            System.out.println("8. 删除我的评论");
            System.out.println("9. 退出登录");
        }
        System.out.println("0. 退出程序");
        System.out.print("请输入选项: ");
    }

    // ---------- 注册 ----------
    private static void register() {
        System.out.print("用户名: ");
        String username = scanner.nextLine();
        System.out.print("密码: ");
        String password = scanner.nextLine();
        System.out.print("昵称: ");
        String nickname = scanner.nextLine();

        JsonObject json = new JsonObject();
        json.addProperty("username", username);
        json.addProperty("passwordHash", password);   // 注意后端字段名
        json.addProperty("nickname", nickname);

        try {
            String response = sendRequest("/user/register", "POST", json.toString(), false);
            System.out.println("注册结果: " + response);
        } catch (Exception e) {
            System.out.println("注册失败: " + e.getMessage());
        }
    }

    // ---------- 登录 ----------
    private static void login() {
        System.out.print("用户名: ");
        String username = scanner.nextLine();
        System.out.print("密码: ");
        String password = scanner.nextLine();

        JsonObject json = new JsonObject();
        json.addProperty("username", username);
        json.addProperty("passwordHash", password);

        try {
            String response = sendRequest("/user/login", "POST", json.toString(), false);
            System.out.println("登录响应: " + response);
            // 解析 Token (假设返回格式: {"code":200,"msg":"success","data":{"token":"xxx"}})
            JsonObject respJson = gson.fromJson(response, JsonObject.class);
            if (respJson.get("code").getAsInt() == 200) {
                JsonObject data = respJson.getAsJsonObject("data");
                authToken = data.get("token").getAsString();
                System.out.println("登录成功！Token 已保存。");
            } else {
                System.out.println("登录失败: " + respJson.get("msg").getAsString());
            }
        } catch (Exception e) {
            System.out.println("登录异常: " + e.getMessage());
        }
    }

    // ---------- 退出登录 ----------
    private static void logout() {
        authToken = null;
        currentUserId = null;
        System.out.println("已退出登录");
    }

    // ---------- 查看视频列表 ----------
    private static void listVideos() {
        System.out.print("页码 (默认1): ");
        String pageStr = scanner.nextLine();
        int page = pageStr.isEmpty() ? 1 : Integer.parseInt(pageStr);
        System.out.print("每页数量 (默认10): ");
        String sizeStr = scanner.nextLine();
        int size = sizeStr.isEmpty() ? 10 : Integer.parseInt(sizeStr);

        try {
            String response = sendRequest("/video/list?page=" + page + "&size=" + size, "GET", null, true);
            System.out.println("视频列表: " + response);
        } catch (Exception e) {
            System.out.println("获取列表失败: " + e.getMessage());
        }
    }

    // ---------- 观看单个视频 ----------
    private static void viewVideo() {
        System.out.print("请输入视频ID: ");
        String id = scanner.nextLine();

        try {
            String response = sendRequest("/video/" + id, "GET", null, true);
            System.out.println("视频详情: " + response);
        } catch (Exception e) {
            System.out.println("获取视频失败: " + e.getMessage());
        }
    }

    // ---------- 上传视频 ----------
    private static void uploadVideo() {
        if (authToken == null) {
            System.out.println("请先登录！");
            return;
        }
        System.out.print("视频标题: ");
        String title = scanner.nextLine();
        System.out.print("视频描述: ");
        String desc = scanner.nextLine();
        System.out.print("视频链接 (URL): ");
        String url = scanner.nextLine();
        System.out.print("封面链接 (可选): ");
        String cover = scanner.nextLine();

        JsonObject json = new JsonObject();
        json.addProperty("title", title);
        json.addProperty("description", desc);
        json.addProperty("url", url);
        if (!cover.isEmpty()) {
            json.addProperty("coverUrl", cover);
        }

        try {
            String response = sendRequest("/video/upload", "POST", json.toString(), true);
            System.out.println("上传结果: " + response);
        } catch (Exception e) {
            System.out.println("上传失败: " + e.getMessage());
        }
    }

    // ---------- 发表评论 ----------
    private static void postComment() {
        if (authToken == null) {
            System.out.println("请先登录！");
            return;
        }
        System.out.print("视频ID: ");
        String videoId = scanner.nextLine();
        System.out.print("评论内容: ");
        String content = scanner.nextLine();

        JsonObject json = new JsonObject();
        json.addProperty("videoId", Long.parseLong(videoId));
        json.addProperty("content", content);
        // parentId 默认为 null

        try {
            String response = sendRequest("/comment/add", "POST", json.toString(), true);
            System.out.println("评论结果: " + response);
        } catch (Exception e) {
            System.out.println("评论失败: " + e.getMessage());
        }
    }

    // ---------- 查看视频评论 ----------
    private static void viewComments() {
        System.out.print("视频ID: ");
        String videoId = scanner.nextLine();

        try {
            String response = sendRequest("/comment/video/" + videoId, "GET", null, true);
            System.out.println("评论列表: " + response);
        } catch (Exception e) {
            System.out.println("获取评论失败: " + e.getMessage());
        }
    }

    // ---------- 删除评论 ----------
    private static void deleteComment() {
        if (authToken == null) {
            System.out.println("请先登录！");
            return;
        }
        System.out.print("要删除的评论ID: ");
        String commentId = scanner.nextLine();

        try {
            String response = sendRequest("/comment/delete/" + commentId, "DELETE", null, true);
            System.out.println("删除结果: " + response);
        } catch (Exception e) {
            System.out.println("删除失败: " + e.getMessage());
        }
    }

    // ---------- 退出程序 ----------
    private static void exit() {
        System.out.println("感谢使用赛博搬石大王，再见！");
        scanner.close();
    }

    // ---------- 通用 HTTP 请求发送 ----------
    private static String sendRequest(String path, String method, String jsonBody, boolean needAuth) throws IOException {
        URL url = new URL(BASE_URL + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        if (needAuth && authToken != null) {
            conn.setRequestProperty("Authorization", authToken);
        }
        if (jsonBody != null) {
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }

        int code = conn.getResponseCode();
        InputStream stream = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
        String response = readStream(stream);

        // 处理可能返回的新 Token
        String newToken = conn.getHeaderField("New-Token");
        if (newToken != null && !newToken.isEmpty()) {
            authToken = newToken;
            System.out.println("[系统] Token 已自动刷新");
        }
        return response;
    }

    private static String readStream(InputStream is) throws IOException {
        if (is == null) return "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }
}
