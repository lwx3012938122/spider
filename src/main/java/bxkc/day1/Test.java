package bxkc.day1;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * 下载网页
 */
public class Test {

    public static void getDownloadPage() throws IOException {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://www.baidu.com/");

        CloseableHttpResponse response = httpClient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            //2.获取response实体
            HttpEntity httpEntity=response.getEntity();
            //3.获取io流
            InputStream in=httpEntity.getContent();
            //4.
            Scanner scanner=new Scanner(in);
            String fileName="test.txt";
            PrintWriter printWriter=new PrintWriter(fileName);
            while (scanner.hasNext()){
                printWriter.write(scanner.nextLine());
            }
            scanner.close();
            in.close();
            printWriter.close();
            response.close();
        }
    }

    public static void main(String[] args) {
        try{
            getDownloadPage();
        }catch (IOException io){
            io.getStackTrace();
        }
    }
}
