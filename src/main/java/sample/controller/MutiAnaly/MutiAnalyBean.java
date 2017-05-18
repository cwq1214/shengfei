package sample.controller.MutiAnaly;

import sample.entity.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bee on 2017/5/9.
 */
public class MutiAnalyBean {
    private Record baseRecord;
    private List<Record> otherIPA;

    public MutiAnalyBean() {
        otherIPA = new ArrayList<>();
    }

    public Record getBaseRecord() {
        return baseRecord;
    }

    public void setBaseRecord(Record baseRecord) {
        this.baseRecord = baseRecord;
    }

    public List<Record> getOtherIPA() {
        return otherIPA;
    }

    public void addOtherIPA(Record r) {
        this.otherIPA.add(r);
    }
}
