package com.topview.util;
//连接池实现
import java.io.InputStream;
import java.sql.*;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 简易数据库连接池
 * - 初始化时创建指定数量的连接放入池中
 * - 提供 getConnection() 和 releaseConnection() 方法
 */
public class DBUtil {
    private static final Logger logger = LoggerUtil.getLogger(DBUtil.class.getName());

    private static String url;
    private static String username;
    private static String password;
    private static int poolSize = 10;          // 默认连接数
    private static LinkedList<Connection> pool = new LinkedList<>();

    // 静态代码块：加载配置并初始化连接池
    static {
        try (InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties props = new Properties();
            props.load(is);
            String driver = props.getProperty("driver");
            url = props.getProperty("url");
            username = props.getProperty("username");
            password = props.getProperty("password");
            poolSize = Integer.parseInt(props.getProperty("poolSize", "10"));

            Class.forName(driver);
            for (int i = 0; i < poolSize; i++) {
                Connection conn = DriverManager.getConnection(url, username, password);
                pool.add(conn);
            }
            logger.info("数据库连接池初始化完成，池大小：" + poolSize);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "数据库连接池初始化失败", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 从池中获取连接
     */
    public static synchronized Connection getConnection() {
        while (pool.isEmpty()) {
            try {
                logger.warning("连接池已空，等待释放...");
                DBUtil.class.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return pool.removeFirst();
    }

    /**
     * 将连接归还给池
     */
    public static synchronized void releaseConnection(Connection conn) {
        if (conn != null) {
            pool.add(conn);
            DBUtil.class.notify();
        }
    }

    /**
     * 关闭所有连接（通常应用关闭时调用）
     */
    public static synchronized void closeAll() {
        for (Connection conn : pool) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "关闭连接失败", e);
            }
        }
        pool.clear();
    }
}
