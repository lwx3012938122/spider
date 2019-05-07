package bxkc.day5;

import bxkc.day5.dao.WebInfoDao;
import bxkc.day5.domain.WebInfo;
import bxkc.day5.util.PersistenceInfo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.HttpConstant;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 程序员： Administrator
 * 日期：   2019/4/29 13:37
 * 原网址： http://ggzyjy.qinzhou.gov.cn/gxqzzbw/jyxx/001001/001001001/MoreInfo.aspx?CategoryNum=001001001
 * 主页:    http://ggzyjy.qinzhou.gov.cn
 **/
public class FetchWebInfo implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
    private Pattern pattern=Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private WebInfo info = new WebInfo();
    private Connection conn = null;
    private PreparedStatement ps = null;
    private int count=1;//记录页数

    public void process(Page page) {

        try {
            Document doc = page.getHtml().getDocument();
            conn = PersistenceInfo.getConnection();
            //解析列表
            if (page.getUrl().toString().contains("MoreInfo")) {
                Elements detailLinks = doc.select("[id='MoreInfoList1_DataGrid1'] tr td a");
                System.out.println("第 "+count+" 页");
                for (Element detailLink : detailLinks) {
                    String href = "http://ggzyjy.qinzhou.gov.cn" + detailLink.attr("href");
                    String listTitle = detailLink.text();
                    String souce_name = "广西钦州市公共资源交易中心";
                    boolean byDetailLink = WebInfoDao.findByDetailLink(href);
                    if (!byDetailLink) {
                        info.setDetailLink(href);
                        info.setListTitle(listTitle);
                        info.setWebName(souce_name);
                        WebInfoDao.insert(info);//插入数据

                        page.addTargetRequest(href);
                    }
                }
                //解析翻页
                String nextLink = doc.select("a img[src~=(.*)nextn.gif]").attr("src");
                System.out.println("nextLink:"+nextLink);
                if(nextLink !=null){
                    count++;
                    page.addTargetRequest(getRequest(doc,count));
                }
            } else {//解析详情
                String detailTitle = doc.select("[id='lblTitle']").html();
                System.out.println("detailTitle:"+detailTitle);
                String detailContent = doc.select("[id='Table4']").html();
                String detailTime = doc.select("[id='tblInfo'] tr td font[class='webfont']").text();
                Matcher matcher = pattern.matcher(detailTime);
                while (matcher.find()){
                    detailTime=matcher.group();
                }
                String detailLink = page.getUrl().toString();
                info.setDetailLink(detailLink);
                info.setDetailText(detailContent);
                info.setPageTime(detailTime);
                info.setDetailTitle(detailTitle);

                //WebInfoDao.update(info);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                //ps.close();
                conn.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static Request getRequest(Document doc,int count) {

        Request request=new Request("http://ggzyjy.qinzhou.gov.cn/gxqzzbw/jyxx/001001/001001001/MoreInfo.aspx?CategoryNum=001001001");
        request.setMethod(HttpConstant.Method.POST);
        Map<String,Object> map=new HashMap<String, Object>();
        //map.put("__CSRFTOKEN","/wEFJDU4YzliZWJjLTc2MDAtNGVkYy1iNjhiLThmMzZjOTM2OTg3Mg==");
        Element viewstate = doc.getElementById("__VIEWSTATE");
        map.put("__VIEWSTATE",viewstate.attr("value"));
        map.put("__EVENTTARGET","MoreInfoList1$Pager");
        map.put("__EVENTARGUMENT",count);
        map.put("__VIEWSTATEENCRYPTED","");

        request.setRequestBody(HttpRequestBody.form(map,"utf-8"));
        return request;
    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new FetchWebInfo()).addUrl("http://ggzyjy.qinzhou.gov.cn/gxqzzbw/jyxx/001001/001001001/MoreInfo.aspx?CategoryNum=001001001").thread(5).start();
    }
}
