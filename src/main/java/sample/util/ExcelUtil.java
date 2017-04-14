package sample.util;

import java.io.File;

/**
 * Created by chenweiqi on 2017/4/14.
 */
public class ExcelUtil {
    public static PoiExcelHelper getExcelHelper(File file) {
        if (file.getName().contains(".xlsx")) {
            return new PoiExcel2k7Helper();
        } else if (file.getName().contains(".xls")) {
            return new PoiExcel2k3Helper();
        }
        return null;
    }
}
