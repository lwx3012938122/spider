package bxkc.day1;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * httpclient的post请求方式
 */
public class Test3 {

    public static void main(String[] args) {
       try{
           CloseableHttpClient httpClient = HttpClients.createDefault();//创建实例
           HttpPost httpPost = new HttpPost("http://www.yaggzy.org.cn/jyxx/jsgcZbgg");//创建get
           //调用接口
           NameValuePair  json = new BasicNameValuePair("currentPage", "2");
           NameValuePair  mobile = new BasicNameValuePair("area","004");
           NameValuePair templateCode = new BasicNameValuePair("secondArea","000");
           NameValuePair ntimestamp = new BasicNameValuePair("industriesTypeCode","000");
           NameValuePair nsignature = new BasicNameValuePair("tenderProjectCode",null);
           NameValuePair nsigName = new BasicNameValuePair("bulletinName",null);

           List<NameValuePair> list = new ArrayList<NameValuePair>();
           list.add(json);
           list.add(mobile);
           list.add(templateCode);
           list.add(ntimestamp);
           list.add(nsignature);
           httpPost.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
           CloseableHttpResponse response = httpClient.execute(httpPost);//执行请求，返回响应对象
           //返回获取数据
           HttpEntity entity = response.getEntity();
           String body = EntityUtils.toString(entity);
           System.out.println(body);

       }catch (Exception e){
           e.printStackTrace();
       }
    }
}
