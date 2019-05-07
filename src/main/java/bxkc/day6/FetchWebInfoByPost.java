package bxkc.day6;

import bxkc.day6.dao.WebInfoDao;
import bxkc.day6.domain.WebInfo;
import com.google.gson.Gson;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.HttpConstant;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 程序员： 梁伟雄
 * 日期：   2019/4/28 10:13
 * 原网址： http://www.jnggzyjy.gov.cn/GaoXinQu/Bulletins?CategoryCode=503000
 * 主页:    http://www.jnggzyjy.gov.cn/
 **/
public class FetchWebInfoByPost implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
    private Map<String, WebInfo> map = new HashMap<String, WebInfo>();
    private Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static int skipCount = 0;
    private int count = 1;

    public void process(Page page) {

        try {
            Document doc = page.getHtml().getDocument();
            //解析翻页
            if (page.getUrl().toString().contains("GetBulletinList")) {//获取列表
                String stringEntity = page.getRawText();
                JSONObject object = new JSONObject(stringEntity.toString());
                JSONObject result = object.getJSONObject("result");
                JSONArray items = result.getJSONArray("items");
                System.out.println("第 " + count + " 页");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject jsonObject = items.getJSONObject(i);
                    String listTitle = jsonObject.getString("title");
                    //System.out.println("标题：" + listTitle);
                    String listTime = jsonObject.getString("startDate");
                    Matcher matcher = pattern.matcher(listTime);
                    while (matcher.find()) {
                        listTime = matcher.group();
                    }
                    String id = jsonObject.getString("id");
                    String detailLink = "http://www.jnggzyjy.gov.cn/GaoXinQu/Bulletins/Detail/" + id + "/?CategoryCode=503000";
                    //创建对象
                    WebInfo info = new WebInfo();
                    info.setListTitle(listTitle);
                    info.setPageTime(listTime);
                    info.setWebName("济宁市高新区公共资源交易网");
                    info.setDetailLink(detailLink);
                    map.put(detailLink, info);
                    boolean byDetailLink = WebInfoDao.findByDetailLink(detailLink);
                    if (!byDetailLink) {//若数据库，查询没有该标题，做新增
                        page.addTargetRequest(detailLink);
                    }
                }
                if (count < 5) {
                    skipCount += 20;
                    page.addTargetRequest(getRequest(skipCount));
                    count++;
                }

            } else {//详情页
                String detailContent = doc.select("[class='panel-body']").html()
                        .replace(doc.select("[class='bm-side-link']").html(), "");
                String detailTitle = doc.select("[class='page-header'] h2").text();

                //传入详情的目标请求，获取对象
                WebInfo info = map.get(page.getUrl().toString());
                info.setDetailTitle(detailTitle);
                info.setDetailText(detailContent);
                //WebInfoDao.insert(info);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Site getSite() {
        Map map = getCookie();
        int count = 3;
        while (map.size() < 2 && count > 0) {
            map = getCookie();
            count--;
        }
        String cookie = map.get("cookie").toString();
        String token = map.get("token").toString();

        return this.site.addHeader("cookie", cookie)
                .addHeader("Public-X-XSRF-TOKEN", token);
    }

    public static void main(String[] args) {
        Spider.create(new FetchWebInfoByPost()).addRequest(getRequest(skipCount)).thread(3).run();
    }

    public static Request getRequest(int skipCount) {

        System.out.println("skipCount:" + skipCount);
        Request request = new Request("http://www.jnggzyjy.gov.cn/api/services/app/stPrtBulletin/GetBulletinList?time=" + new Date().getTime());
        try {
            request.setMethod(HttpConstant.Method.POST);
            request.addHeader("Content-Type", "application/json");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("FilterText", "");
            map.put("categoryCode", "503000");
            map.put("maxResultCount", 20);
            map.put("regionId", "4");
            map.put("siteId", "4");
            map.put("skipCount", skipCount);
            map.put("tenantId", "18");
            Gson gson = new Gson();
            String jsonStr = gson.toJson(map);
            request.setRequestBody(HttpRequestBody.json(jsonStr, "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return request;
    }

    private Map getCookie() {
        Map<String, String> map = new HashMap<String, String>();
        String url = "http://www.jnggzyjy.gov.cn/GaoXinQu/Bulletins?CategoryCode=503000";
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
//                if (h.getName().equals("Set-Cookie")) {
//                    String v = h.getValue();
//                    if (v.contains("Public-XSRF-TOKEN")) {
//
//                        String token = v.substring(v.indexOf("=") + 1);
//                        map.put("token", token);
//                    }
                map.put(h.getName(), h.getValue());
                sb.append(h.getValue()).append(";");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        cookie = sb.toString();
        map.put("cookie", cookie);
        System.out.println("cookie --> " + cookie);

        return map;
    }

}
