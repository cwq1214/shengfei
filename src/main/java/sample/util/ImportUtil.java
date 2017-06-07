package sample.util;

import javafx.scene.control.Alert;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Bee on 2017/5/14.
 */
public class ImportUtil {

    private static void impAV(File vDir,Record r,Table t){
        if (vDir != null){
            String wavName = r.getBaseCode() + (r.getContent().length() > 4?r.getContent().substring(0,4):r.getContent()) + ".wav";
            String mp4Name = r.getBaseCode() + (r.getContent().length() > 4?r.getContent().substring(0,4):r.getContent()) + ".mp4";

            File[] fs = vDir.listFiles();
            for (File f : fs) {
                if (f.getName().contains(wavName)){
                    FileUtil.fileCopy(f.getAbsolutePath(),Constant.ROOT_FILE_DIR + "/audio/" + t.getId() + "/" + r.getUuid() + ".wav");
                    r.setDone("1");
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

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("提示");
            alert.setHeaderText("");
            alert.setContentText("是否同时导入音视频");

            Optional<ButtonType> result = alert.showAndWait();
            File vDir = null;
            if (result.get() == ButtonType.OK){
                vDir = DialogUtil.dirChooses(new Stage());
            }

            Sheet sheet = workbook.getSheetAt(0);
            if (type == 0){
                importYbWord(sheet,t,vDir);
            }else if (type == 1){
                importYbCi(sheet,t,vDir);
            }else if (type == 2){
                importYbSentence(sheet,t,vDir);
            }

            mainController.openTable(t);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static void importYbCi(Sheet sheet,Table t,File vDir){
        List<Record> impDatas = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            StringBuilder ipaSb = new StringBuilder();
            StringBuilder noteSb = new StringBuilder();
            System.out.println("i:"+i);
            for (int j = 0; j < 3; j++) {
                //声
                Cell ipa = row.getCell(j*3 + 1 + 3);
                Cell note = row.getCell(j*3 + 2 + 3);

                if (ipa == null){
                    ipa = row.createCell(j*3 + 1 + 3);
                }

                if (note == null){
                    note = row.createCell(j*3 + 2 + 3);
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

            if (ipaSb.length() != 0){
                ipaSb.deleteCharAt(ipaSb.length() - 1);
            }

            Cell noteCell = row.getCell(2);
            if (noteCell.getStringCellValue().length() != 0){
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
            r.setBaseCode("yb"+ String.format("%05d",i));
            r.setInvestCode(r.getBaseCode());
            r.setIPA(ipaSb.toString());
            r.setNote(noteSb.toString());

            Cell contentCell = row.getCell(1);
            r.setContent(contentCell.getStringCellValue());
            r.autoSupple();

            impAV(vDir,r,t);

            impDatas.add(r);

        }

        ProgressViewController pvc = ViewUtil.getInstance().showProgressView("导入数据中，请稍等");
        DbHelper.getInstance().insertRecordReal(impDatas, new ImportExcelBindViewController.InsertSuccessCallBack() {
            @Override
            public void insertSuccess() {
                pvc.mStage.close();
            }
        });
    }
    private static void importYbSentence(Sheet sheet,Table t,File vDir){
        List<Record> impDatas = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            StringBuilder ipaSb = new StringBuilder();
            StringBuilder noteSb = new StringBuilder();
            System.out.println("i:"+i);
            for (int j = 0; j < 3; j++) {
                //声
                Cell ipa = row.getCell(j*3 + 1 + 3);
                Cell note = row.getCell(j*3 + 2 + 3);

                if (ipa == null){
                    ipa = row.createCell(j*3 + 1 + 3);
                }

                if (note == null){
                    note = row.createCell(j*3 + 2 + 3);
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

            if (ipaSb.length() != 0){
                ipaSb.deleteCharAt(ipaSb.length() - 1);
            }

            Cell noteCell = row.getCell(2);
            if (noteCell.getStringCellValue().length() != 0){
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
            r.setBaseCode("yb"+ String.format("%05d",i));
            r.setInvestCode(r.getBaseCode());
            r.setIPA(ipaSb.toString());
            r.setNote(noteSb.toString());

            Cell contentCell = row.getCell(1);
            r.setContent(contentCell.getStringCellValue());
            r.autoSupple();

            impAV(vDir,r,t);

            impDatas.add(r);
        }

        ProgressViewController pvc = ViewUtil.getInstance().showProgressView("导入数据中，请稍等");
        DbHelper.getInstance().insertRecordReal(impDatas, new ImportExcelBindViewController.InsertSuccessCallBack() {
            @Override
            public void insertSuccess() {
                pvc.mStage.close();
            }
        });
    }


    private static void importYbWord(Sheet sheet,Table t,File vDir){
        List<Record> impDatas = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            StringBuilder syd = new StringBuilder();
            StringBuilder bSb = new StringBuilder();
            for (int j = 0; j < 3; j++) {
                //声
                Cell s = row.getCell(j*3 + 0 + 3);
                Cell y = row.getCell(j*3 + 1 + 3);
                Cell d = row.getCell(j*3 + 2 + 3);
                Cell b = row.getCell(j*3 + 3 + 3);

                if (s == null){
                    s = row.createCell(j * 3 + 0 + 3);
                }
                if (y == null){
                    y = row.createCell(j * 3 + 2 + 3);
                }
                if (d == null){
                    d = row.createCell(j * 3 + 3 + 3);
                }
                if (b == null){
                    b = row.createCell(j * 3 + 4 + 3);
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
            r.setBaseCode("yb"+ String.format("%05d",i));
            r.setInvestCode(r.getBaseCode());
            r.setIPA(syd.toString());
            r.setNote(bSb.toString());

            Cell contentCell = row.getCell(1);
            r.setContent(contentCell.getStringCellValue());
            r.autoSupple();

            impAV(vDir,r,t);

            impDatas.add(r);
        }




        ProgressViewController pvc = ViewUtil.getInstance().showProgressView("导入数据中，请稍等");
        DbHelper.getInstance().insertRecordReal(impDatas, new ImportExcelBindViewController.InsertSuccessCallBack() {
            @Override
            public void insertSuccess() {
                pvc.mStage.close();
            }
        });
    }
}
