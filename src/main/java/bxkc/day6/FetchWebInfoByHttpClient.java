package bxkc.day6;

import bxkc.day6.dao.FetchWebInfoDao;
import bxkc.day6.domain.FetcheWebInfo;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.SerializableEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 程序员： 梁伟雄
 * 日期：   2019/4/28 10:13
 * 原网址： http://www.jnggzyjy.gov.cn/GaoXinQu/Bulletins?CategoryCode=503000
 * 主页:    http://www.jnggzyjy.gov.cn/
 **/
public class FetchWebInfoByHttpClient {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
    private static Map<String, FetcheWebInfo> map = new HashMap<String, FetcheWebInfo>();
    private static Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static int skipCount = 0;
    private static int count = 1;

    public static void main(String[] args) {
        try {
            while (count < 5) {
                CloseableHttpClient httpClient = HttpClients.createDefault();
                HttpPost httpPost = new HttpPost("http://www.jnggzyjy.gov.cn/api/services/app/stPrtBulletin/GetBulletinList");

                String str = "{\"skipCount\":" + skipCount + ",\"maxResultCount\":20,\"tenantId\":\"18\",\"categoryCode\":\"503000\",\"siteId\":\"4\",\"regionId\":\"4\",\"FilterText\":\"\"}";
                httpPost.setHeader("Content-Type", "application/json");//设置请求头
                StringEntity se = new StringEntity(str);//设置Json的请求实体
                httpPost.setEntity(se);

                CloseableHttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                String stringEntity = EntityUtils.toString(entity);
                JSONObject object = new JSONObject(stringEntity.toString());
                JSONObject result = object.getJSONObject("result");
                JSONArray items = result.getJSONArray("items");

                System.out.println("第 " + count + " 页");
                for (int i = 0; i < items.length(); i++) {//列表
                    JSONObject jsonObject = items.getJSONObject(i);
                    String listTitle = (String) jsonObject.get("title");
                    String listTime = (String) jsonObject.get("startDate");
                    Matcher matcher = pattern.matcher(listTime);
                    while (matcher.find()) {
                        listTime = matcher.group();
                    }
                    String id = (String) jsonObject.get("id");
                    String detailLink = "http://www.jnggzyjy.gov.cn/GaoXinQu/Bulletins/Detail/" + id + "/?CategoryCode=503000";
                    //System.out.println(detailLink);
                    //创建对象
                    FetcheWebInfo info = new FetcheWebInfo();
                    info.setListTitle(listTitle);
                    info.setListTime(listTime);

                    map.put(detailLink, info);
                }

                Set<String> detailLinks = map.keySet();
                for (String href : detailLinks) {//详情

                    //添加get请求
                    HttpGet httpGet = new HttpGet(href);
                    response = httpClient.execute(httpGet);
                    HttpEntity entity1 = response.getEntity();
                    String html = EntityUtils.toString(entity1);
                    Document doc = Jsoup.parse(html);
                    String detailContent = doc.select("[class='panel-body']").html();
                    String detailTitle = doc.select("[class='page-header'] h2").text();

                    boolean flag = FetchWebInfoDao.findBydetailTitle(detailTitle);
                    if (!flag) {//若数据库，查询没有该标题，做新增
                        //传入详情的目标请求，获取对象
                        FetcheWebInfo info = map.get(href);
                        info.setDetailContent(detailContent);
                        FetchWebInfoDao.insert(info);//插入
                    }
                }
                skipCount +=20;
                count++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
