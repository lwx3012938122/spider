package bxkc.day2;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Json;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 简单的使用webmagic框架
 */


public class WebMagic implements PageProcessor {

    // 抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
    private static int count = 1;

    public Site getSite() {
        return site;
    }

    public void process(Page page) {

        Html html = page.getHtml();
        Document doc = html.getDocument();
        //解析列表
        if (!page.getUrl().toString().contains("guid")) {
            Elements trs = doc.select(".clearfloat tr");
            System.out.println("第 " + count + " 页");
            for (Element tr : trs) {
                String href = tr.select("td a").attr("href");
                String text = tr.select("td a").text();
                Elements time = tr.select("td:matchesOwn((\\d{4})-(\\d{2})-(\\d{2}))");
                if (!href.equals("") || !text.equals("") || !time.equals("")) {
                    String link = "http://www.yaggzy.org.cn" + href;
                    System.out.println("href:" + href);
                    //page.addTargetRequest(link);
                    System.out.println("text:" + text);

                    //System.out.println("time:" + time);
                    System.out.println("===============================================");
                }
            }

            String dian=doc.getElementsByClass("dian").text();
            String totalPage = dian.replaceAll(":matchesOwn(共\\d页)", "共");
            System.out.println("totalPage:"+totalPage);

            //解析翻页
            if (count < 11) {
                page.addTargetRequest(getRequest(count));
                page.addTargetRequest("");
            }
            count = count + 1;
        } else {
            //解析详情
            Elements trs = doc.getElementsByClass("content_all_nr");
            System.out.println(trs.html());
        }

    }

    public static void main(String[] args) {

        System.out.println("开始爬.......");
        Spider.create(new WebMagic()).addUrl("http://www.yaggzy.org.cn/jyxx/jsgcZbgg").thread(1).start();

    }
    public Request getRequest(int pageNo) {

        Request request = null;
        try {
            request = new Request("http://www.yaggzy.org.cn/jyxx/jsgcZbgg");
            request.setMethod(HttpConstant.Method.POST);
            //Map<String, Object> params = new HashMap<String, Object>();

            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put("currentPage", pageNo);
            params.put("area", "004");
            params.put("secondArea", "000");
            params.put("industriesTypeCode", "000");
            params.put("tenderProjectCode", "");
            params.put("bulletinName", "");
            request.setRequestBody(HttpRequestBody.form(params, "utf-8"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return request;
    }
}
