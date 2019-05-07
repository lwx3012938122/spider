package bxkc.day1;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class Test4 {

    public static void main(String[] args) {

       try {
           //创建实体
           CloseableHttpClient httpClient = HttpClients.createDefault();
           HttpGet httpGet=new HttpGet("https://movie.douban.com/j/search_subjects?type=movie&tag=%E7%83%AD%E9%97%A8&sort=time&page_limit=20&page_start=60");
           CloseableHttpResponse execute = httpClient.execute(httpGet);
           //获取实体
           HttpEntity entity = execute.getEntity();
           String body = EntityUtils.toString(entity,"utf-8");
           System.out.println(body);

       }catch (Exception e){
           e.printStackTrace();
       }
    }
}
