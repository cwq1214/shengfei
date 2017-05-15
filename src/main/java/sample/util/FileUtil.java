package sample.util;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Created by chenweiqi on 2017/4/14.
 */
public class FileUtil {

    public static void TableViewDataToExcel(TableView tableView, File saveFile) throws NoSuchFieldException, IllegalAccessException, IOException {
        if (saveFile == null) {
            throw new RuntimeException("save File can not be null");
        }
        if (!saveFile.exists()) {
            saveFile.getParentFile().mkdirs();
        }
        ObservableList<TableColumn> columns = tableView.getColumns();
        ObservableList<Object> items = tableView.getItems();
//        Field[] fields = null;
        Workbook workbook = null;
        String[] titleNames = null;
        if (saveFile.getName().contains(".xlsx")) {
            workbook = new XSSFWorkbook();
        } else if (saveFile.getName().contains(".xls")) {
            workbook = new HSSFWorkbook();
        }

        for (int i = 0; i < columns.size(); i++) {

        }

//        if (items!=null&&items.size()!=0){
//            fields= items.get(0).getClass().getDeclaredFields();
//            for (Field f :
//                    fields) {
//                f.setAccessible(true);
//            }
//        }

        Sheet sheet = workbook.createSheet();
        for (int i = 0, iMax = items.size() + 1; i < iMax; i++) {
            Row row = sheet.createRow(i);
            for (int j = 0, jMax = columns.size(); j < jMax; j++) {
                Cell cell = row.createCell(j);

                if (i == 0) {
                    cell.setCellValue(columns.get(j).getText());
                    continue;
                }

                Object rowObj = items.get(i - 1);
                String columnFieldName = ((PropertyValueFactory) columns.get(j).getCellValueFactory()).getProperty();
                Field field = rowObj.getClass().getDeclaredField(columnFieldName);
                field.setAccessible(true);
                String content = (String) field.get(rowObj);
                cell.setCellValue(content);

            }
        }

        FileOutputStream outputStream = new FileOutputStream(saveFile);
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();


    }


    private Workbook getWorkBook(String path) {
        if (path.endsWith(".xlsx")) {


            return new XSSFWorkbook();
        } else if (path.endsWith(".xls")) {
            return new HSSFWorkbook();
        }
        return null;
    }


    public static String getFullLocation(String dir,String fileName){
        File dirF = new File(dir);
        File[] subFiles = dirF.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.getName().contains(fileName)){
                    return true;
                }
                return false;
            }
        });
        if (subFiles == null || subFiles.length == 0){
            return null;
        }else {
            return subFiles[0].getAbsolutePath();
        }
    }

    public static void deleteFile(String path){
        File file = new File(path);
        if (!file.exists()){
            return;
        }
        file.delete();

    }

    public static void copyFile(File from,File to){
        if (!from.exists()){
            return;
        }
        try {
            Files.copy(from.toPath(),to.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void copy(String src, String des) {
        File file1=new File(src);
        File[] fs=file1.listFiles();
        File file2=new File(des);
        if(!file2.exists()){
            file2.mkdirs();
        }
        for (File f : fs) {
            if(f.isFile()){
                fileCopy(f.getPath(),des+"\\"+f.getName()); //调用文件拷贝的方法
            }else if(f.isDirectory()){
                copy(f.getPath(),des+"\\"+f.getName());
            }
        }

    }

    /**
     * 文件拷贝的方法
     */
    public static void fileCopy(String src, String des) {
        try {
            Files.copy(new File(src).toPath(),new File(des).toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
