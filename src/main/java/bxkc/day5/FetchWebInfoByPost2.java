package bxkc.day5;

import bxkc.day5.dao.WebInfoDao;
import bxkc.day5.domain.WebInfo;
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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 程序员： Administrator
 * 日期：   2019/4/29 17:44
 * 原网址：http://www.yaggzy.org.cn/jyxx/jsgcZbgg
 * 主页:   http://www.yaggzy.org.cn
 **/
public class FetchWebInfoByPost2 implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
    private Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private Map<String, WebInfo> list = new HashMap<String, WebInfo>();
    private int count = 1;
    private int num = 1;
    public static Spider spider=null;

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

                    WebInfo info = new WebInfo();
                    info.setDetailLink(href);
                    info.setWebName(souce_name);
                    info.setListTitle(listTitle);
                    list.put(href, info);
                    //添加请求
                    page.addTargetRequest(href);
                }

                //解析翻页
                String nextPage = doc.select("[class='mmggxlh'] a:contains(下一页)").attr("onclick");
                if (nextPage != null&& !nextPage.equals("")) {
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
                String detailLink = page.getUrl().toString();
                WebInfo webInfo = list.get(detailLink);
                boolean byDetailLink = WebInfoDao.findByDetailLink(detailLink);

                if (!byDetailLink) {// 若在数据库中没有找到该url,　做新增
                    WebInfo info = list.get(detailLink);
                    info.setDetailLink(detailLink);
                    info.setDetailTitle(detailTitle);
                    info.setPageTime(pageTime);
                    info.setDetailText(detailContent);
                    WebInfoDao.insert(info);
                }else{
                    num=num+1;
                    if(num>50){
                        spider.stop();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Site getSite() {
        return site;
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

    public static void main(String[] args) {
        spider=Spider.create(new FetchWebInfoByPost2()).addUrl("http://www.yaggzy.org.cn/jyxx/jsgcZbgg").thread(5);
        spider.start();
    }
}
