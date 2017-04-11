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
}
