import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;

public class ElasticsearchTest1 {
    public final static String HOST = "127.0.0.1";//本地地址

    public final static int PORT = 9200;//http请求的端口是9200

    public final static String SCHEME = "http";

    /*
        功能：最普通的搜索，查询某个字段name值为value。
        @param client
        @param name
        @param value
        @return json
    */
    private static String SearchBySearchSourceBuilder(RestHighLevelClient client,String index,String name,String value){
        String result = null;
        //open index
        try {
            OpenIndexRequest request = new OpenIndexRequest(index);
            client.indices().open(request);
        } catch (ElasticsearchStatusException e) {
            return "索引文件打开失败或不存在";
        } catch (IOException e) {
            e.printStackTrace();
        }

        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(value,name);
        multiMatchQueryBuilder.analyzer("ik_max_word");//设置中文分词器
        multiMatchQueryBuilder.type(MultiMatchQueryBuilder.Type.BEST_FIELDS);//设置匹配模式
        multiMatchQueryBuilder.tieBreaker((float)0.3);//匹配系数
        multiMatchQueryBuilder.fuzziness(Fuzziness.AUTO);//开启模糊匹配

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();//实例化搜索资源构建器
        sourceBuilder.query(multiMatchQueryBuilder);//

        SearchRequest searchRequest = new SearchRequest();//add source to SearchRequest
        searchRequest.source(sourceBuilder);

        try {
            SearchResponse searchResponse = client.search(searchRequest);//同步搜索，结果以json的形式返回给searchResponse
            System.out.println("执行getSearch()，查询到结果");
            result = searchResponse.getHits().getHits()[0].getSourceAsString();
        } catch (IOException e) {
            System.out.println("执行getSearch()，查询出错，出错原因如下：");
            e.printStackTrace();
        }

        //close the index
        try {
            CloseIndexRequest closeIndexRequest = new CloseIndexRequest(index);
            client.indices().close(closeIndexRequest);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ElasticsearchStatusException e) {
            return "索引文件关闭失败";
        }

        return result;
    }

    public static void main(String[] args){
        //CREATE A CLIENT
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost(HOST, PORT, SCHEME)));


        //DO SOMETHING START

        String temp = SearchBySearchSourceBuilder(client,"index","content","中国渔船上的嫌犯的校车路权");
        System.out.println(temp);

        //DO SOMETHING DONE

        //cloes the client
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}