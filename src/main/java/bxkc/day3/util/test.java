package bxkc.day3.util;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * 程序员： Administrator
 * 日期：   2019/4/26 18:10
 * 原网址：
 * 主页:
 **/
public class test {
    public static void main(String[] args)  {
        try {
            Class.forName("oracle.jdbc.OracleDriver");       //加载数据库驱动
            String url = "jdbc:oracle:thin:@192.168.2.54:1521:orcl";      //连接URL
            String username = "bxkc";
            String password = "bxkc";
            Connection connection = DriverManager.getConnection(url, username, password);
            String sql = "insert into  bxkc.XIN_XI_INFO_TEST(ID,SOURCE_NAME,DETAIL_LINK,DETAIL_CONTENT,PAGE_TIME,CREATE_TIME,LIST_TITLE,CREATE_BY) values(seq_info_test.nextval,?,?,?,?,?,?,?)";
            //String sql = "insert into bxkc.XIN_XI_INFO_TEST(ID,SOURCE_NAME,DETAIL_LINK,CREATE_TIME,CREATE_BY)  values (?,?,?,?,?)";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setObject(1,"https://movie.douban.com");
            ps.setObject(2,"https://movie.douban.com/explore#!type=movie&tag=%E7%83%AD%E9%97%A8&sort=time&page_limit=20&page_start=0");
            ps.setObject(3,"dfsfdsf");
            ps.setObject(4,"sdfsdfs");
            ps.setDate(5,new Date(System.currentTimeMillis()));
            ps.setObject(6,"sdfsdfsdf");
            //ps.setObject(7,info.getListTitle());
            ps.setObject(7,"梁伟雄");

            int executeUpdate = ps
                    .executeUpdate();
            System.out.println(executeUpdate);
        }catch (Exception e){
            e.printStackTrace();
        }
//        Date date = new Date(System.currentTimeMillis());
//        System.out.println(date);

    }
}