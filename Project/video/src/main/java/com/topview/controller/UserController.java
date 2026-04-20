package com.topview.controller;



import com.topview.exception.BizException;
import com.topview.entity.User;
import com.topview.service.UserService;
import com.topview.util.JsonUtil;
import com.topview.util.Result;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
//大致流程 接收请求 ->调用 Service -> 返回 JSON 响应
@WebServlet("/api/user/*")
public class UserController extends HttpServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        try {
            if ("/register".equals(path)) {
                register(req, resp);
            } else if ("/login".equals(path)) {
                login(req, resp);
            } else {
                resp.sendError(404);
            }
        } catch (BizException e) {
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(JsonUtil.toJson(Result.error(400, e.getMessage())));
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "服务器内部错误");
        }
    }

    private void register(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = JsonUtil.fromJson(req.getReader(), User.class);
        userService.register(user.getUsername(), user.getPasswordHash(), user.getNickname());
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(JsonUtil.toJson(Result.success("注册成功")));
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = JsonUtil.fromJson(req.getReader(), User.class);
        String token = userService.login(user.getUsername(), user.getPasswordHash());
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(JsonUtil.toJson(Result.success(data)));
    }
}
