package bxkc.day6.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 程序员： 梁伟雄
 * 日期：   2019/4/28 10:13
 * 原网址： http://www.jnggzyjy.gov.cn/GaoXinQu/Bulletins?CategoryCode=503000
 * 主页:    http://www.jnggzyjy.gov.cn/
 **/
public class PersistenceInfo {

    private static final String DRIVER = "oracle.jdbc.OracleDriver";
    private static final String URL = "jdbc:oracle:thin:@192.168.2.54:1521:orcl";
    private static final String USER_NAME = "bxkc";
    private static final String PASSWORD = "bxkc";

    public static ThreadLocal<Connection> conn =new ThreadLocal<Connection>();
    public static ThreadLocal<Connection> getConn() {
        return conn;
    }

    public static void setConn(ThreadLocal<Connection> conn) {
        PersistenceInfo.conn = conn;
    }

    static {
        try {
            Class.forName(DRIVER);
        } catch (Exception e) {
            new RuntimeException("驱动加载失败！" + e);
        }
    }

    public static Connection getConnection() throws SQLException {

        try {
            Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
            conn.set(connection);
            return conn.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
