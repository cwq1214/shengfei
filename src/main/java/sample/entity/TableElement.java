package sample.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by chenweiqi on 2017/4/11.
 */
@DatabaseTable(tableName = "tbl_table_element")
public class TableElement {
    @DatabaseField
    public Integer ID;
    @DatabaseField
    public String tableId;
    @DatabaseField
    public String datatype;
    @DatabaseField
    public String projectname;
    @DatabaseField
    public String creator;
    @DatabaseField
    public String speakerId;
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
    public String equipment;
    @DatabaseField
    public String software;
    @DatabaseField
    public String rights;
    @DatabaseField
    public String note;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
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

    public String getSpeakerId() {
        return speakerId;
    }

    public void setSpeakerId(String speakerId) {
        this.speakerId = speakerId;
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

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
