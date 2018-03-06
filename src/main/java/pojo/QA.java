package pojo;

import java.util.HashMap;
import java.util.Map;

public class QA {
    private String question;
    private String answer;
    private String lable;

    public Map<String,String> getMap(){
        Map<String,String> json = new HashMap<String, String>();
        json.put("question",this.getQuestion());
        json.put("answer",this.getAnswer());
        json.put("lable",this.getLable());
        return json;
    }

    public String getAnswer() {
        return answer;
    }

    public String getLable() {
        return lable;
    }

    public String getQuestion() {
        return question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public QA(){

    }

    public QA(String question, String answer, String lable){
        setAnswer(answer);
        setLable(lable);
        setQuestion(question);
    }
}
