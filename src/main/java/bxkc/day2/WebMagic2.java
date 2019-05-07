package bxkc.day2;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.Scheduler;

public class WebMagic2 implements PageProcessor {

    //抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site=Site.me().setRetryTimes(3).setSleepTime(100);

    public void process(Page page) {

        
    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

        System.out.println("开始爬.........");
        Spider.create(new WebMagic2()).addUrl("http://www.yaggzy.org.cn/jyxx/jsgcZbgg").thread(5).run();

    }


}
