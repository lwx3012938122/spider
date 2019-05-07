package bxkc.day2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.jsoup.select.Elements;

/**
 * Jsoup的解析
 */
public class JsoupParser {

    public static void main(String[] args) {

        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = null;
        // 创建实体
        httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://www.sxyc.gov.cn/module/jpage/dataproxy.jsp?startrecord=1&endrecord=45&perpage=15");
        //调用接口
        NameValuePair nameValuePair = new BasicNameValuePair("col", "1");
        NameValuePair appid = new BasicNameValuePair("appid", "1");
        NameValuePair webid = new BasicNameValuePair("webid", "3090");
        NameValuePair path = new BasicNameValuePair("path", "/");
        NameValuePair columnid = new BasicNameValuePair("columnid", "1559772");
        NameValuePair sourceContentType = new BasicNameValuePair("sourceContentType", "1");
        NameValuePair unitid = new BasicNameValuePair("unitid", "4851098");
        NameValuePair webname = new BasicNameValuePair("webname", "越城区人民政府门户网站");
        NameValuePair permissiontype = new BasicNameValuePair("permissiontype", "0");

        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(nameValuePair);
        list.add(appid);
        list.add(webid);
        list.add(path);
        list.add(unitid);
        list.add(columnid);
        list.add(sourceContentType);
        list.add(webname);
        list.add(permissiontype);

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(list, "utf-8"));
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String html = EntityUtils.toString(entity);
            System.out.println(html);
            html = html.replace("<![CDATA[", "").replace("]]", "");
            Document doc = Jsoup.parse(html);
            Element body = doc.body();

            Elements lis = body.select("datastore").select("record");
            //System.out.println(lis);

            for (Element element : lis) {
                String time = element.select("span[class=fr]").text();//时间
                String title = element.select("li").select("a").text();//标题
                System.out.println("标题：" + title + "  时间：" + time);
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
                httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

