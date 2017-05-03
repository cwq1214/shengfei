package sample.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

/**
 * Created by chenweiqi on 2017/4/11.
 */
@DatabaseTable(tableName = "tbl_record")
public class Record {
    @DatabaseField(id = true, canBeNull = false)
    public String uuid;
    @DatabaseField
    public int baseId;
    @DatabaseField
    public String investCode;
    @DatabaseField
    public String baseCode;
    @DatabaseField
    public String hide;
    @DatabaseField
    public String done;
    @DatabaseField
    public String IPA;
    @DatabaseField
    public String note;
    @DatabaseField
    public String spell;
    @DatabaseField
    public String english;
    @DatabaseField
    public String MWFY;
    @DatabaseField
    public String free_trans;
    @DatabaseField
    public String content;
    @DatabaseField
    public String rank;
    @DatabaseField
    public String yun;
    @DatabaseField
    public String createDate;

    public Record() {
    }

//    public Record(String uuid,int baseId, String investCode, String baseCode, String hide, String done, String IPA, String note, String spell, String english, String MWFY,String free_trans, String content, String rank, String yun, String createDate){
//        this(baseId,investCode,baseCode,hide,done,IPA,note,spell,english,MWFY,free_trans,content,rank,yun,createDate);
//        this.uuid = UUID.randomUUID().toString();
//    }

    public Record(int baseId, String investCode, String baseCode, String hide, String done, String IPA, String note, String spell, String english, String MWFY,String free_trans, String content, String rank, String yun, String createDate) {
        this.uuid = UUID.randomUUID().toString();
        this.baseId = baseId;
        this.investCode = investCode;
        this.baseCode = baseCode;
        this.hide = hide;
        this.done = done;
        this.IPA = IPA;
        this.note = note;
        this.spell = spell;
        this.english = english;
        this.MWFY = MWFY;
        this.free_trans = free_trans;
        this.content = content;
        this.rank = rank;
        this.yun = yun;
        this.createDate = createDate;
    }

    public String getFree_trans() {
        return free_trans;
    }

    public void setFree_trans(String free_trans) {
        this.free_trans = free_trans;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getBaseId() {
        return baseId;
    }

    public void setBaseId(int baseId) {
        this.baseId = baseId;
    }

    public String getInvestCode() {
        return investCode;
    }

    public void setInvestCode(String investCode) {
        this.investCode = investCode;
    }

    public String getBaseCode() {
        return baseCode;
    }

    public void setBaseCode(String baseCode) {
        this.baseCode = baseCode;
    }

    public String getHide() {
        return hide;
    }

    public void setHide(String hide) {
        this.hide = hide;
    }

    public String getDone() {
        return done;
    }

    public void setDone(String done) {
        this.done = done;
    }

    public String getIPA() {
        return IPA;
    }

    public void setIPA(String IPA) {
        this.IPA = IPA;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getSpell() {
        return spell;
    }

    public void setSpell(String spell) {
        this.spell = spell;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getMWFY() {
        return MWFY;
    }

    public void setMWFY(String MWFY) {
        this.MWFY = MWFY;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getYun() {
        return yun;
    }

    public void setYun(String yun) {
        this.yun = yun;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
