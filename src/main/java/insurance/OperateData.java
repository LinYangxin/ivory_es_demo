import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class OperateData {

    //{other-insurance=1, medicare-insurance=1, disability-insurance=1, health-insurance=1, home-insurance=1, long-term-care-insurance=1, annuities=1, auto-insurance=1, critical-illness-insurance=1, life-insurance=1, retirement-plans=1, renters-insurance=1}

    public static ArrayList<QAE> initQuestionDataByJson(String path) {
        ArrayList<QAE> result = new ArrayList<QAE>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
            String jsonData = bufferedReader.readLine();
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray question_id = jsonObject.names();//获取每个问题的ID
            for (int i = 0; i < question_id.length(); i++) {
                JSONObject tmp = jsonObject.getJSONObject(question_id.getString(i));
                String question = tmp.getString("zh");//question->zh
              //  String answer = tmp.getJSONObject("evidences").getJSONObject(question_id.getString(i) + "#00").getString("answer");
              //  String evidence = tmp.getJSONObject("evidences").getJSONObject(question_id.getString(i) + "#00").getString("evidence");
                String answer = tmp.getString("answers");
                String domain = tmp.getString("domain");
                QAE qae = new QAE(question, answer, domain,question_id.getString(i));
                result.add(qae);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void addQuestionDoc(RestHighLevelClient client, ArrayList<QAE> qaes) {
        for (int i = 0; i < qaes.size(); i++) {
            IndexRequest indexRequest = new IndexRequest("insurance", "insurance_question_answer",qaes.get(i).getId()).source(qaes.get(i).getMap());
            try {
                System.out.println("正在添加第" + i + "/" + qaes.size() + "个");
                IndexResponse indexResponse = client.index(indexRequest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static ArrayList<Answer> initAnswerByJson(String path) {
        ArrayList<Answer> result = new ArrayList<Answer>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
            String jsonData = bufferedReader.readLine();
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray question_id = jsonObject.names();//获取每个问题的ID
            for (int i = 0; i < question_id.length(); i++) {
                JSONObject tmp = jsonObject.getJSONObject(question_id.getString(i));
                String answer = tmp.getString("zh");
                Answer ans = new Answer(answer,question_id.getString(i));
                result.add(ans);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void addAnswerDoc(RestHighLevelClient client, ArrayList<Answer> qaes) {
        for (int i = 0; i < qaes.size(); i++) {
            IndexRequest indexRequest = new IndexRequest("answers", "insurance_answer",qaes.get(i).getId()).source(qaes.get(i).getMap());
            try {
                System.out.println("正在添加第" + i + "/" + qaes.size() + "个");
                IndexResponse indexResponse = client.index(indexRequest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String doSearch(RestHighLevelClient client, String index, String filename, String value) {
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

        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(value, filename);
        multiMatchQueryBuilder.analyzer("ik_smart");//设置中文分词器
        multiMatchQueryBuilder.type(MultiMatchQueryBuilder.Type.BEST_FIELDS);//设置匹配模式

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();//实例化搜索资源构建器
        sourceBuilder.query(multiMatchQueryBuilder);//

        SearchRequest searchRequest = new SearchRequest();//add source to SearchRequest
        searchRequest.source(sourceBuilder);

        try {
            SearchResponse searchResponse = client.search(searchRequest);//同步搜索，结果以json的形式返回给searchResponse
            System.out.println("执行doSearch()，查询到结果");
            result = searchResponse.toString();//.getHits()//.getHits()[0].getClusterAlias().toString();//.getSourceAsString();
        } catch (IOException e) {
            System.out.println("执行doSearch()，查询出错，出错原因如下：");
            e.printStackTrace();
        }

        return result;
    }

    public static void printData(String json) {
        try {
            JSONObject response = new JSONObject(json);
            response = response.getJSONObject("hits");
            JSONArray hits = response.getJSONArray("hits");
            response = hits.getJSONObject(0);

            String score = response.getString("_score");
            JSONObject result = response.getJSONObject("_source");
            String result_question = result.getString("question");
            String result_answer = result.getString("answer");
            String result_evidence = result.getString("evidence");
            if (result_answer == "[\"no_answer\"]")
                System.out.println("查无答案");
            else {
                System.out.println("问题：" + result_question);
                System.out.println("得分：" + score);
                System.out.println("答案：" + result_answer);
                System.out.println("理由：" + result_evidence);
            }
        } catch (JSONException e) {
            // e.printStackTrace();
            System.out.println("查无此问题");
        }
    }

    public static void delete(RestHighLevelClient client, String index, String filename, String value) {
        MatchQueryBuilder matchQuery = QueryBuilders.matchQuery(filename, value);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();//实例化搜索资源构建器
        sourceBuilder.query(matchQuery);//
        sourceBuilder.size(10000);
        SearchRequest searchRequest = new SearchRequest();//add source to SearchRequest
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse searchResponse = client.search(searchRequest);//同步搜索，结果以json的形式返回给searchResponse
            SearchHit[] searchHit = searchResponse.getHits().getHits();
            System.out.println("共有" + searchHit.length + "待删除");
            int i = 1;
            for (SearchHit temp : searchHit) {
                System.out.println("正在删除" + (i++) + "/" + searchHit.length);
                DeleteRequest deleteRequest = new DeleteRequest("qa", "question_answer", temp.getId());
                client.delete(deleteRequest);
            }
            System.out.println("删除完成");
        } catch (IOException e) {
            System.out.println("执行delete()出错，出错原因如下：");
            e.printStackTrace();
        }
    }
}
