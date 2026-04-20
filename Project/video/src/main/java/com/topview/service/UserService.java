package com.topview.service;


import com.topview.dao.UserDAO;
import com.topview.exception.BizException;
import com.topview.entity.User;
import com.topview.util.PasswordUtil;
import com.topview.util.TokenUtil;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    /**
     * 用户注册
     */
    public void register(String username, String password, String nickname) {
        // 检查用户名是否已存在
        User exist = userDAO.findByUsername(username);
        if (exist != null) {
            throw new BizException("用户名已被注册");
        }

        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(password, salt);

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(hash);
        user.setSalt(salt);
        user.setNickname(nickname);
        user.setRole(0); // 默认普通用户
        userDAO.insert(user);
    }

    /**
     * 用户登录，成功返回 Token
     */
    public String login(String username, String password) {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            throw new BizException("用户名或密码错误");
        }
        boolean valid = PasswordUtil.verifyPassword(password, user.getPasswordHash(), user.getSalt());
        if (!valid) {
            throw new BizException("用户名或密码错误");
        }
        // 生成 Token 并缓存
        return TokenUtil.generateToken(user.getId());
    }

    public User getUserById(Long userId) {
        return userDAO.findById(userId);
    }
}
