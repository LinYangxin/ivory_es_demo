package insurance;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Answer {
    private String answer;
    private String id;
    public Answer(){

    }
    public Answer( String answer , String id){
        setAnswer(answer);
        setId(id);
    }
    public Map<String,Object> getMap(){
        Map<String,Object> jsonMap = new HashMap<String,Object>();
        jsonMap.put("answer",answer);
//        jsonMap.put("id",id);
        return jsonMap;
    }

    public String getId() {
        return id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setId(String id) {
        this.id = id;
    }
}
