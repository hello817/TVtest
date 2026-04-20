package com.topview.inceptor;

import com.topview.util.TokenUtil;
import com.topview.util.JsonUtil;
import com.topview.util.Result;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证过滤器，校验 Token
 * 可在 web.xml 中配置拦截路径（如 /api/*）
 */
public class AuthInceptor implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 放行注册、登录等公开接口
        String path = req.getRequestURI();
        if (path.contains("/user/register") || path.contains("/user/login")) {
            chain.doFilter(request, response);
            return;
        }

        String token = req.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            sendError(resp, 401, "未提供Token");
            return;
        }

        Long userId = TokenUtil.getUserId(token);
        if (userId == null) {
            sendError(resp, 401, "Token无效或已过期");
            return;
        }

        // 将 userId 存入 request 属性，后续使用
        req.setAttribute("userId", userId);

        // 可选：刷新 Token 并设置响应头
        String newToken = TokenUtil.refreshTokenIfNeeded(token);
        if (!token.equals(newToken)) {
            resp.setHeader("New-Token", newToken);
        }

        chain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse resp, int code, String msg) throws IOException {
        resp.setStatus(code);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(JsonUtil.toJson(Result.error(code, msg)));
    }

    @Override public void init(FilterConfig filterConfig) {}
    @Override public void destroy() {}
}
