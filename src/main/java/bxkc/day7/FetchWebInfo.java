package bxkc.day7;

import bxkc.day7.dao.WebInfoDao;
import bxkc.day7.domain.WebInfo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 程序员： 梁伟雄
 * 日期：   2019/5/5 10:00
 * 原网址：https://www.lyggzy.com.cn/lyztb/gcjs/081001/081001003/081001003002/
 * 主页:   https://www.lyggzy.com.cn
 **/
public class FetchWebInfo implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
    private Map<String, WebInfo> map = new HashMap<String, WebInfo>();
    private Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static int count = 1;
    private int num = 1;
    private static Spider spider;

    public void process(Page page) {

        Document doc = page.getHtml().getDocument();
        if (!page.getUrl().toString().contains("InfoID")) {
            Elements lis = doc.select("ul[class='list'] li");
            System.out.println("第 " + count + " 页");
            for (Element li : lis) {

                String detailLink = "https://www.lyggzy.com.cn" + li.select("a").attr("href");
                String listTiltle = li.select("a").text();
                String listTime = li.select("span").text();
                WebInfo info = new WebInfo();
                info.setListTitle(listTiltle);
                info.setPageTime(listTime);
                info.setDetailLink(detailLink);
                info.setWebName("龙岩市公共资源交易中心");
                boolean byDetailLink = WebInfoDao.findByDetailLink(detailLink);
                if (!byDetailLink) {
                    page.addTargetRequest(detailLink);
                } else {
                    num++;
                    if (num > 100) {
                        spider.stop();
                    }
                }
                map.put(detailLink, info);
            }
            //解析翻页
            String onclick = doc.select("[class='r-bd'] li a:contains(下一页)").attr("onclick");
            if (onclick != null || !onclick.equals("")) {
                count++;
                page.addTargetRequest("https://www.lyggzy.com.cn/lyztb/gcjs/081001/081001003/081001003002/?pageing=" + count);
            }
        } else {
            String detailContent = doc.select("[class='detail-content']").html();
            String detailTitle = doc.select("[class='detail-content'] h3").text();
            WebInfo info = map.get(page.getUrl().toString());
            info.setDetailTitle(detailTitle);
            info.setDetailText(detailContent);
            WebInfoDao.insert(info);

        }
    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

        spider = Spider.create(new FetchWebInfo()).addUrl("https://www.lyggzy.com.cn/lyztb/gcjs/081001/081001003/081001003002/?pageing=" + count).thread(5);
        spider.start();
    }
}
