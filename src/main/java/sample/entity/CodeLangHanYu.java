package sample.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by chenweiqi on 2017/4/11.
 */
@DatabaseTable(tableName = "tbl_code_lang_hanyu")
public class CodeLangHanYu {
    @DatabaseField
    public String code;
    @DatabaseField
    public String name;
    @DatabaseField
    public String region;
}
