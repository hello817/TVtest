package com.topview.controller;

import com.topview.exception.BizException;
import com.topview.entity.Comment;
import com.topview.service.CommentService;
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

//评论接口, 路径/api/comment/*
@WebServlet("/api/comment/*")
public class CommentController extends HttpServlet {

    private static final Logger logger = LoggerUtil.getLogger(CommentController.class.getName());
    private final CommentService commentService = new CommentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            // GET /api/comment/video/{videoId}  获取某个视频的所有评论
            if (pathInfo != null && pathInfo.startsWith("/video/")) {
                String videoIdStr = pathInfo.substring(7); // 去掉 "/video/"
                handleGetCommentsByVideo(videoIdStr, resp);
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            if ("/add".equals(pathInfo)) {
                // POST /api/comment/add  添加评论
                handleAddComment(req, resp);
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

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            // DELETE /api/comment/delete/{commentId}
            if (pathInfo != null && pathInfo.startsWith("/delete/")) {
                String commentIdStr = pathInfo.substring(8); // 去掉 "/delete/"
                handleDeleteComment(commentIdStr, req, resp);
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

     //获取某个视频的所有评论
     //GET /api/comment/video/{videoId}
    private void handleGetCommentsByVideo(String videoIdStr, HttpServletResponse resp) throws IOException {
        try {
            Long videoId = Long.parseLong(videoIdStr);
            List<Comment> comments = commentService.getCommentsByVideoId(videoId);
            logger.info("查询视频评论: videoId=" + videoId + ", 评论数=" + comments.size());
            JsonUtil.writeJson(resp, Result.success(comments));
        } catch (NumberFormatException e) {
            JsonUtil.writeJson(resp, Result.error(400, "无效的视频ID"));
        }
    }
    
     //添加评论
     //POST /api/comment/add
     //Body: { "videoId":123, "content":"...", "parentId":null }
     // 需要登录，userId 从 request 属性获取

    private void handleAddComment(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long userId = (Long) req.getAttribute("userId");
        if (userId == null) {
            JsonUtil.writeJson(resp, Result.error(401, "请先登录"));
            return;
        }

        Comment comment = JsonUtil.fromJson(req.getReader(), Comment.class);
        // 校验必填字段
        if (comment.getVideoId() == null) {
            JsonUtil.writeJson(resp, Result.error(400, "视频ID不能为空"));
            return;
        }
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            JsonUtil.writeJson(resp, Result.error(400, "评论内容不能为空"));
            return;
        }

        comment.setUserId(userId);
        commentService.addComment(comment);
        logger.info("用户 " + userId + " 对视频 " + comment.getVideoId() + " 发表了评论");
        JsonUtil.writeJson(resp, Result.success("评论成功"));
    }

    /**
     * 删除评论
     * DELETE /api/comment/delete/{commentId}
     * 需要登录，只有评论作者或管理员可以删除
     */
    private void handleDeleteComment(String commentIdStr, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long userId = (Long) req.getAttribute("userId");
        if (userId == null) {
            JsonUtil.writeJson(resp, Result.error(401, "请先登录"));
            return;
        }

        try {
            Long commentId = Long.parseLong(commentIdStr);
            commentService.deleteComment(commentId, userId);
            logger.info("用户 " + userId + " 删除了评论 " + commentId);
            JsonUtil.writeJson(resp, Result.success("删除成功"));
        } catch (NumberFormatException e) {
            JsonUtil.writeJson(resp, Result.error(400, "无效的评论ID"));
        } catch (RuntimeException e) {
            // Service 层抛出的权限异常
            JsonUtil.writeJson(resp, Result.error(403, e.getMessage()));
        }
    }
}