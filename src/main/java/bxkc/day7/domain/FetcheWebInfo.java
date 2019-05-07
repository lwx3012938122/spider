package bxkc.day7.domain;

/**
 * 程序员： 梁伟雄
 * 日期：   2019/4/28 10:13
 * 原网址： https://www.lyggzy.com.cn/lyztb/gcjs/081001/081001003/081001003002/
 * 主页:    https://www.lyggzy.com.cn
 **/
public class FetcheWebInfo {

    private String listTime;//列表时间
    private String listTitle;//列表标题
    private String detailContent;//详情内容

    public String getListTime() {
        return listTime;
    }

    public void setListTime(String listTime) {
        this.listTime = listTime;
    }

    public String getListTitle() {
        return listTitle;
    }

    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }

    public String getDetailContent() {
        return detailContent;
    }

    public void setDetailContent(String detailContent) {
        this.detailContent = detailContent;
    }

    @Override
    public String toString() {
        return "domain{" +
                "listTime='" + listTime + '\'' +
                ", listTitle='" + listTitle + '\'' +
                ", detailContent='" + detailContent + '\'' +
                '}';
    }
}
