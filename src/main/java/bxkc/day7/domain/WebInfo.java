package bxkc.day7.domain;

/**
 * 程序员： Administrator
 * 日期：   2019/4/26 16:32
 * 原网址：
 * 主页:
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
