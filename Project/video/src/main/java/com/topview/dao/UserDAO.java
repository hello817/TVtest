package com.topview.dao;


import com.topview.entity.User;

import java.util.List;

/**
 * 用户数据访问对象
 * 继承 BaseDAO，获得通用 CRUD 能力
 */
public class UserDAO extends BaseDAO<User> {

    /**
     * 插入新用户
     * 注意：密码哈希和盐值已在 Service 层生成，这里直接保存
     */
    public int insert(User user) {
        String sql = "INSERT INTO user (username, password_hash, salt, nickname, avatar, role) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        return update(sql,
                user.getUsername(),
                user.getPasswordHash(),
                user.getSalt(),
                user.getNickname(),
                user.getAvatar(),
                user.getRole()
        );
    }

    /**
     * 根据用户名查询用户（用于登录和注册查重）
     */
    public User findByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";
        return queryOne(sql, username);
    }

    /**
     * 根据用户ID查询
     */
    public User findById(Long id) {
        String sql = "SELECT * FROM user WHERE id = ?";
        return queryOne(sql, id);
    }

    /**
     * 更新用户信息（可扩展）
     */
    public int updateUser(User user) {
        String sql = "UPDATE user SET nickname = ?, avatar = ?, role = ? WHERE id = ?";
        return update(sql,
                user.getNickname(),
                user.getAvatar(),
                user.getRole(),
                user.getId()
        );
    }

    /**
     * 查询所有用户（管理员功能，可暂不使用）
     */
    public List<User> findAll() {
        String sql = "SELECT * FROM user";
        return queryList(sql);
    }
}