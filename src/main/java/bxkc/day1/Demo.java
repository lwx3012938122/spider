package bxkc.day1;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Demo {

    //请求地址;
    private static final String URL = "http://www.tooopen.com/view/1439719.html";
    //获取image的正则表达式
    private static final String IMGURL_REG = "<img.*src(.*?)[^>]*?>";
    //获取src正则表达式
    private static final String IMGSRC_REG = "[a-zA-z]+://[^\\s]*";

    public static void main(String[] args) {

        Demo demo = new Demo();

        //获取html内容
        String htmlText = demo.getHtml(URL);
        System.out.println("html:"+htmlText);
        //获取图片src标签
        List<String> imageSrc = demo.getImageSrc(htmlText);
        System.out.println("imageSrc:"+imageSrc);
        //获取图片src
        List<String> src = demo.getSrc(imageSrc);
        //下载图片
        demo.download(src);
    }

    //获取html内容
    private String getHtml(String url) {

        URL uri=null;
        InputStream in=null;
        URLConnection urlConnection=null;
        InputStreamReader ins=null;
        BufferedReader br=null;
        try {
            uri = new URL(url);
            urlConnection = uri.openConnection();
            in = urlConnection.getInputStream();
            ins = new InputStreamReader(in);
            br = new BufferedReader(ins);

            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line=br.readLine()) != null) {
                sb.append(line, 0, line.length());
                sb.append('\n');
            }
            System.out.println("sb:"+sb.toString());

            in.close();
            ins.close();
            br.close();

            return sb.toString();
        } catch (Exception e ) {
            e.printStackTrace();
        }
        return "";
    }

    //获取src的标签
    private List<String> getImageSrc(String html) {

        Matcher matcher = Pattern.compile(IMGURL_REG).matcher(html);
        List<String> list = new ArrayList<String>();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;
    }

    //获取scr地址
    private List<String> getSrc(List<String> imagesSrc) {

        List<String> list = new ArrayList<String>();
        for (String src : imagesSrc) {
            Matcher matcher = Pattern.compile(IMGSRC_REG).matcher(src);
            while (matcher.find()) {
                list.add(matcher.group().substring(0, matcher.group().length() - 1));
            }
        }
        return list;
    }

    //下载图片
    private void download(List<String> srcs) {

        CloseableHttpClient httpClient = null;
        InputStream content = null;
        try {
            for (String src : srcs) {
                httpClient = HttpClients.createDefault();//创建实例
                HttpGet httpGet = new HttpGet(src);//创建get
                CloseableHttpResponse response = httpClient.execute(httpGet);//执行请求，返回响应对象
                HttpEntity entity = response.getEntity();//获取实体
                if (entity != null) {
                    content = entity.getContent();
                    FileUtils.copyToFile(content, new File("D:\\Downloads"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
}
