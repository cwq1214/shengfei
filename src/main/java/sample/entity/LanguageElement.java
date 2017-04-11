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

}
