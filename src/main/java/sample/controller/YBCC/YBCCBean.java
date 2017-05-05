package sample.controller.YBCC;

import sample.entity.Record;
import sample.util.Constant;
import sample.util.FileUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bee on 2017/4/20.
 */
public class YBCCBean {
    private Record record;
    private String wrongReason;
    private String demoVideoLoc;
    private String demoPicLoc;

    //声母 韵母 声调 变调
    private List smList;
    private List ymList;
    private List sdList;

    public YBCCBean(Record record) {
        this.record = record;
        this.wrongReason = "";

        //TODO 根据record里的uuid查询获得demoVideoLoc和demoPicLoc
        demoVideoLoc = FileUtil.getFullLocation(Constant.DEMO_Video_DIR,record.getUuid());
        demoPicLoc = FileUtil.getFullLocation(Constant.DEMO_PIC_DIR,record.getUuid());
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

    public String getDemoVideoLoc() {
        return demoVideoLoc;
    }

    public void setDemoVideoLoc(String demoVideoLoc) {
        this.demoVideoLoc = demoVideoLoc;
    }

    public String getDemoPicLoc() {
        return demoPicLoc;
    }

    public void setDemoPicLoc(String demoPicLoc) {
        this.demoPicLoc = demoPicLoc;
    }

    public List getSmList() {
        return smList;
    }

    public List getYmList() {
        return ymList;
    }

    public List getSdList() {
        return sdList;
    }

    public void addSm(String sm){
        if (smList == null){
            smList = new ArrayList();
        }
        smList.add(sm);
    }

    public void addYm(String ym){
        if (ymList == null){
            ymList = new ArrayList();
        }
        ymList.add(ym);
    }

    public void addSd(String sd){
        if (sdList == null){
            sdList = new ArrayList();
        }
        sdList.add(sd);
    }

}
