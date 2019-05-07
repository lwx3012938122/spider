package bxkc.day3;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

/**
 * 程序员： Administrator
 * 日期：   2019/4/26 9:45
 * 原网址： http://www.zsfz.org/Category_161/Index.aspx
 * 主页:    http://www.zsfz.org
 **/
public class FetchListAndDetail implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

    public void process(Page page) {

        System.out.println(page.getHtml());
        //遍历列表，获取时间
        if (page.getUrl().toString().contains("Category_161")) {

            List<String> allLins = page.getHtml().xpath("//ul[@class='listStyle1']/li/a").links().all();
            for (String lins : allLins) {
                page.addTargetRequest(lins);
            }
            List<String> times = page.getHtml().xpath("//ul[@class='listStyle1']/li").all();
            for (String time : times) {
                //System.out.println(time);
                System.out.println("日期:" + "20" + time.replaceAll("(<li><a(.*)</a>)", "")
                        .replaceAll("</li>", "").replaceAll("\\[", "")
                        .replaceAll("\\]", "").trim());
            }
            //遍历翻页
            List<String> pages = page.getHtml().xpath("//div[@class='class_page']/a").all();
            for (String nextPage : pages) {

                boolean next = nextPage.contains("下一页");
                System.out.println("flat:"+next);
                if(next){//如果存在下一页翻页，继续爬
                     nextPage ="http://www.zsfz.org"+ nextPage.replaceAll("<a href=", "")
                             .replaceAll(">下一页</a>", "");
                    while (next) {
                        page.addTargetRequest(nextPage);
                    }
                }
            }

        } else { //遍历详情，获取详情标题
            String title = page.getHtml().xpath("//div[@class='c_title_text']/span/h2").toString();
            System.out.println("标题：" + title);

        }
    }

    public Site getSite() {

        return site;
    }

    public static void main(String[] args) {

        System.out.println("开始........");
        Spider.create(new FetchListAndDetail()).addUrl("http://www.zsfz.org/Category_161/Index.aspx").thread(5).start();

    }
}
