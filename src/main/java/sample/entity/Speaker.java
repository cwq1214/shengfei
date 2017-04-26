package sample.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by chenweiqi on 2017/4/11.
 */
@DatabaseTable(tableName = "tbl_speaker")
public class Speaker {
    @DatabaseField(generatedId = true)
    public Integer ID;
    @DatabaseField
    public String withtable;
    @DatabaseField
    public String speakcode;
    @DatabaseField
    public String realname;
    @DatabaseField
    public String sex;
    @DatabaseField
    public String birth;
    @DatabaseField
    public String usualLang;
    @DatabaseField
    public String motherLang;
    @DatabaseField
    public String secondLang;
    @DatabaseField
    public String education;
    @DatabaseField
    public String job;
    @DatabaseField
    public String workRole;
    @DatabaseField
    public String addr;
    @DatabaseField
    public String notetext;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getWithtable() {
        return withtable;
    }

    public void setWithtable(String withtable) {
        this.withtable = withtable;
    }

    public String getSpeakcode() {
        return speakcode;
    }

    public void setSpeakcode(String speakcode) {
        this.speakcode = speakcode;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getUsualLang() {
        return usualLang;
    }

    public void setUsualLang(String usualLang) {
        this.usualLang = usualLang;
    }

    public String getMotherLang() {
        return motherLang;
    }

    public void setMotherLang(String motherLang) {
        this.motherLang = motherLang;
    }

    public String getSecondLang() {
        return secondLang;
    }

    public void setSecondLang(String secondLang) {
        this.secondLang = secondLang;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getWorkRole() {
        return workRole;
    }

    public void setWorkRole(String workRole) {
        this.workRole = workRole;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getNotetext() {
        return notetext;
    }

    public void setNotetext(String notetext) {
        this.notetext = notetext;
    }

    @Override
    public String toString() {
        return "Speaker{" +
                "ID=" + ID +
                ", withtable='" + withtable + '\'' +
                ", speakcode='" + speakcode + '\'' +
                ", realname='" + realname + '\'' +
                ", sex='" + sex + '\'' +
                ", birth='" + birth + '\'' +
                ", usualLang='" + usualLang + '\'' +
                ", motherLang='" + motherLang + '\'' +
                ", secondLang='" + secondLang + '\'' +
                ", education='" + education + '\'' +
                ", job='" + job + '\'' +
                ", workRole='" + workRole + '\'' +
                ", addr='" + addr + '\'' +
                ", notetext='" + notetext + '\'' +
                '}';
    }
}
