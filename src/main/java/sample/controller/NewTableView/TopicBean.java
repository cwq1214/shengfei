package sample.controller.NewTableView;

/**
 * Created by Bee on 2017/4/28.
 */
public class TopicBean {
    private String code;
    private String topic;
    private String english;

    public TopicBean(String code, String topic, String english) {
        this.code = code;
        this.topic = topic;
        this.english = english;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }
}
