package bxkc.day8;

import bxkc.day8.dao.WebInfoDao;
import bxkc.day8.domain.WebInfo;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 程序员： 梁伟雄
 * 日期：   2019/5/6 9:17
 * 原网址：http://jyzx.ybq.gov.cn/cqybwz/jyxx/001004/001004010/MoreInfo.aspx?CategoryNum=264200
 * 主页:   http://jyzx.ybq.gov.cn/
 **/
public class FetchWebInfo implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
    private Map<String, WebInfo> map = new HashMap<String, WebInfo>();
    private Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static int count = 1;
    private static Spider spider;

    public void process(Page page) {
        try {
            Document doc = page.getHtml().getDocument();
            if (!page.getUrl().toString().contains("InfoID")) {
                Elements trs = doc.select("[id='MoreInfoList1_DataGrid1'] tr");
                List<String> details = new ArrayList<String>();
                System.out.println("第 " + count + "页");
                for (Element tr : trs) {
                    String href = "http://jyzx.ybq.gov.cn" + tr.select("td a").attr("href");
                    String listTille = tr.select("td a").text();
                    String listTime = tr.select("td:matches(\\d{4}-\\d{2}-\\d{2})").text();

                    WebInfo info = new WebInfo();
                    info.setPageTime(listTime);
                    info.setListTitle(listTille);
                    info.setDetailLink(href);
                    info.setWebName("重庆市渝北区公共资源交易网");
                    map.put(href, info);
                    boolean byDetailLink = WebInfoDao.findByDetailLink(href);
                    if (!byDetailLink) {
                        page.addTargetRequest(href);
                    }
                }
                //添加翻页
                String text = doc.select("[id='MoreInfoList1_Pager'] tr td").text();
                Matcher matcher = Pattern.compile("总页数：(\\d*) 当").matcher(text);
                Integer totalPage = null;
                if (matcher.find()) {
                    totalPage = Integer.valueOf(matcher.group(1));
                    if (count < totalPage) {
                        count++;
                        String __CSRFTOKEN = doc.select("[id='__CSRFTOKEN']").attr("value");
                        String __VIEWSTATE = doc.select("[id='__VIEWSTATE']").attr("value");
                        page.addTargetRequest(getRequest(__CSRFTOKEN, __VIEWSTATE, count));
                    }
                }
            } else {
                String detailTitle = doc.select("[class='p-content'] p[class='p-title']").text();
                String detailContent = doc.select("[class='p-content']").html();
                //删除
                doc.select("[class='p-info'] p a").remove();
                WebInfo info = map.get(page.getUrl().toString());
                info.setDetailText(detailContent);
                info.setDetailTitle(detailTitle);
                System.out.println("listTime:" + info.getPageTime());
                WebInfoDao.insert(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Site getSite() {
        Map map = getCookie();
        int count = 3;
        while (map.size() < 1 && count > 0) {
            map = getCookie();
            count--;
        }
        String cookie = map.get("cookie").toString();
        return this.site.addHeader("cookie", cookie);
    }

    public static void main(String[] args) {
        String url = "http://jyzx.ybq.gov.cn/cqybwz/jyxx/001004/001004010/MoreInfo.aspx?CategoryNum=264200";
        Spider.create(new FetchWebInfo()).addUrl(url).thread(5).start();

    }

    private static Map getCookie() {
        Map<String, String> map = new HashMap<String, String>();
        String url = "http://jyzx.ybq.gov.cn/cqybwz/jyxx/001004/001004010/MoreInfo.aspx?CategoryNum=264200";
        String cookie = "";
        StringBuilder sb = new StringBuilder();
        CloseableHttpClient httpClient;
        CloseableHttpResponse response;
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
            httpClient = HttpClients.createDefault();
            RequestConfig config = RequestConfig.custom()
                    .setRedirectsEnabled(false)
                    .build();
            httpGet.setConfig(config);
            response = httpClient.execute(httpGet);
            Header[] headers = response.getAllHeaders();
            for (Header h : headers) {
                if (h.getName().equals("Set-Cookie")) {
                    System.out.println(h.getName() + ":" + h.getValue());
                    sb.append(h.getValue()).append(";");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        cookie = sb.toString();
        map.put("cookie", cookie);
        return map;
    }

    private static Request getRequest(String __CSRFTOKEN, String __VIEWSTATE, int count) {
        Request request = null;
        try {
            String url = "http://jyzx.ybq.gov.cn/cqybwz/jyxx/001004/001004010/MoreInfo.aspx?CategoryNum=264200";
            request = new Request(url);
            request.setMethod(HttpConstant.Method.POST);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("__CSRFTOKEN", __CSRFTOKEN);
            map.put("__VIEWSTATE", __VIEWSTATE);
            map.put("__EVENTTARGET", "MoreInfoList1$Pager");
            map.put("__EVENTARGUMENT", count);
            request.setRequestBody(HttpRequestBody.form(map, "utf-8"));
            return request;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return request;
    }
}
