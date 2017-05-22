package sample.entity;

import java.util.List;

/**
 * Created by chenweiqi on 2017/5/17.
 */
public class BindResult {
    public String key1;
    public RecordMapper key2;





    public static String getKey2ByKey1(List<BindResult> bindResults,String key1){
        for (int i=0,max = bindResults.size(); i < max;i++){
            if (bindResults.get(i).key1.equals(key1)){
                return bindResults.get(i).key2.recordFieldName;
            }
        }
        return null;
    }

    public static class RecordMapper{
        public String recordName;
        public String recordFieldName;

        public RecordMapper(String recordName, String recordFieldName) {
            this.recordName = recordName;
            this.recordFieldName = recordFieldName;
        }
    }

}
