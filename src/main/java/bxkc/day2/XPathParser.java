package bxkc.day2;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * xpath的解析
 */
public class XPathParser {

    public static void main(String[] args) throws IOException {

        CloseableHttpClient httpClient =null;
        CloseableHttpResponse response = null;
        httpClient = HttpClients.createDefault();

        HttpPost httpPost=new HttpPost("http://www.sxyc.gov.cn/module/jpage/dataproxy.jsp?startrecord=1&endrecord=45&perpage=15");

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

        try{
            httpPost.setEntity(new UrlEncodedFormEntity(list,"utf-8"));
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            System.out.println("status:"+response.getStatusLine().getStatusCode());
            InputStream content = entity.getContent();

            System.out.println("content:"+content.toString());
            //实例化DocumentBuilderFactory，获取Document对象
            DocumentBuilderFactory builderFactory=DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();

            Document doc = builder.parse(content);

            System.out.println("doc:"+doc);
            //实例化XPathFactory对象，获取XPath对象
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression compile = xPath.compile("record");//选取节点
            NodeList nodeLists = (NodeList) compile.evaluate(doc, XPathConstants.NODESET);//获取所有的节点
            System.out.println(nodeLists.toString());

        }catch (Exception e){
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
