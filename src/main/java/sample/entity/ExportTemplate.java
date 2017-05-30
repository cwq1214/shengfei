package sample.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Bee on 2017/5/29.
 */

@DatabaseTable(tableName = "export_template")
public class ExportTemplate {
    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField
    public String name;
    @DatabaseField
    public String learnLang;
    @DatabaseField
    public String appKind;
    @DatabaseField
    public String mediaKind;
    @DatabaseField
    public String difficult;
    @DatabaseField
    public String lessonName;
    @DatabaseField
    public String fDir;
    @DatabaseField
    public String sDir;

    public ExportTemplate() {

    }

    public ExportTemplate(String name, String learnLang, String appKind, String mediaKind, String difficult, String lessonName, String fDir, String sDir) {
        this.name = name;
        this.learnLang = learnLang;
        this.appKind = appKind;
        this.mediaKind = mediaKind;
        this.difficult = difficult;
        this.lessonName = lessonName;
        this.fDir = fDir;
        this.sDir = sDir;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLearnLang() {
        return learnLang;
    }

    public void setLearnLang(String learnLang) {
        this.learnLang = learnLang;
    }

    public String getAppKind() {
        return appKind;
    }

    public void setAppKind(String appKind) {
        this.appKind = appKind;
    }

    public String getMediaKind() {
        return mediaKind;
    }

    public void setMediaKind(String mediaKind) {
        this.mediaKind = mediaKind;
    }

    public String getDifficult() {
        return difficult;
    }

    public void setDifficult(String difficult) {
        this.difficult = difficult;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getfDir() {
        return fDir;
    }

    public void setfDir(String fDir) {
        this.fDir = fDir;
    }

    public String getsDir() {
        return sDir;
    }

    public void setsDir(String sDir) {
        this.sDir = sDir;
    }
}
