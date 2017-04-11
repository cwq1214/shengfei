package sample.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by chenweiqi on 2017/4/11.
 */
@DatabaseTable(tableName = "tbl_record")
public class Record {
    @DatabaseField(id = true, canBeNull = false)
    public String uuid;
    @DatabaseField
    public int baseId;
    @DatabaseField
    public String investCode;
    @DatabaseField
    public String baseCode;
    @DatabaseField
    public String hide;
    @DatabaseField
    public String done;
    @DatabaseField
    public String IPA;
    @DatabaseField
    public String note;
    @DatabaseField
    public String spell;
    @DatabaseField
    public String english;
    @DatabaseField
    public String MWFY;
    @DatabaseField
    public String content;
    @DatabaseField
    public String rank;
    @DatabaseField
    public String yun;
    @DatabaseField
    public String createDate;
}
