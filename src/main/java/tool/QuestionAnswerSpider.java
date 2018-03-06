package tool;

import org.json.JSONException;
import org.json.JSONObject;
import pojo.QA;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//处理网站的信息，扒出问答语料
public class QuestionAnswerSpider {
    private static final String PAGEENCODE = "gb2312";

    //获取网站源码
    private static String getPageSource(String pageUrl){
        StringBuffer sb = new StringBuffer();
        try {
            //构建一URL对象
            URL url = new URL(pageUrl);
            //使用openStream得到一输入流并由此构造一个BufferedReader对象
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), PAGEENCODE));
            String line;
            //读取www资源
            while ((line = in.readLine()) != null)
            {
                sb.append(line);
            }
            in.close();
        }
        catch (Exception ex)
        {
            System.err.println(ex);
        }
        return sb.toString();
    }

    //获取主页的url链接
    private static ArrayList<String> getURL(String website,String index){
        ArrayList<String> result = new ArrayList<String>();
        String source = getPageSource(website+index);
        int begin = source.indexOf("<ul class=\"conRight_text_ul1 l1\">");
        int end = source.indexOf("</ul>",begin);
        Pattern r = Pattern.compile("(?<=href=\\\").*?(?=\\\")");
        Matcher m = r.matcher(source.substring(begin,end));
        do{
            if(m.find()){
                String temp = m.group(0);
                String result_url = website + temp.substring(2);
                result.add(result_url);
//                System.out.println(result_url);
                begin = source.indexOf(temp) + temp.length();
                m = r.matcher(source.substring(begin,end));
            }else
                break;
        }while(true);
        System.out.println("执行getURL，共获得"+result.size()+"个URL链接");
        return result;
    }

    //根据具体URL链接，获取对应问题与答案
    private static ArrayList<QA> getQA(ArrayList<String> urls,String lable,ArrayList<QA> result){

        for(int i = 0;i<urls.size();i++){
            System.out.println("正在处理第"+ i +"/"+urls.size()+"条数据");
            String source = getPageSource(urls.get(i));
            Pattern r = Pattern.compile("(?<=h4>).*?(?=</h4)");
            Matcher m = r.matcher(source);
            m.find();
            String question = m.group(0);
            String answer = "";
            int begin = source.indexOf("<div class=\"nr\">");
            int end = source.indexOf("<script type=\"text/javascript\">",begin);
            answer = delHTMLTag(source.substring(begin,end));
            QA temp = new QA(question,answer,lable);
            result.add(temp);
        }
        return result;
    }

    public static String delHTMLTag(String htmlStr) {
        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
        String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
        String regEx_space = "\\s*|\t|\r|\n";//定义空格回车换行符
        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); // 过滤script标签
        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); // 过滤style标签
        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); // 过滤html标签
        Pattern p_space = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE);
        Matcher m_space = p_space.matcher(htmlStr);
        htmlStr = m_space.replaceAll(""); // 过滤空格回车标签
        htmlStr = htmlStr.trim(); // 返回文本字符串
        htmlStr = htmlStr.replaceAll(" ", "");
        return htmlStr;
    }

    private static String getJson(ArrayList<QA> qas){
        JSONObject jsonObject = new JSONObject();
        for(int i = 0;i<qas.size();i++){
            try {
                jsonObject.put(Integer.toString(i),qas.get(i).getMap());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

//    public static void main(String arc[]) {
////        ArrayList<QA> result = new ArrayList<QA>();
////            System.out.println("文件名");
////            Scanner in = new Scanner(System.in);
////            String website, index, lable,name;
////            //website = in.nextLine();
////               // website=website.trim();
////            website = "http://www.szhrss.gov.cn/zmhd/cjwt/gwyyw/gzryzy/";//需要修改
////                name = in.nextLine();
////                lable = "公职人员业务";  ///需要修改
////                if (name.contentEquals("000"))
////                    index = "";
////                else
////                    index = "index_"+name+".htm";
////            System.out.println("正在添加纪录");
////            getQA(getURL(website, index), lable, result);
////            System.out.println("记录添加完成");
////        System.out.println("共有"+result.size()+"条记录");
////        String json = getJson(result);
////        try {
////            if (name.contentEquals("000"))
////                name = "0";
////            File file = new File("C:\\Users\\user\\Desktop\\zy"+name+"_data.json"); //需要修改
////            Writer write = null;
////            write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
////            write.write(json);
////            write.flush();
////            write.close();
////        } catch (Exception e){
////            e.printStackTrace();
////        }
//    }
}
