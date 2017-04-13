package sample.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by chenweiqi on 2017/4/11.
 */
@DatabaseTable(tableName = "tbl_code_base")
public class CodeBase {
    @DatabaseField(id = true, canBeNull = false)
    public String code;
    @DatabaseField
    public Integer codeType;
    @DatabaseField
    public String content;
    @DatabaseField
    public String rank;
    @DatabaseField
    public String yun;
    @DatabaseField
    public String spell;
    @DatabaseField
    public String IPA;
    @DatabaseField
    public String english;
    @DatabaseField
    public String note;
    @DatabaseField
    public String mwfy;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getCodeType() {
        return codeType;
    }

    public void setCodeType(Integer codeType) {
        this.codeType = codeType;
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

    public String getSpell() {
        return spell;
    }

    public void setSpell(String spell) {
        this.spell = spell;
    }

    public String getIPA() {
        return IPA;
    }

    public void setIPA(String IPA) {
        this.IPA = IPA;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getMwfy() {
        return mwfy;
    }

    public void setMwfy(String mwfy) {
        this.mwfy = mwfy;
    }
}
