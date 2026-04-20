CREATE DATABASE IF NOT EXISTS stone_video DEFAULT CHARSET utf8mb4;
USE stone_video;

-- 用户表
CREATE TABLE `user` (
                        `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
                        `username` VARCHAR(64) NOT NULL UNIQUE COMMENT '登录账号',
                        `password_hash` VARCHAR(128) NOT NULL COMMENT '密码哈希值',
                        `salt` VARCHAR(32) NOT NULL COMMENT '密码盐值',
                        `nickname` VARCHAR(64) COMMENT '昵称',
                        `avatar` VARCHAR(255) COMMENT '头像URL',
                        `role` TINYINT DEFAULT 0 COMMENT '角色：0普通用户 1管理员',
                        `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
                        `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 视频表
CREATE TABLE `video` (
                         `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '视频ID',
                         `title` VARCHAR(255) NOT NULL COMMENT '视频标题',
                         `description` TEXT COMMENT '视频描述',
                         `url` VARCHAR(512) NOT NULL COMMENT '外部视频链接',
                         `cover_url` VARCHAR(512) COMMENT '封面图链接',
                         `uploader_id` BIGINT NOT NULL COMMENT '上传者用户ID',
                         `view_count` INT DEFAULT 0 COMMENT '观看次数',
                         `like_count` INT DEFAULT 0 COMMENT '点赞数',
                         `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
                         `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         FOREIGN KEY (`uploader_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频表';

-- 评论表
CREATE TABLE `comment` (
                           `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
                           `content` TEXT NOT NULL COMMENT '评论内容',
                           `video_id` BIGINT NOT NULL COMMENT '所属视频ID',
                           `user_id` BIGINT NOT NULL COMMENT '评论者ID',
                           `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID（用于回复）',
                           `like_count` INT DEFAULT 0 COMMENT '点赞数',
                           `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (`video_id`) REFERENCES `video`(`id`),
                           FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';