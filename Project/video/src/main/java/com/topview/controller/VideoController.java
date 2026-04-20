package com.topview.controller;


import com.topview.exception.BizException;
import com.topview.entity.Video;
import com.topview.service.VideoService;
import com.topview.util.JsonUtil;
import com.topview.util.Result;
import com.topview.util.LoggerUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 视频相关接口
 * 路径前缀: /api/video/*
 */
@WebServlet("/api/video/*")
public class VideoController extends HttpServlet {

    private static final Logger logger = LoggerUtil.getLogger(VideoController.class.getName());
    private final VideoService videoService = new VideoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // 获取 URL 中 /api/video 之后的部分
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/video?page=1&size=10  获取视频列表
                handleListVideos(req, resp);
            } else {
                // GET /api/video/{id}  获取单个视频详情
                String idStr = pathInfo.substring(1); // 去掉开头的 '/'
                handleGetVideo(idStr, resp);
            }
        } catch (BizException e) {
            logger.warning("业务异常: " + e.getMessage());
            JsonUtil.writeJson(resp, Result.error(400, e.getMessage()));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "服务器内部错误", e);
            JsonUtil.writeJson(resp, Result.error(500, "服务器内部错误"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            if ("/upload".equals(pathInfo)) {
                // POST /api/video/upload  上传视频
                handleUploadVideo(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (BizException e) {
            logger.warning("业务异常: " + e.getMessage());
            JsonUtil.writeJson(resp, Result.error(400, e.getMessage()));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "服务器内部错误", e);
            JsonUtil.writeJson(resp, Result.error(500, "服务器内部错误"));
        }
    }

    /**
     * 处理视频列表请求
     * URL: GET /api/video?page=1&size=10
     */
    private void handleListVideos(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pageParam = req.getParameter("page");
        String sizeParam = req.getParameter("size");

        int page = (pageParam == null || pageParam.isEmpty()) ? 1 : Integer.parseInt(pageParam);
        int size = (sizeParam == null || sizeParam.isEmpty()) ? 10 : Integer.parseInt(sizeParam);

        // 限制每页最大数量，防止恶意请求
        if (size > 50) size = 50;
        if (page < 1) page = 1;

        List<Video> videos = videoService.listVideos(page, size);
        logger.info("查询视频列表: page=" + page + ", size=" + size + ", 结果数=" + videos.size());
        JsonUtil.writeJson(resp, Result.success(videos));
    }

    /**
     * 处理单个视频查询
     * URL: GET /api/video/{id}
     */
    private void handleGetVideo(String idStr, HttpServletResponse resp) throws IOException {
        try {
            Long videoId = Long.parseLong(idStr);
            Video video = videoService.getVideoById(videoId);
            if (video == null) {
                JsonUtil.writeJson(resp, Result.error(404, "视频不存在"));
                return;
            }
            logger.info("获取视频详情: id=" + videoId + ", 标题=" + video.getTitle());
            JsonUtil.writeJson(resp, Result.success(video));
        } catch (NumberFormatException e) {
            JsonUtil.writeJson(resp, Result.error(400, "无效的视频ID"));
        }
    }

    /**
     * 处理视频上传
     * URL: POST /api/video/upload
     * Body: { "title":"...", "description":"...", "url":"...", "coverUrl":"..." }
     * 要求用户已登录，从 request 属性中获取 userId
     */
    private void handleUploadVideo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 从 AuthFilter 设置的属性中获取当前登录用户ID
        Long userId = (Long) req.getAttribute("userId");
        if (userId == null) {
            JsonUtil.writeJson(resp, Result.error(401, "请先登录"));
            return;
        }

        // 解析请求体 JSON
        Video video = JsonUtil.fromJson(req.getReader(), Video.class);
        if (video.getTitle() == null || video.getTitle().trim().isEmpty()) {
            JsonUtil.writeJson(resp, Result.error(400, "视频标题不能为空"));
            return;
        }
        if (video.getUrl() == null || video.getUrl().trim().isEmpty()) {
            JsonUtil.writeJson(resp, Result.error(400, "视频链接不能为空"));
            return;
        }

        // 设置上传者 ID
        video.setUploaderId(userId);
        // 初始观看数和点赞数为 0（数据库已设默认值）

        videoService.uploadVideo(video);
        logger.info("用户 " + userId + " 上传视频: " + video.getTitle());
        JsonUtil.writeJson(resp, Result.success("视频上传成功"));
    }
}