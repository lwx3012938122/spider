package bxkc.day3;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 程序员： Administrator
 * 日期：   2019/4/26 12:48
 * 原网址：http://www.tzztb.com/tzcms/gcjyzhaobgg/index.htm?loca=1&xiaoe=1&type=1
 * 主页:    http://www.tzztb.com/
 **/
public class FetchListAndDetail2 implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

    private Pattern pattern=Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");

    private static   int count=1;

    public void process(Page page) {

        //解析列表
        if(page.getUrl().toString().contains("loca")){

            List<String> links = page.getHtml().xpath("[@class='table-box']/tbody/tr/td/a").links().all();
            for(String link:links){
                //System.out.println(link);
                page.addTargetRequest(link);
            }
            List<String> times = page.getHtml().xpath("[@class='table-box']/tbody/tr/td[4]").all();
            System.out.println("第 "+count+"页");
            for (String time:times) {
                time = time.replaceAll("<td>", "").replaceAll("</td>", "");
                System.out.println("时间："+time);
            }
            //解析翻页
            List<String> all = page.getHtml().xpath("//div[@class='Page-bg']/div/a").all();
            for(String nextPage:all){
                boolean next = nextPage.contains("下一页");
                if(next){//如果有下一页，继续
                    count+=1;//翻页,添加页码
                    nextPage="http://www.tzztb.com/tzcms/gcjyzhaobgg/"+ nextPage.replaceAll("<a href=\"", "")
                            .replaceAll("\">下一页</a>", "");
                    System.out.println("翻页地址："+nextPage);
                    page.addTargetRequest(nextPage);

                }
            }

        }else {//解析详情
            String title = page.getHtml().xpath("[@class='content-box']/h1").toString();
            title=title.replaceAll("<h1>","").replaceAll("</h1>","");
            System.out.println("标题："+title);
        }
    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        System.out.println("开始........");
        Spider.create(new FetchListAndDetail2()).addUrl("http://www.tzztb.com/tzcms/gcjyzhaobgg/index.htm?loca=1&xiaoe=1&type=1").thread(5).start();

    }
}
