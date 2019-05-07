package bxkc.day7.dao;

import bxkc.day7.domain.FetcheWebInfo;
import bxkc.day6.util.PersistenceInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

/**
 * 程序员： 梁伟雄
 * 日期：   2019/4/30 10:25
 * 原网址： https://www.lyggzy.com.cn/lyztb/gcjs/081001/081001003/081001003002/
 * 主页:    https://www.lyggzy.com.cn/
 **/
public class FetchWebInfoDao {

    private static Connection conn = null;

    static {
        try {
            conn = PersistenceInfo.getConnection();
        } catch (Exception e) {
            new RuntimeException("获取Connection失败！" + e.getMessage());
        }
    }

    public static boolean findBydetailTitle(String detailTitle) {

       try {
           String sql="select LIST_TITLE from JIAN_YAN_FETCH_WEBINFO where LIST_TITLE=?";
           PreparedStatement   ps=conn.prepareStatement(sql);
           ps.setObject(1,detailTitle);
           ResultSet resultSet = ps.executeQuery();
           boolean next = resultSet.next();
           if(next){
               return true;
           }
       }catch (Exception e){
           e.printStackTrace();
       }
        return false;
    }

    public static void insert(FetcheWebInfo info) {

       try {
           String sql="insert into JIAN_YAN_FETCH_WEBINFO(" +
                   "ID," +
                   "LIST_TITLE," +
                   "LIST_TIME," +
                   "DETAIL_CONTENT) values(?,?,?,?)";
           PreparedStatement  ps=conn.prepareStatement(sql);
           String uuid = UUID.randomUUID().toString();

           ps.setObject(1,uuid);
           ps.setObject(2,info.getListTitle());
           ps.setObject(3,info.getListTime());
           ps.setObject(4,info.getDetailContent());

           int num = ps.executeUpdate();
           System.out.println("插入 "+num+" 行");

       }catch (Exception e){
           e.printStackTrace();
       }
    }
}
