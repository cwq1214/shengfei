package sample.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by chenweiqi on 2017/4/11.
 */
@DatabaseTable(tableName = "tbl_code_ipa_base")
public class CodeIPABase {
    @DatabaseField
    public String code;
    @DatabaseField
    public String content;
    @DatabaseField
    public String type;
}
