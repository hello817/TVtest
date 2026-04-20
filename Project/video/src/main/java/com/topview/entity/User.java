package com.topview.entity;


import java.util.Date;

/**
 * 用户实体类，对应数据库 user 表
 */
public class User {
    private Long id;
    private String username;        // 登录账号
    private String passwordHash;    // 密码哈希值
    private String salt;            // 密码盐值
    private String nickname;        // 昵称
    private String avatar;          // 头像URL
    private Integer role;           // 角色：0普通用户 1管理员
    private Date createdAt;
    private Date updatedAt;

    // 无参构造（反射需要）
    public User() {}

    // 用于注册的便捷构造
    public User(String username, String passwordHash, String salt, String nickname) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.nickname = nickname;
        this.role = 0;
    }

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
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
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", role=" + role +
                '}';
    }
}