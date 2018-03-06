package insurance;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QAE {
    private String question;
    private String answer;
    private String domain;
    private String id;
    public QAE(){

    }
    public QAE(String question,String answer,String domain,String id){
        setQuestion(question);
        setAnswer(answer);
        setDomain(domain);
        setId(id);
    }
    public Map<String,Object> getMap(){
        Map<String,Object> jsonMap = new HashMap<String,Object>();
        jsonMap.put("question",question);
        jsonMap.put("answer",answer);
        jsonMap.put("domain",domain);
//        jsonMap.put("id",id);
        return jsonMap;
    }

    public String getId() {
        return id;
    }

    public String getAnswer() {
        return answer;
    }

    public String getDomain() {
        return domain;
    }

    public String getQuestion() {
        return question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setId(String id) {
        this.id = id;
    }
}
