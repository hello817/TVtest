package com.topview.entity;

import java.util.Date;

/**
 * 评论实体类，对应数据库 comment 表
 */
public class Comment {
    private Long id;
    private String content;
    private Long videoId;           // 所属视频ID
    private Long userId;            // 评论者ID
    private Long parentId;          // 父评论ID（支持回复功能）
    private Integer likeCount;      // 点赞数
    private Date createdAt;

    public Comment() {}

    public Comment(String content, Long videoId, Long userId) {
        this.content = content;
        this.videoId = videoId;
        this.userId = userId;
    }

    // Getter 和 Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", userId=" + userId +
                ", videoId=" + videoId +
                '}';
    }
}
