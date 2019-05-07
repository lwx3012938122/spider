package bxkc.day3;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

/**
 * 程序员： Administrator
 * 日期：   2019/4/26 14:51
 * 原网址：http://www.tzztb.com/tzcms/gcjyzhaobgg/index.htm?loca=1&xiaoe=1&type=1
 * 主页:   http://www.tzztb.com/
 **/
public class FetchListAndDetail3 implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
    private static int count = 1;

    public void process(Page page) {

        Document doc = page.getHtml().getDocument();
        //解析列表
        if (page.getUrl().toString().contains("loca")) {
            Elements hrefs = doc.select("[class='table-box'] tr td a");
            for (Element href : hrefs) {
                String link = "http://www.tzztb.com" + href.attr("href");
                System.out.println("详情页："+link);
                page.addTargetRequest(link);

            }
            Elements timeTds = doc.select("[class='table-box'] tr td:matches(\\d{4}-\\d{2}-\\d{2})");
            System.out.println("第 "+count+" 页");
            for(Element timeTd:timeTds){
                String time = timeTd.text();
                time=time.replaceAll("<td>","").replaceAll("</td>","");
                System.out.println("时间："+time);
            }
            //解析翻页
            String nextPage = doc.select("a:contains(下一页)").get(0).attr("href");
            if(nextPage!=null){//该处要注意：没有下一页，href为null
                nextPage="http://www.tzztb.com/tzcms/gcjyzhaobgg/"+nextPage;
                System.out.println("翻页链接："+nextPage);
                page.addTargetRequest(nextPage);
                count++;
            }
        } else {//解析详情
            String title = doc.select("div[class='content-box'] h1").text();
            System.out.println("标题："+title);
        }

    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

        Spider.create(new FetchListAndDetail3()).addUrl("http://www.tzztb.com/tzcms/gcjyzhaobgg/index.htm?loca=1&xiaoe=1&type=1").thread(5).start();
    }
}
