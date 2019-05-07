package bxkc.day5.dao;

import bxkc.day5.domain.WebInfo;
import bxkc.day5.util.PersistenceInfo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

/**
 * 程序员： Administrator
 * 日期：   2019/4/29 9:58
 * 原网址：
 * 主页:
 **/
public class WebInfoDao {
    //插入语句代码
    public static void insert(WebInfo info) {

        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = PersistenceInfo.getConnection();
            conn.setAutoCommit(false);
            String uuid = UUID.randomUUID().toString();
            String sql = "insert into  XIN_XI_INFO_TEST(" +
                    "ID," +
                    "SOURCE_NAME," +
                    "DETAIL_LINK," +
                    "DETAIL_TITLE," +
                    "DETAIL_CONTENT," +
                    "PAGE_TIME," +
                    "CREATE_TIME," +
                    "LIST_TITLE," +
                    "CREATE_BY) values(?,?,?,?,?,?,?,?,?)";

            ps = conn.prepareStatement(sql);
            ps.setObject(1, uuid);
            ps.setObject(2, info.getWebName());
            ps.setObject(3, info.getDetailLink());
            ps.setObject(4, info.getDetailTitle());
            ps.setObject(5, info.getDetailText());
            ps.setObject(6, info.getPageTime());
            ps.setDate(7, new Date(System.currentTimeMillis()));
            ps.setObject(8, info.getListTitle());
            ps.setObject(9, "梁伟雄");

            int num = ps.executeUpdate();
            conn.commit();
            System.out.println("插入行数：" + num);

        } catch (Exception e) {
            try {
                conn.rollback();//回滚事物
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            new RuntimeException("插入异常：" + e.getMessage());
        } finally {
            if (ps != null || conn != null) {
                try {
                    ps.close();
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 是否存在url
     *
     * @param link
     * @return
     */
    public static boolean findByDetailLink(String link) {

        PreparedStatement ps = null;
        Connection conn = null;

        try {

            conn = bxkc.day6.util.PersistenceInfo.getConnection();
            String sql = " select DETAIL_LINK from bxkc.XIN_XI_INFO_TEST where DETAIL_LINK=?";
            ps = conn.prepareStatement(sql);
            ps.setObject(1, link);
            ResultSet resultSet = ps.executeQuery();
            boolean next = resultSet.next();
            if (next) {
                return true;//存在数据
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ps != null || conn != null) {
                try {
                    ps.close();
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
