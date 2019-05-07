//package bxkc.day3.util;
//
//import org.apache.ibatis.io.Resources;
//import org.apache.ibatis.session.SqlSession;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.apache.ibatis.session.SqlSessionFactoryBuilder;
//
//import java.io.InputStream;
//import java.sql.Connection;
//
///**
// * 程序员： Administrator
// * 日期：   2019/4/26 16:41
// * 原网址：
// * 主页:
// **/
//public class MyBatisUtils {
//
//    private static ThreadLocal<SqlSession> local = new InheritableThreadLocal<SqlSession>();
//    private static SqlSessionFactory sqlSessionFactory;
//
//    static {
//        try {
//            String resource = "mybatis-config.xml";
//            InputStream inputStream = Resources.getResourceAsStream(resource);
//            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//
//        } catch (Exception e) {
//            new RuntimeException("读取mybatis-config.xml失败！"+e.getMessage());
//        }
//    }
//
//    public static SqlSession getSqlSession() {
//
//        try {
//            SqlSession sqlSession = sqlSessionFactory.openSession();
//            local.set(sqlSession);//设值
//            System.out.println("链接数据库成功");
//            return local.get();
//        }catch (Exception e){
//            new RuntimeException("获取sqlSession失败！"+e.getMessage());
//        }
//        return null;
//    }
//
//    //获取SqlSession
//    public static ThreadLocal<SqlSession> getLocal() {
//
//        return local;
//    }
//
//    //设置 SqlSession
//    public static void setLocal(ThreadLocal<SqlSession> local) {
//        MyBatisUtils.local = local;
//    }
//}
