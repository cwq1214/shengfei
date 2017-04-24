package sample.controller.YBCC;

import sample.entity.Record;

/**
 * Created by Bee on 2017/4/20.
 */
public class YBCCBean {
    private Record record;
    private String wrongReason;

    public YBCCBean(Record record) {
        this.record = record;
        this.wrongReason = "";
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public String getWrongReason() {
        return wrongReason;
    }

    public void setWrongReason(String wrongReason) {
        this.wrongReason = wrongReason;
    }
}
