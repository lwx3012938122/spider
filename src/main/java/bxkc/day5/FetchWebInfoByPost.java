package bxkc.day5;

import bxkc.day5.dao.WebInfoDao;
import bxkc.day5.domain.WebInfo;
import bxkc.day5.util.PersistenceInfo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.HttpConstant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 程序员： Administrator
 * 日期：   2019/4/29 15:43
 * 原网址： http://www.yaggzy.org.cn/jyxx/jsgcZbgg
 * 主页:    http://www.yaggzy.org.cn
 **/
public class FetchWebInfoByPost implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
    private Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private WebInfo info = new WebInfo();
    private int count = 1;


    public void process(Page page) {

        try {
            Document doc = page.getHtml().getDocument();
            if (!page.getUrl().toString().contains("guid")) {
                Elements detailLinks = doc.select("li[class='clearfloat'] tr td a");
                System.out.println("第 " + count + " 页");
                for (Element detailLink : detailLinks) {
                    String href = "http://www.yaggzy.org.cn" + detailLink.attr("href");
                    String souce_name = "雅安公共资源交易服务中心";
                    String listTitle = detailLink.text();

                    info.setDetailLink(href);
                    info.setWebName(souce_name);
                    info.setListTitle(listTitle);

                    boolean byDetailLink = WebInfoDao.findByDetailLink(href);
                    if (!byDetailLink) {
                        info.setDetailLink(href);
                        info.setWebName(souce_name);
                        info.setListTitle(listTitle);
                        WebInfoDao.insert(info);
                        page.addTargetRequest(href);
                    }
                }

                //解析翻页
                String nextPage = doc.select("[class='mmggxlh'] a:contains(下一页)").attr("onclick");
                if (nextPage != null) {
                    count++;
                    page.addTargetRequest(getRequest(count));
                }

            } else {
                String detailTitle = doc.select("div[class='table_title']").html();
                String pageTime = doc.select("div[class='time']").html();
                Matcher matcher = pattern.matcher(pageTime);
                while (matcher.find()) {
                    pageTime = matcher.group();
                }
                String detailContent = doc.select("[class='content_all_nr']").html();
                //System.out.println("detailContent:"+detailContent);
                String detailLink = page.getUrl().toString();
                info.setDetailLink(detailLink);
                info.setDetailTitle(detailTitle);
                info.setPageTime(pageTime);
                info.setDetailText(detailContent);

                //WebInfoDao.update(info);//修改
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Request getRequest(int count) {

        Request request = new Request("http://www.yaggzy.org.cn/jyxx/jsgcZbgg");
        request.setMethod(HttpConstant.Method.POST);
        Map<String, Object> param = new HashMap<String, Object>();

        param.put("currentPage", count);
        param.put("area", "004");
        param.put("secondArea", "000");
        param.put("industriesTypeCode", "000");
        param.put("tenderProjectCode", "");
        param.put("bulletinName", "");
        request.setRequestBody(HttpRequestBody.form(param, "utf-8"));

        return request;
    }

    public Site getSite() {
        return site;
    }


    public static void main(String[] args) {
        Spider.create(new FetchWebInfoByPost()).addUrl("http://www.yaggzy.org.cn/jyxx/jsgcZbgg").thread(5).start();
    }
}
