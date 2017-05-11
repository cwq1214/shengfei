package sample.controller.ZlyydView;

import sample.entity.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bee on 2017/5/8.
 */
public class SameIPABean {
    private String ipa;
    private String baseCode;
    private int count;
    private List<Record> sameRecords;

    public SameIPABean(String ipa, int count,String baseCode) {
        this.ipa = ipa;
        this.count = count;
        this.baseCode = baseCode;
    }

    public String getIpa() {
        return ipa;
    }

    public void setIpa(String ipa) {
        this.ipa = ipa;
    }

    public String getBaseCode() {
        return baseCode;
    }

    public void setBaseCode(String baseCode) {
        this.baseCode = baseCode;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Record> getSameRecords() {
        return sameRecords;
    }

    public void addSameRecords(Record record) {
       if (sameRecords == null){
           sameRecords = new ArrayList<>();
       }
       sameRecords.add(record);
    }
}
