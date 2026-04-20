package com.topview.dao;


import com.topview.entity.Video;

import java.util.List;

/**
 * 视频数据访问对象
 */
public class VideoDAO extends BaseDAO<Video> {

    //插入新视频
    public int insert(Video video) {
        String sql = "INSERT INTO video (title, description, url, cover_url, uploader_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        return update(sql,
                video.getTitle(),
                video.getDescription(),
                video.getUrl(),
                video.getCoverUrl(),
                video.getUploaderId()
        );
    }

    //根据ID查询视频
    public Video findById(Long id) {
        String sql = "SELECT * FROM video WHERE id = ?";
        return queryOne(sql, id);
    }

    //分页查询视频列表（按创建时间倒序）
    public List<Video> findPage(int offset, int limit) {
        String sql = "SELECT * FROM video ORDER BY created_at DESC LIMIT ? OFFSET ?";
        return queryList(sql, limit, offset);
    }

    //根据上传者ID查询视频
    public List<Video> findByUploaderId(Long uploaderId) {
        String sql = "SELECT * FROM video WHERE uploader_id = ? ORDER BY created_at DESC";
        return queryList(sql, uploaderId);
    }

    //增加视频观看次数（原子操作）
    public int incrementViewCount(Long videoId) {
        String sql = "UPDATE video SET view_count = view_count + 1 WHERE id = ?";
        return update(sql, videoId);
    }

    //更新视频点赞数,点赞/取消点赞时同步
    public int updateLikeCount(Long videoId, int delta) {
        String sql = "UPDATE video SET like_count = like_count + ? WHERE id = ?";
        return update(sql, delta, videoId);
    }

    //删除视频（管理员或上传者可操作）
    public int deleteById(Long id) {
        String sql = "DELETE FROM video WHERE id = ?";
        return update(sql, id);
    }
}
