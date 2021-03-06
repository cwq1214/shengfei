package sample.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.controller.ImportExcel.ImportExcelBindViewController;
import sample.controller.MainController;
import sample.diycontrol.ProgressView.ProgressViewController;
import sample.entity.Record;
import sample.entity.Table;

import javax.swing.text.View;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Bee on 2017/5/14.
 */
public class ImportUtil {

    private static void impAV(File vDir,Record r,Table t){
        if (vDir != null){
            String wavName = r.getInvestCode() + (r.getContent().length() > 4?r.getContent().substring(0,4):r.getContent()) + ".wav";
            String mp4Name = r.getInvestCode() + (r.getContent().length() > 4?r.getContent().substring(0,4):r.getContent()) + ".mp4";

            System.out.println(wavName +"\t"+mp4Name);

            File[] fs = vDir.listFiles();
            for (File f : fs) {
                if (f.getName().contains(wavName)){
                    FileUtil.fileCopy(f.getAbsolutePath(),Constant.ROOT_FILE_DIR + "/audio/" + t.getId() + "/" + r.getUuid() + ".wav");
                    r.setDone("1");
                    r.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                }else if (f.getName().contains(mp4Name)){
                    FileUtil.fileCopy(f.getAbsolutePath(),Constant.ROOT_FILE_DIR + "/video/" + t.getId() + "/" + r.getUuid() + ".mp4");
                }
            }
        }
    }

