package bxkc.day2;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsoupParser2 {

    public static void main(String[] args) {

        int page=1;
        while (true){
            CloseableHttpClient httpClient = null;
            CloseableHttpResponse response = null;
            try {
                httpClient = HttpClients.createDefault();
                HttpPost httpPost = new HttpPost("http://www.yaggzy.org.cn/jyxx/jsgcZbgg");

                NameValuePair currentPage = new BasicNameValuePair("currentPage", page+"");
                NameValuePair area = new BasicNameValuePair("area", "004");
                NameValuePair secondArea = new BasicNameValuePair("secondArea", "000");
                NameValuePair industriesTypeCode = new BasicNameValuePair("industriesTypeCode", "000");
                NameValuePair tenderProjectCode = new BasicNameValuePair("tenderProjectCode", null);
                NameValuePair bulletinName = new BasicNameValuePair("bulletinName", null);

                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(currentPage);
                list.add(area);
                list.add(secondArea);
                list.add(industriesTypeCode);
                list.add(tenderProjectCode);
                list.add(bulletinName);
                httpPost.setEntity(new UrlEncodedFormEntity(list, "utf-8"));
                response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                String html = EntityUtils.toString(entity);

                //System.out.println(html);
                Document doc = Jsoup.parse(html);
                Elements trs = doc.select(".clearfloat tr");
                //System.out.println(td);
                for (Element tr : trs) {

                    String href = tr.select("td a").attr("href");//链接
                    String text = tr.select("td a").text();//标题
                    String time = tr.select("td:matchesOwn((\\d{4})-(\\d{2})-(\\d{2}))").text();

                    if(!href.equals("")||!text.equals("")||!time.equals("")){
                    System.out.println("href:" + href);
                    System.out.println("text:" + text);
                    System.out.println("time:" + time);
                    System.out.println();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println("页数："+page);
                page++;
                try {
                    response.close();
                    httpClient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
