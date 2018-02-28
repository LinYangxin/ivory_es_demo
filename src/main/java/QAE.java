import java.util.HashMap;
import java.util.Map;

public class QAE {
    private String question;
    private String answer;
    private String evidence;

    public QAE(){

    }
    public QAE(String question,String answer,String evidence){
        setQuestion(question);
        setAnswer(answer);
        setEvidence(evidence);
    }
    public Map<String,Object> getMap(){
        Map<String,Object> jsonMap = new HashMap<String,Object>();
        jsonMap.put("question",question);
        jsonMap.put("answer",answer);
        jsonMap.put("evidence",evidence);
        return jsonMap;
    }
    public String getAnswer() {
        return answer;
    }

    public String getEvidence() {
        return evidence;
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

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }
}
