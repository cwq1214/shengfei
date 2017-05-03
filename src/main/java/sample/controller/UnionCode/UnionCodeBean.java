package sample.controller.UnionCode;

import sample.entity.CodeBase;
import sample.entity.Record;

/**
 * Created by Bee on 2017/5/3.
 */
public class UnionCodeBean {
    private Record unionRecord;
    private CodeBase codeBase;

    public UnionCodeBean() {

    }

    public UnionCodeBean(Record unionRecord, CodeBase codeBase) {
        this.unionRecord = unionRecord;
        this.codeBase = codeBase;
    }

    public Record getUnionRecord() {
        return unionRecord;
    }

    public void setUnionRecord(Record unionRecord) {
        this.unionRecord = unionRecord;
    }

    public CodeBase getCodeBase() {
        return codeBase;
    }

    public void setCodeBase(CodeBase codeBase) {
        this.codeBase = codeBase;
    }
}
