package bxkc.day1;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class YaAn {

    public static void main(String[] args) throws IOException {

        Connection connect = Jsoup.connect("http://www.yaggzy.org.cn/jyxx/jsgcZbgg");
        Document document = connect.get();
        //System.out.println(document);

        //获取当前页数
        int totalNum = Integer.parseInt(document.select("div[class=mmggxlh]").select("a[class=cur]").text().replace("\\D",""));
        // System.out.println(totalNum);


        Element body = document.body();
    }
}
