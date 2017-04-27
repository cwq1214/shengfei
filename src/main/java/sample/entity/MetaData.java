package sample.entity;

/**
 * Created by chenweiqi on 2017/4/27.
 */
public class MetaData {
    public String key;
    public String value;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return "MetaData{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
