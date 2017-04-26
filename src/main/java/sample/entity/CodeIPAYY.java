package sample.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Bee on 2017/4/25.
 */
@DatabaseTable(tableName = "tbl_code_ipa_yyy")
public class CodeIPAYY {
    @DatabaseField(id = true, canBeNull = false)
    private String code;
    @DatabaseField
    private String content;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
