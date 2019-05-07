package bxkc.day3;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 程序员： Administrator
 * 日期：   2019/4/26 16:59
 * 原网址：
 * 主页:
 **/
public class WebInfoPersistence implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
    private static int count = 1;

    public void process(Page page) {

//        SqlSession sqlSession = MyBatisUtils.getSqlSession();
//        System.out.println("sqlSession:" + sqlSession);

        Document doc = page.getHtml().getDocument();
        Elements hrefs = doc.select("[class='table-box'] tr td a");

        //解析列表
        if (page.getUrl().toString().contains("loca")) {
            for (Element href : hrefs) {
                String link = "http://www.tzztb.com" + href.attr("href");
                page.addTargetRequest(link);

            }
            Elements timeTds = doc.select("tr td:matches(\\d{4}-\\d{2}-\\d{2})");
            System.out.println("第 " + count + " 页");
            for (Element timeTd : timeTds) {
                String time = timeTd.text();
                time = time.replaceAll("<td>", "").replaceAll("</td>", "");
                System.out.println("时间：" + time);
            }
            //解析翻页
            //Elements pageNums = doc.select("div[class='Page-bg floatL'] div a");
            String nextPage = doc.select("a:contains(下一页)").get(0).attr("href");
            if (nextPage != null) {//该处要注意：没有下一页，href为null
                nextPage = "http://www.tzztb.com/tzcms/gcjyzhaobgg/" + nextPage;
                System.out.println("翻页链接：" + nextPage);
                page.addTargetRequest(nextPage);
                count++;
            }
        } else {//解析详情
            String title = doc.select(".content-box h1").text();
            System.out.println("标题：" + title);
        }
    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

        Spider.create(new WebInfoPersistence()).addUrl("http://www.tzztb.com/tzcms/gcjyzhaobgg/index.htm?loca=1&xiaoe=1&type=1").thread(5).start();
    }

}
