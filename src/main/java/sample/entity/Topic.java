package sample.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by chenweiqi on 2017/4/11.
 */
@DatabaseTable(tableName = "tbl_topic")
public class Topic {
    @DatabaseField(id = true, canBeNull = false)
    public String uuid;
    @DatabaseField
    public int type;
    @DatabaseField
    public String content;
    @DatabaseField
    public String speakerId;
    @DatabaseField
    public String ipa;
    @DatabaseField
    public String mwfy;
    @DatabaseField
    public String spell;
    @DatabaseField
    public String word_trans;
    @DatabaseField
    public String free_trans;
    @DatabaseField
    public String note;
    @DatabaseField
    public String english;
    @DatabaseField
    public String createDate;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSpeakerId() {
        return speakerId;
    }

    public void setSpeakerId(String speakerId) {
        this.speakerId = speakerId;
    }

    public String getIpa() {
        return ipa;
    }

    public void setIpa(String ipa) {
        this.ipa = ipa;
    }

    public String getMwfy() {
        return mwfy;
    }

    public void setMwfy(String mwfy) {
        this.mwfy = mwfy;
    }

    public String getSpell() {
        return spell;
    }

    public void setSpell(String spell) {
        this.spell = spell;
    }

    public String getWord_trans() {
        return word_trans;
    }

    public void setWord_trans(String word_trans) {
        this.word_trans = word_trans;
    }

    public String getFree_trans() {
        return free_trans;
    }

    public void setFree_trans(String free_trans) {
        this.free_trans = free_trans;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
