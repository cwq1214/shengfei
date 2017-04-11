package sample.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by chenweiqi on 2017/4/11.
 */
@DatabaseTable(tableName = "tbl_speaker")
public class Speaker {
    @DatabaseField
    public Integer ID;
    @DatabaseField
    public String withable;
    @DatabaseField
    public String speakcode;
    @DatabaseField
    public String realname;
    @DatabaseField
    public String sex;
    @DatabaseField
    public String birth;
    @DatabaseField
    public String usualLang;
    @DatabaseField
    public String motherLang;
    @DatabaseField
    public String secondLang;
    @DatabaseField
    public String education;
    @DatabaseField
    public String job;
    @DatabaseField
    public String workRole;
    @DatabaseField
    public String addr;
    @DatabaseField
    public String notetext;
}
