import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import java.io.IOException;
import java.util.Scanner;

public class ElasticsearchTest1 {
    public final static String HOST = "127.0.0.1";//本地地址

    public final static int PORT = 9200;//http请求的端口是9200

    public final static String SCHEME = "http";

    public static void main(String[] args){
        //CREATE A CLIENT
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost(HOST, PORT, SCHEME)));

        //DO SOMETHING START
        String question = null;
        Scanner in = new Scanner(System.in);
        question = in.nextLine();
        String temp = OperateData.doSearch(client,"qa","question",question);
        OperateData.printData(temp);

        //DO SOMETHING DONE

        //cloes the client
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}