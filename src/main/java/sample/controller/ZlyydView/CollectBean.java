package sample.controller.ZlyydView;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import sample.entity.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bee on 2017/5/5.
 */
public class CollectBean {
    private String key;
    private int count;
    private BooleanProperty isChecked;
    private List<Record> demoRecordList;
    private int type;

    public CollectBean(String key, int count,int type) {
        this.key = key;
        this.count = count;
        this.type = type;
        this.isChecked = new SimpleBooleanProperty(false);
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isIsChecked() {
        return isChecked.get();
    }

    public BooleanProperty isCheckedProperty() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked.set(isChecked);
    }

    public List<Record> getDemoRecordList() {
        return demoRecordList;
    }

    public void addDemoReocrd(Record r){
        if (demoRecordList == null){
            demoRecordList = new ArrayList<>();
        }
        demoRecordList.add(r);
    }
}
