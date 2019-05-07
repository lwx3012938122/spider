package bxkc.day8;

import bxkc.day8.dao.WebInfoDao;
import bxkc.day8.domain.WebInfo;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
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
 * 日期：   2019/5/6 16:02
 * 原网址： http://www.lhztb.gov.cn/dahai/Bulltinmore1.aspx?PrjTypeId=01&BulletinTypeId=17
 * 主页:    http://www.lhztb.gov.cn/
 **/
public class FetchWebInfo3 implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
    private Map<String, WebInfo> map = new HashMap<String, WebInfo>();
    private Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static int count = 1;
    private int pageIndex = 1;
    private static Spider spider;

    public void process(Page page) {
        try {
            Document doc = page.getHtml().getDocument();
            if (!page.getUrl().toString().contains("PrjId")) {
                Elements tds = doc.select("td[height='40']");
                System.out.println("第 " + count + "页");
                for (Element td : tds) {
                    td = td.parent();
                    String span = td.select("span").text();
                    String detailLink = "http://www.lhztb.gov.cn" + td.select("a").attr("href");
                    String listTitle = span + td.select("a").text();
                    Matcher matcher = pattern.matcher(td.text());
                    String listTime = null;
                    if (matcher.find()) {
                        listTime = matcher.group();
                    }
                    WebInfo info = new WebInfo();
                    info.setListTitle(listTitle);
                    info.setDetailLink(detailLink);
                    info.setPageTime(listTime);
                    info.setWebName("临海市公共资源交易中心");
                    map.put(detailLink, info);
                    boolean byDetailLink = WebInfoDao.findByDetailLink(detailLink);
                    if (!byDetailLink) {
                        page.addTargetRequest(detailLink);
                    }
                }
                //解析翻页
                String text = doc.select("#Label1 div").text();
                if (!text.equals("")) {
                    Matcher matcher = Pattern.compile("共(\\d*)页").matcher(text);
                    Integer totalPage = null;
                    if (matcher.find()) {
                        totalPage = Integer.valueOf(matcher.group(1));
                    }
                    if (count < totalPage) {
                        count++;
                        pageIndex++;
                        String url = "http://www.lhztb.gov.cn/dahai/Bulltinmore1.aspx?PrjTypeId=01&BulletinTypeId=17&pageindex=" + pageIndex;
                        page.addTargetRequest(url);
                    }
                }
            } else {//解析详情
                //点开链接
                String path = page.getUrl().toString();
                String url = path.substring(0, path.lastIndexOf("/") + 1);
                String href = url + doc.select("font:contains(合同公告)").parents().first().attr("href");
                doc = openLink(href);
                WebInfo info = map.get(page.getUrl().toString());
                if (page.getUrl().toString().contains("FileType")) {
                    String detailTitle = doc.select(".title1").text();
                    String down =url+doc.select(".GridView6RowStyle td a").attr("href");
                    doc.select(".GridView6RowStyle td a").attr("href",down);
                    String detailContent = doc.select("[id='_ctl3_Content']").html();
                    info.setDetailText(detailContent);
                    info.setDetailTitle(detailTitle);
                    WebInfoDao.insert(info);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Document openLink(String href) {

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(href);
            response = httpClient.execute(httpGet);
            String html = EntityUtils.toString(response.getEntity());
            return Jsoup.parse(html);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
                response.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

        String url = "http://www.lhztb.gov.cn/dahai/Bulltinmore1.aspx?PrjTypeId=01&BulletinTypeId=17";
        Spider.create(new FetchWebInfo3()).addUrl(url).thread(5).start();
    }
}
