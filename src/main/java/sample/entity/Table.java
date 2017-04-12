package sample.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by chenweiqi on 2017/4/11.
 */
@DatabaseTable(tableName = "tbl_table")
public class Table {
    @DatabaseField(canBeNull = false, id = true)
    public int ID;
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

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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
}
