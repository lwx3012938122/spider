package bxkc.day7;

import bxkc.day7.dao.WebInfoDao;
import bxkc.day7.domain.WebInfo;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 程序员： 梁伟雄
 * 日期：   2019/5/5 15:16
 * 原网址：http://www.nanan.gov.cn/zwgk/ztzl/ggzyjy/
 * 主页:   http://www.nanan.gov.cn/
 **/
public class FetchWebInfo2 implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
    private Map<String, WebInfo> map = new HashMap<String, WebInfo>();
    private static int count = 1;
    private static int pageIndex = 1;
    private static Spider spider;

    public void process(Page page) {

        try {
            if (page.getUrl().toString().contains("getTenderInfoPage")) {
                JSONObject object = new JSONObject(page.getRawText()).getJSONObject("data");

                JSONArray datalist = object.getJSONArray("datalist");
                for (int i = 0; i < datalist.length(); i++) {
                    JSONObject jsonObject = datalist.getJSONObject(i);
                    String listTime = jsonObject.getString("sendTime");
                    String listTitle = jsonObject.getString("proj_name");
                    String listId = jsonObject.getString("tenderProjCode");

                    String detailLink = "http://120.33.41.196/hyweb/transInfo/getProjBuildNoticeById.do?tenderProjCode=" + jsonObject.getString("tenderProjCode");

                    ///System.out.println("listTime:" + listTime);
                    WebInfo info = new WebInfo();
                    info.setPageTime(listTime);
                    info.setListTitle(listTitle);
                    info.setWebName("南安人民政府门户网站");
                    info.setDetailLink(detailLink);
                    map.put(detailLink, info);

                    boolean byDetailLink = WebInfoDao.findByDetailLink(detailLink);
                    if (!byDetailLink) {//添加详情页的目标请求
                        page.addTargetRequest(getDetailRequest(listId));
                    }
                }
                //翻页解析
                int pageindex = object.getInt("pageindex");//开始数
                int pagecount = object.getInt("pagecount");//总数

                if (pageindex<pagecount) {//存在下一页
                    count++;
                    page.addTargetRequest(getRequest(count));
                }

            } else {

                JSONArray jsonArray = new JSONObject(page.getRawText())
                        .getJSONObject("data").getJSONArray("noticeList");
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String detailContent = jsonObject.getString("content");
                    String detailTitle = jsonObject.getString("noticeTitle");

                    String tenderProjCode = jsonObject.getString("tenderProjCode");
                    String detailLink="http://120.33.41.196/hyweb/transInfo/getProjBuildNoticeById.do?tenderProjCode="+tenderProjCode;

                    WebInfo info = map.get(detailLink);//获取对象
                    info.setDetailTitle(detailTitle);
                    info.setDetailText(detailContent);
                    WebInfoDao.insert(info);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Site getSite() {
        return this.site;
    }

    public static void main(String[] args) {
        Spider.create(new FetchWebInfo2()).addRequest(getRequest(pageIndex)).thread(5).start();
    }

    private static Request getRequest(int pageIndex) {

        Request request = null;
        try {
            request = new Request("http://120.33.41.196/hyweb/transInfo/getTenderInfoPage.do");
            request.setMethod(HttpConstant.Method.POST);
            request.addHeader("Content-Type", "application/json");

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("pageIndex", pageIndex);
            map.put("pageSize", "10");
            map.put("noticeTitle", "");
            map.put("regionCode", "350500");
            map.put("tenderType", "A");
            map.put("pubTime", "");
            map.put("state", "");
            map.put("noticeType", "1");
            map.put("tradeCode", "1");

            Gson gson = new Gson();
            String jsonStr = gson.toJson(map);
            request.setRequestBody(HttpRequestBody.json(jsonStr, "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return request;
    }

    private Request getDetailRequest(String tenderProjCode) {

        Request request = null;
        try {
            request = new Request("http://120.33.41.196/hyweb/transInfo/getProjBuildNoticeById.do");
            request.setMethod(HttpConstant.Method.POST);
            request.addHeader("Content-Type", "application/json");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("tenderProjCode", tenderProjCode);
            map.put("noticeType", "1");
            map.put("noticeId", "");
            Gson gson = new Gson();
            String jsonStr = gson.toJson(map);
            request.setRequestBody(HttpRequestBody.json(jsonStr, "utf-8"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return request;
    }
}
