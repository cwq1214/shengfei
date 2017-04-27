package sample.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by chenweiqi on 2017/4/11.
 */
@DatabaseTable(tableName = "tbl_table")
public class Table {
    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField
    public String title;
    @DatabaseField
    public String datatype;
    @DatabaseField
    public String projectname;
    @DatabaseField
    public String creator;
    @DatabaseField
    public String speaker;
    @DatabaseField
    public String contributor;
    @DatabaseField
    public String recordingdate;
    @DatabaseField
    public String recordingplace;
    @DatabaseField
    public String language;
    @DatabaseField
    public String languagecode;
    @DatabaseField
    public String languageplace;
    @DatabaseField
    public String datades;
    @DatabaseField
    public String equipment;
    @DatabaseField
    public String software;
    @DatabaseField
    public String rightl;
    @DatabaseField
    public String snote;
    @DatabaseField
    public String custom;

    public Table() {
    }

    public Table(String title, String datatype, String projectname, String creator, String speaker, String contributor, String recordingdate, String recordingplace, String language, String languagecode, String languageplace, String datades, String equipment, String software, String rightl, String snote, String custom) {
        this.title = title;
        this.datatype = datatype;
        this.projectname = projectname;
        this.creator = creator;
        this.speaker = speaker;
        this.contributor = contributor;
        this.recordingdate = recordingdate;
        this.recordingplace = recordingplace;
        this.language = language;
        this.languagecode = languagecode;
        this.languageplace = languageplace;
        this.datades = datades;
        this.equipment = equipment;
        this.software = software;
        this.rightl = rightl;
        this.snote = snote;
        this.custom = custom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getRecordingdate() {
        return recordingdate;
    }

    public void setRecordingdate(String recordingdate) {
        this.recordingdate = recordingdate;
    }

    public String getRecordingplace() {
        return recordingplace;
    }

    public void setRecordingplace(String recordingplace) {
        this.recordingplace = recordingplace;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguagecode() {
        return languagecode;
    }

    public void setLanguagecode(String languagecode) {
        this.languagecode = languagecode;
    }

    public String getLanguageplace() {
        return languageplace;
    }

    public void setLanguageplace(String languageplace) {
        this.languageplace = languageplace;
    }

    public String getDatades() {
        return datades;
    }

    public void setDatades(String datades) {
        this.datades = datades;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getSoftware() {
        return software;
    }

    public void setSoftware(String software) {
        this.software = software;
    }

    public String getRightl() {
        return rightl;
    }

    public void setRightl(String rightl) {
        this.rightl = rightl;
    }

    public String getSnote() {
        return snote;
    }

    public void setSnote(String snote) {
        this.snote = snote;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    @Override
    public String toString() {
        return "Table{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", datatype='" + datatype + '\'' +
                ", projectname='" + projectname + '\'' +
                ", creator='" + creator + '\'' +
                ", speaker='" + speaker + '\'' +
                ", contributor='" + contributor + '\'' +
                ", recordingdate='" + recordingdate + '\'' +
                ", recordingplace='" + recordingplace + '\'' +
                ", language='" + language + '\'' +
                ", languagecode='" + languagecode + '\'' +
                ", languageplace='" + languageplace + '\'' +
                ", datades='" + datades + '\'' +
                ", equipment='" + equipment + '\'' +
                ", software='" + software + '\'' +
                ", rightl='" + rightl + '\'' +
                ", snote='" + snote + '\'' +
                ", custom='" + custom + '\'' +
                '}';
    }
}