    /**
     * 导入语保模板
     * @param type 0字 1词 2句
     */
    public static void importYbWithType(int type, MainController mainController){
        File selectFile = DialogUtil.fileChoiceDialog("请选择导入文件",DialogUtil.FILE_CHOOSE_TYPE_EXCEL);

        Workbook workbook = null;
        try {
            if (selectFile.getName().contains(".xlsx")){
                workbook = new XSSFWorkbook(new FileInputStream(selectFile));
            }else {
                workbook = new HSSFWorkbook(new POIFSFileSystem(selectFile));
            }

            Table t = new Table("",Integer.toString(type),"","","","","","","","","","","","","","","");
            DbHelper.getInstance().insertNewTable(t);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"是否同时导入音视频",new ButtonType("是"),new ButtonType("否"));
            alert.setTitle("提示");
            alert.setHeaderText("");

            Optional<ButtonType> result = alert.showAndWait();
            final File vDir;
            if (result.get().getText() == "是"){
                vDir = DialogUtil.dirChooses(new Stage());
            }else {
                vDir = null;
            }

            Sheet sheet = workbook.getSheetAt(0);

            ViewUtil.getInstance().showProgressView("此过程可能需要几分钟，请耐心等待！", new ViewUtil.ProgressMethod() {
                @Override
                public void progresing() {
                    if (type == 0){
                        importYbWord(sheet,t,vDir);
                    }else if (type == 1){
                        importYbCi(sheet,t,vDir);
                    }else if (type == 2){
                        importYbSentence(sheet,t,vDir);
                    }
                }

                @Override
                public void finished() {
                    mainController.openTable(t);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static void importYbCi(Sheet sheet,Table t,File vDir){
        List<Record> impDatas = new ArrayList<>();

        Row titleRow = sheet.getRow(0);
        boolean isLong = titleRow.getLastCellNum() > 9;

        System.out.println("last row:"+sheet.getLastRowNum());

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            StringBuilder wordSb = new StringBuilder();
            StringBuilder ipaSb = new StringBuilder();
            StringBuilder noteSb = new StringBuilder();
            for (int j = 0; j < 3; j++) {

                if (isLong){
                    Cell word = row.getCell(j*3+3);
                    Cell ipa = row.getCell(j*3 + 1 + 3);
                    Cell note = row.getCell(j*3 + 2 + 3);

                    if (word == null){
                        word = row.createCell(j*3+3);
                    }

                    if (ipa == null){
                        ipa = row.createCell(j*3 + 1 + 3);
                    }

                    if (note == null){
                        note = row.createCell(j*3 + 2 + 3);
                    }

                    word.setCellType(Cell.CELL_TYPE_STRING);
                    ipa.setCellType(Cell.CELL_TYPE_STRING);
                    note.setCellType(Cell.CELL_TYPE_STRING);

                    if (word.getStringCellValue().length() != 0){
                        wordSb.append(word.getStringCellValue() + ";");
                    }

                    if (ipa.getStringCellValue().length() != 0){
                        ipaSb.append(ipa.getStringCellValue() + ";");
                    }

                    if (note.getStringCellValue().length() != 0){
                        noteSb.append(note.getStringCellValue() + ";");
                    }
                }else {
                    Cell ipa = row.getCell(j*2 + 0 + 3);
                    Cell note = row.getCell(j*2 + 1 + 3);

                    if (ipa == null){
                        ipa = row.createCell(j*2 + 0 + 3);
                    }

                    if (note == null){
                        note = row.createCell(j*2 + 1 + 3);
                    }

                    ipa.setCellType(Cell.CELL_TYPE_STRING);
                    note.setCellType(Cell.CELL_TYPE_STRING);

                    if (ipa.getStringCellValue().length() != 0){
                        ipaSb.append(ipa.getStringCellValue() + ";");
                    }

                    if (note.getStringCellValue().length() != 0){
                        noteSb.append(note.getStringCellValue() + ";");
                    }
                }

            }

            if (wordSb.length() != 0){
                wordSb.deleteCharAt(wordSb.length() - 1);
            }

            if (ipaSb.length() != 0){
                ipaSb.deleteCharAt(ipaSb.length() - 1);
            }

            Cell noteCell = row.getCell(2);
            if (noteCell != null && noteCell.getStringCellValue().length() != 0){
                noteSb.insert(0,noteCell.getStringCellValue()+";");
            }

            if (noteSb.length() != 0){
                noteSb.deleteCharAt(noteSb.length() - 1);
            }

            Record r = new Record();
            r.setUuid(UUID.randomUUID().toString());
            r.setMWFY(wordSb.toString());
            r.setBaseId(t.getId());
            r.setHide("0");
            r.setDone("0");
            r.setBaseCode("yb"+ String.format("%04d",i));

            row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
            if (row.getCell(0).getStringCellValue().length() == 0){
                break;
            }
            r.setInvestCode(row.getCell(0).getStringCellValue());

            r.setIPA(ipaSb.toString());
            r.setNote(noteSb.toString());

            Cell contentCell = row.getCell(1);
            r.setContent(contentCell.getStringCellValue());
//            r.autoSupple();

            impAV(vDir,r,t);

            r.setInvestCode(r.getBaseCode());

            impDatas.add(r);

        }

        DbHelper.getInstance().insertRecordReal(impDatas, null);
    }
    private static void importYbSentence(Sheet sheet,Table t,File vDir){
        List<Record> impDatas = new ArrayList<>();

        Row titleRow = sheet.getRow(0);
        boolean isLong = titleRow.getLastCellNum() > 12;

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            StringBuilder wordSb = new StringBuilder();
            StringBuilder ipaSb = new StringBuilder();
            StringBuilder noteSb = new StringBuilder();
            StringBuilder wordCiSb = new StringBuilder();

            for (int j = 0; j < 3; j++) {
                if (isLong){

                    Cell ipa = row.getCell(j*4  + 3);
                    Cell ci = row.getCell(j*4 + 1 + 3);
                    Cell word = row.getCell(j*4 + 2 + 3);
                    Cell note = row.getCell(j*4 + 3 + 3);


                    if (word == null){
                        word = row.createCell(j*3 + 3);
                    }

                    if (ci == null){
                        ci = row.createCell(j*3 + 3);
                    }

                    if (ipa == null){
                        ipa = row.createCell(j*3 + 1 + 3);
                    }

                    if (note == null){
                        note = row.createCell(j*3 + 2 + 3);
                    }

                    word.setCellType(Cell.CELL_TYPE_STRING);
                    ipa.setCellType(Cell.CELL_TYPE_STRING);
                    note.setCellType(Cell.CELL_TYPE_STRING);
                    ci.setCellType(Cell.CELL_TYPE_STRING);

                    if (word.getStringCellValue().length() != 0){
                        wordSb.append(word.getStringCellValue() + ";");
                    }

                    if (ci.getStringCellValue().length() != 0){
                        wordCiSb.append(ci.getStringCellValue() + ";");
                    }

                    if (ipa.getStringCellValue().length() != 0){
                        ipaSb.append(ipa.getStringCellValue() + ";");
                    }

                    if (note.getStringCellValue().length() != 0){
                        noteSb.append(note.getStringCellValue() + ";");
                    }
                }else {
                    //声
                    Cell word = row.getCell(j*3 + 3);
                    Cell ipa = row.getCell(j*3 + 1 + 3);
                    Cell note = row.getCell(j*3 + 2 + 3);

                    if (word == null){
                        word = row.createCell(j*3 + 3);
                    }

                    if (ipa == null){
                        ipa = row.createCell(j*3 + 1 + 3);
                    }

                    if (note == null){
                        note = row.createCell(j*3 + 2 + 3);
                    }

                    word.setCellType(Cell.CELL_TYPE_STRING);
                    ipa.setCellType(Cell.CELL_TYPE_STRING);
                    note.setCellType(Cell.CELL_TYPE_STRING);

                    if (word.getStringCellValue().length() != 0){
                        wordSb.append(word.getStringCellValue() + ";");
                    }

                    if (ipa.getStringCellValue().length() != 0){
                        ipaSb.append(ipa.getStringCellValue() + ";");
                    }

                    if (note.getStringCellValue().length() != 0){
                        noteSb.append(note.getStringCellValue() + ";");
                    }
                }
            }

            if (wordSb.length() != 0){
                wordSb.deleteCharAt(wordSb.length() - 1);
            }

            if (wordCiSb.length() != 0){
                wordCiSb.deleteCharAt(wordCiSb.length() - 1);
            }

            if (ipaSb.length() != 0){
                ipaSb.deleteCharAt(ipaSb.length() - 1);
            }

            Cell noteCell = row.getCell(2);
            if (noteCell != null && noteCell.getStringCellValue().length() != 0){
                noteSb.insert(0,noteCell.getStringCellValue()+";");
            }

            if (noteSb.length() != 0){
                noteSb.deleteCharAt(noteSb.length() - 1);
            }

            Record r = new Record();
            r.setUuid(UUID.randomUUID().toString());
            r.setBaseId(t.getId());
            r.setHide("0");
            r.setDone("0");
            r.setBaseCode("yb"+ String.format("%04d",i));

            row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
            if (row.getCell(0).getStringCellValue().length() == 0){
                break;
            }
            r.setInvestCode(row.getCell(0).getStringCellValue());

            r.setFree_trans(wordCiSb.toString());
            r.setMWFY(wordSb.toString());
            r.setIPA(ipaSb.toString());
            r.setNote(noteSb.toString());


            Cell contentCell = row.getCell(1);
            r.setContent(contentCell.getStringCellValue());
//            r.autoSupple();

            impAV(vDir,r,t);

            r.setInvestCode(r.getBaseCode());

            impDatas.add(r);
        }

        DbHelper.getInstance().insertRecordReal(impDatas, null);
    }


    private static void importYbWord(Sheet sheet,Table t,File vDir){
        List<Record> impDatas = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            StringBuilder syd = new StringBuilder();
            StringBuilder bSb = new StringBuilder();

            if (row.getCell(2) != null && row.getCell(2).getStringCellValue().length() != 0){
                row.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
                bSb.append(row.getCell(2).getStringCellValue()+";");
            }

            for (int j = 0; j < 3; j++) {
                //声
                Cell s = row.getCell(j*4 + 0 + 3);
                Cell y = row.getCell(j*4 + 1 + 3);
                Cell d = row.getCell(j*4 + 2 + 3);
                Cell b = row.getCell(j*4 + 3 + 3);

                if (s == null){
                    s = row.createCell(j * 4 + 0 + 3);
                }
                if (y == null){
                    y = row.createCell(j * 4 + 2 + 3);
                }
                if (d == null){
                    d = row.createCell(j * 4 + 3 + 3);
                }
                if (b == null){
                    b = row.createCell(j * 4 + 4 + 3);
                }


                s.setCellType(Cell.CELL_TYPE_STRING);
                y.setCellType(Cell.CELL_TYPE_STRING);
                d.setCellType(Cell.CELL_TYPE_STRING);
                b.setCellType(Cell.CELL_TYPE_STRING);

                if (s.getStringCellValue().length() != 0 && y.getStringCellValue().length() != 0 && d.getStringCellValue().length() != 0){
                    syd.append(s.getStringCellValue() + y.getStringCellValue() + d.getStringCellValue()+";");
                }
                if (b.getStringCellValue().length() != 0){
                    bSb.append(b.getStringCellValue()+";");
                }
            }

            if (syd.length() != 0){
                syd.deleteCharAt(syd.length() - 1);
            }

            if (bSb.length() != 0){
                bSb.deleteCharAt(bSb.length() - 1);
            }

            Record r = new Record();
            r.setUuid(UUID.randomUUID().toString());
            r.setBaseId(t.getId());
            r.setHide("0");
            r.setDone("0");
            r.setBaseCode("yb"+ String.format("%04d",i));

            row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
            if (row.getCell(0).getStringCellValue().length() == 0){
                break;
            }
            r.setInvestCode(row.getCell(0).getStringCellValue());

            r.setIPA(syd.toString());
            r.setNote(bSb.toString());

            Cell contentCell = row.getCell(1);
            r.setContent(contentCell.getStringCellValue());
//            r.autoSupple();

            impAV(vDir,r,t);

            r.setInvestCode(r.getBaseCode());

            impDatas.add(r);
        }


        DbHelper.getInstance().insertRecordReal(impDatas,null);
    }
}
