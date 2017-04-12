package sample.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by chenweiqi on 2017/4/11.
 */
@DatabaseTable(tableName = "tbl_language_element")
public class LanguageElement {
    @DatabaseField
    public String ID;
    @DatabaseField
    public String clarcid;
    @DatabaseField
    public String language;
    @DatabaseField
    public String creator;
    @DatabaseField
    public String contributor;
    @DatabaseField
    public String coveragedate;
    @DatabaseField
    public String coverageplace;
    @DatabaseField
    public String datades;
    @DatabaseField
    public String software;
    @DatabaseField
    public String archive;
    @DatabaseField
    public String URL;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getClarcid() {
        return clarcid;
    }

    public void setClarcid(String clarcid) {
        this.clarcid = clarcid;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getCoveragedate() {
        return coveragedate;
    }

    public void setCoveragedate(String coveragedate) {
        this.coveragedate = coveragedate;
    }

    public String getCoverageplace() {
        return coverageplace;
    }

    public void setCoverageplace(String coverageplace) {
        this.coverageplace = coverageplace;
    }

    public String getDatades() {
        return datades;
    }

    public void setDatades(String datades) {
        this.datades = datades;
    }

    public String getSoftware() {
        return software;
    }

    public void setSoftware(String software) {
        this.software = software;
    }

    public String getArchive() {
        return archive;
    }

    public void setArchive(String archive) {
        this.archive = archive;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
