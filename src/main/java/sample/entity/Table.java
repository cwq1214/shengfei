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

}
