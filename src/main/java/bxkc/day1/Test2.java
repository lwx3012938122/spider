package bxkc.day1;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * httpclient 的get请求方式
 */
public class Test2 {

    public static void main(String[] args) throws IOException {

        CloseableHttpClient httpClient = HttpClients.createDefault();//创建实例
        HttpGet httpGet = new HttpGet("http://static.bootcss.com/www/assets/img/codeguide.png?1505127079951");//创建get
        CloseableHttpResponse response = httpClient.execute(httpGet);//执行请求，返回响应对象
        HttpEntity entity = response.getEntity();//获取实体
        if (entity != null) {
            final InputStream content = entity.getContent();
            FileUtils.copyToFile(content, new File("D://Downloads/a.png"));
        }
    }
}
