package bxkc.day4;

import bxkc.day4.dao.WebInfoDao;
import bxkc.day4.domain.WebInfo;
import bxkc.day4.util.PersistenceInfo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 程序员： Administrator
 * 日期：   2019/4/28 9:22
 * 原网址：http://www.tzztb.com/tzcms/gcjyzhaobgg/index.htm?loca=1&xiaoe=1&type=1
 * 主页:   http://www.tzztb.com/
 **/
public class FetchWebInfo implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

    Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private int count = 1;
    private WebInfo info = new WebInfo();//创建对象;
    private Connection conn = null;
    private PreparedStatement ps = null;

    public void process(Page page) {

        try {
            Document doc = page.getHtml().getDocument();
            conn=PersistenceInfo.getConnection();
            if (page.getUrl().toString().contains("loca")) {//解析列表
                Elements dhrefs = doc.select("[class='table-box'] tr td a");//获取详情页链接
                System.out.println("第 " + count + " 页");
                for (Element href : dhrefs) {
                    String listTitle = href.select("span").text();//列表标题
                    String link = "http://www.tzztb.com" + href.attr("href");//详情页链接
                    //获取网站名称
                    String sounce_name = doc.select("span[class='fl'] a").text();
                    boolean flag=WebInfoDao.findByDetailLink(ps,conn,link);
                    if(!flag){//不存在详情页的uri，做新增
                        info.setDetailLink(link);
                        info.setListTitle(listTitle);
                        info.setWebName(sounce_name);
                        WebInfoDao.insert(ps, conn, info);//插入
                        page.addTargetRequest(link);
                    }
                }
                //解析翻页
                String nextLink = doc.select("[class='Page-bg floatL'] a:contains(下一页)").first().attr("href");
                if (nextLink != null) {
                    nextLink = "http://www.tzztb.com/tzcms/gcjyzhaobgg/" + nextLink;
                    page.addTargetRequest(nextLink);
                    count++;
                }
            } else { //解析详情
                String detailLink = page.getUrl().toString();
                String title = doc.select("div[class='content-box'] h1").text().replaceAll("\\[市本级\\]", "").trim();//标题
                String pageTime = doc.select(".content-box span:matches(\\d{4}-\\d{2}-\\d{2})").text();
                String src = doc.select("div[class='content-box'] div div img").attr("src");

                Matcher m = pattern.matcher(pageTime);
                String time = null;
                while (m.find()) {
                    time = m.group();
                }
                info.setDetailLink(detailLink);
                info.setDetailTitle(title);
                info.setPageTime(time);
                info.setDetailText(src);

                WebInfoDao.update(ps,conn,info);//修改，补全信息
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }


    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new FetchWebInfo()).addUrl("http://www.tzztb.com/tzcms/gcjyzhaobgg/index.htm?loca=1&xiaoe=1&type=1").thread(5).start();
    }
}
