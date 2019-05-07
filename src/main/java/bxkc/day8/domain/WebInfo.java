package bxkc.day8.domain;

/**
 * 程序员： 梁伟雄
 * 日期：   2019/5/6 9:17
 * 原网址：http://jyzx.ybq.gov.cn/cqybwz/jyxx/001004/001004010/MoreInfo.aspx?CategoryNum=264200
 * 主页:   http://jyzx.ybq.gov.cn/
 **/

public class WebInfo {

    private String webName;//网站名称
    private String detailLink;//详情页链接
    private String detailTitle;//详情页标题
    private String detailText;//详情页内容
    private String pageTime;//页面时间
    private String createTime;
    private String listTitle;//列表标题
    private String author;


    public String getWebName() {
        return webName;
    }

    public void setWebName(String webName) {
        this.webName = webName;
    }

    public String getDetailLink() {
        return detailLink;
    }

    public void setDetailLink(String detailLink) {
        this.detailLink = detailLink;
    }

    public String getDetailTitle() {
        return detailTitle;
    }

    public void setDetailTitle(String detailTitle) {
        this.detailTitle = detailTitle;
    }

    public String getDetailText() {
        return detailText;
    }

    public void setDetailText(String detailText) {
        this.detailText = detailText;
    }

    public String getPageTime() {
        return pageTime;
    }

    public void setPageTime(String pageTime) {
        this.pageTime = pageTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getListTitle() {
        return listTitle;
    }

    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "WebInfo{" +
                "webName='" + webName + '\'' +
                ", detailLink='" + detailLink + '\'' +
                ", detailTitle='" + detailTitle + '\'' +
                ", detailText='" + detailText + '\'' +
                ", pageTime='" + pageTime + '\'' +
                ", createTime='" + createTime + '\'' +
                ", listTitle='" + listTitle + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
