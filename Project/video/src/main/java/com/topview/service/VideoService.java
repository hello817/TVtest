package com.topview.service;

import com.topview.dao.VideoDAO;
import com.topview.entity.Video;
import com.topview.util.LoggerUtil;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class VideoService {
    private static final Logger logger = LoggerUtil.getLogger(VideoService.class.getName());
    private final VideoDAO videoDAO = new VideoDAO();
    // 简单缓存：videoId -> Video
    private final ConcurrentHashMap<Long, Video> videoCache = new ConcurrentHashMap<>();

    public Video getVideoById(Long id) {
        // 先查缓存
        Video cached = videoCache.get(id);
        if (cached != null) {
            logger.info("从缓存获取视频 ID=" + id);
            return cached;
        }
        Video video = videoDAO.findById(id);
        if (video != null) {
            videoCache.put(id, video);
            // 同时更新观看次数（简单处理）
            videoDAO.incrementViewCount(id);
        }
        return video;
    }

    public List<Video> listVideos(int page, int size) {
        int offset = (page - 1) * size;
        return videoDAO.findPage(offset, size);
    }

    public void uploadVideo(Video video) {
        videoDAO.insert(video);
    }

    // 删除或更新时可清除缓存
    public void evictCache(Long videoId) {
        videoCache.remove(videoId);
    }
}
