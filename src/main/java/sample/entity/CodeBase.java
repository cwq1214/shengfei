package sample.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by chenweiqi on 2017/4/11.
 */
@DatabaseTable(tableName = "tbl_code_base")
public class CodeBase {
    @DatabaseField
    public String code;
    @DatabaseField
    public Integer codeType;
    @DatabaseField
    public String content;
    @DatabaseField
    public String rank;
    @DatabaseField
    public String yun;
    @DatabaseField
    public String spell;
    @DatabaseField
    public String IPA;
    @DatabaseField
    public String english;
    @DatabaseField
    public String note;
    @DatabaseField
    public String mwfy;
}
