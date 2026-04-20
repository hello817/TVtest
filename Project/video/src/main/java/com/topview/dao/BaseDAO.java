package com.topview.dao;

import com.topview.util.DBUtil;
import com.topview.util.LoggerUtil;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 基础 DAO，封装通用的增删改查操作
 * 子类继承后可直接调用
 */
public abstract class BaseDAO<T> {
    private static final Logger logger = LoggerUtil.getLogger(BaseDAO.class.getName());
    private final Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public BaseDAO() {
        // 通过反射获取泛型参数的实际类型
        this.entityClass = (Class<T>) ((java.lang.reflect.ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    // ---------- 通用更新（增删改） ----------
    protected int update(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            setParameters(ps, params);
            int rows = ps.executeUpdate();
            logger.info("执行更新，影响行数：" + rows);
            return rows;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "数据库更新异常", e);
            throw new RuntimeException(e);
        } finally {
            close(ps);
            DBUtil.releaseConnection(conn);
        }
    }

    // ---------- 通用查询（返回单个对象） ----------
    protected T queryOne(String sql, Object... params) {
        List<T> list = queryList(sql, params);
        return list.isEmpty() ? null : list.get(0);
    }

    // ---------- 通用查询（返回列表） ----------
    protected List<T> queryList(String sql, Object... params) {
        List<T> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            setParameters(ps, params);
            rs = ps.executeQuery();
            while (rs.next()) {
                T obj = entityClass.getDeclaredConstructor().newInstance();
                mapRow(rs, obj);
                list.add(obj);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "数据库查询异常", e);
            throw new RuntimeException(e);
        } finally {
            close(rs, ps);
            DBUtil.releaseConnection(conn);
        }
        return list;
    }

    // ---------- 辅助方法 ----------
    private void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    private void mapRow(ResultSet rs, T obj) throws Exception {
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            // 将驼峰命名转换为下划线命名（简单映射，也可使用注解）
            String columnName = camelToSnake(fieldName);
            try {
                Object value = rs.getObject(columnName);
                if (value != null) {
                    field.set(obj, value);
                }
            } catch (SQLException ignored) {
                // 某些字段可能不在结果集中，忽略
            }
        }
    }

    private String camelToSnake(String camel) {
        return camel.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    private void close(AutoCloseable... closeables) {
        for (AutoCloseable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception e) {
                    logger.warning("关闭资源失败：" + e.getMessage());
                }
            }
        }
    }
}
