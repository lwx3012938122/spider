package bxkc.day8;

import bxkc.day8.dao.WebInfoDao;
import bxkc.day8.domain.WebInfo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 程序员： 梁伟雄
 * 日期：   2019/5/6 14:06
 * 原网址： http://www.gdx.gov.cn/20330/20344/20371/index.htm
 * 主页:   http://www.gdx.gov.cn/
 **/
public class FetchWebInfo2 implements PageProcessor {


    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
    private Map<String, WebInfo> map = new HashMap<String, WebInfo>();
    private Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static int count = 1;
    private int pageIndex = 75;
    private static Spider spider;

    public void process(Page page) {

        try {
            Document doc = page.getHtml().getDocument();
            if (!page.getUrl().toString().contains("content")) {
                //解析雷彪数据

                Elements lis = doc.select("[class=newslist margintop10] ul li");
                System.out.println("第 " + count + "页");
                for (Element li : lis) {
                    String listTitle = li.select("a").text();
                    String listTime = li.select("span").text();
                    String detailLink = null;
                    if (count < 6) {
                        detailLink = "http://www.gdx.gov.cn/20330/20344/20371/" + li.select("a").attr("href");
                    } else {
                        detailLink =li.select("a").attr("href");
                        System.out.println("detailLink:"+detailLink);
                    }
                    WebInfo info = new WebInfo();
                    info.setListTitle(listTitle);
                    info.setPageTime(listTime);
                    info.setDetailLink(detailLink);
                    info.setWebName("重庆市渝北区公共资源交易网");
                    map.put(detailLink, info);

                    boolean byDetailLink = WebInfoDao.findByDetailLink(detailLink);
                    if (!byDetailLink) {
                        page.addTargetRequest(detailLink);
                    }
                }
                //解析翻页
                String text = doc.select("[class='page']").text();
                Matcher matcher = Pattern.compile("/(\\d*)页").matcher(text);
                Integer totalPage = null;
                if (matcher.find()) {
                    totalPage = Integer.valueOf(matcher.group(1));
                }
                if (count < totalPage) {
                    if (count < 5) {
                        String url = "http://www.gdx.gov.cn/20330/20344/20371/index_" + count + ".htm";
                        page.addTargetRequest(url);
                    } else {
                        String url = "http://app.gdx.gov.cn/guidong/20330/20344/20371/index.jsp?pager.offset=" + pageIndex + "&pager.desc=false";
                        page.addTargetRequest(url);
                        pageIndex += 15;
                    }
                    count++;
                }
            } else {//解析翻页
                doc.select(".xl-date").remove();
                doc.select(".xl-weixin").remove();
                String detailTitle = doc.select("div[class='xl-titel cDblue']").text();
                String detailContent = doc.select(".wrapper3").html();
                WebInfo info = map.get(page.getUrl().toString());
                info.setDetailTitle(detailTitle);
                info.setDetailText(detailContent);
                WebInfoDao.insert(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

        String url = "http://www.gdx.gov.cn/20330/20344/20371/index.htm";
        Spider.create(new FetchWebInfo2()).addUrl(url).thread(5).start();

    }
}
