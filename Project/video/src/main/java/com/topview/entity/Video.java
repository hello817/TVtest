package com.topview.entity;


import java.util.Date;

/**
 * 视频实体类，对应数据库 video 表
 */
public class Video {
    private Long id;
    private String title;
    private String description;
    private String url;             // 外部视频链接
    private String coverUrl;        // 封面图链接
    private Long uploaderId;        // 上传者用户ID
    private Integer viewCount;      // 观看次数
    private Integer likeCount;      // 点赞数
    private Date createdAt;
    private Date updatedAt;

    public Video() {}

    public Video(String title, String description, String url, Long uploaderId) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.uploaderId = uploaderId;
    }

    // Getter 和 Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Long getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(Long uploaderId) {
        this.uploaderId = uploaderId;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
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

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Video{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", uploaderId=" + uploaderId +
                '}';
    }
}
