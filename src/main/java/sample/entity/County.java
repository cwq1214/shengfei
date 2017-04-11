package sample.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by chenweiqi on 2017/4/10.
 */
@DatabaseTable(tableName = "tbl_code_county")
public class County {
    @DatabaseField
    public int code;
    @DatabaseField
    public String name;
}
