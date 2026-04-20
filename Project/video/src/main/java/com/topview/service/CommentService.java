package com.topview.service;


import com.topview.dao.CommentDAO;
import com.topview.entity.Comment;

import java.util.List;

public class CommentService {
    private final CommentDAO commentDAO = new CommentDAO();

    public void addComment(Comment comment) {
        commentDAO.insert(comment);
    }

    public List<Comment> getCommentsByVideoId(Long videoId) {
        return commentDAO.findByVideoId(videoId);
    }

    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentDAO.findById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        // 简单权限：只有作者本人或管理员可删（此处略去管理员判断）
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此评论");
        }
        commentDAO.deleteById(commentId);
    }
}
