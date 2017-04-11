package sample.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by chenweiqi on 2017/4/11.
 */
@DatabaseTable(tableName = "tbl_topic")
public class Topic {
    @DatabaseField(id = true, canBeNull = false)
    public String uuid;
    @DatabaseField
    public int type;
    @DatabaseField
    public String content;
    @DatabaseField
    public String speakerId;
    @DatabaseField
    public String ipa;
    @DatabaseField
    public String mwfy;
    @DatabaseField
    public String spell;
    @DatabaseField
    public String word_trans;
    @DatabaseField
    public String free_trans;
    @DatabaseField
    public String note;
    @DatabaseField
    public String english;
    @DatabaseField
    public String createDate;

}
