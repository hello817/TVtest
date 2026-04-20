package com.topview.dao;


import com.topview.entity.Comment;

import java.util.List;

/**
 * 评论数据访问对象
 */
public class CommentDAO extends BaseDAO<Comment> {

    //插入新评论
    public int insert(Comment comment) {
        String sql = "INSERT INTO comment (content, video_id, user_id, parent_id) " +
                "VALUES (?, ?, ?, ?)";
        return update(sql,
                comment.getContent(),
                comment.getVideoId(),
                comment.getUserId(),
                comment.getParentId()
        );
    }

    //根据ID查询评论
    public Comment findById(Long id) {
        String sql = "SELECT * FROM comment WHERE id = ?";
        return queryOne(sql, id);
    }

    //查询某个视频的所有评论（按时间正序，即最早的在前）
    public List<Comment> findByVideoId(Long videoId) {
        String sql = "SELECT * FROM comment WHERE video_id = ? ORDER BY created_at ASC";
        return queryList(sql, videoId);
    }

    //查询某个视频的所有评论（按点赞数倒序，用于热度排序，进阶功能）

    public List<Comment> findByVideoIdOrderByLikes(Long videoId) {
        String sql = "SELECT * FROM comment WHERE video_id = ? ORDER BY like_count DESC, created_at ASC";
        return queryList(sql, videoId);
    }

    //根据用户ID查询他发表的所有评论
    public List<Comment> findByUserId(Long userId) {
        String sql = "SELECT * FROM comment WHERE user_id = ? ORDER BY created_at DESC";
        return queryList(sql, userId);
    }

    //删除评论
    public int deleteById(Long id) {
        String sql = "DELETE FROM comment WHERE id = ?";
        return update(sql, id);
    }

    //更新评论点赞数
    public int updateLikeCount(Long commentId, int delta) {
        String sql = "UPDATE comment SET like_count = like_count + ? WHERE id = ?";
        return update(sql, delta, commentId);
    }
}