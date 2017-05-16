package sample.controller.ImportExcel;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.controller.BaseController;
import sample.controller.MainController;
import sample.diycontrol.ProgressView.ProgressViewController;
import sample.entity.Record;
import sample.entity.Table;
import sample.util.DbHelper;
import sample.util.DialogUtil;
import sample.util.ViewUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Bee on 2017/5/13.
 */
public class ImportExcelBindViewController extends BaseController {

    //0字表
    //1词表
    //2句表
    private int excelType;

    public ObservableList<ExcelTitleBean> excelTitleData = FXCollections.observableArrayList();
    public ObservableList<ImpTitleBean> impTitleData = FXCollections.observableArrayList();
    public ObservableList<ImpBindBean> impBindData = FXCollections.observableArrayList();

    private Workbook workbook;

    @FXML
    private ListView excelTitleListView;

    @FXML
    private ListView impTitleListView;

    @FXML
    private ListView resultListView;

    @FXML
    public void addBtnClick(){
        if (excelTitleListView.getSelectionModel().getSelectedIndex() != -1 && impTitleListView.getSelectionModel().getSelectedIndex() != -1){
            impBindData.add(new ImpBindBean(excelTitleData.get(excelTitleListView.getSelectionModel().getSelectedIndex()),impTitleData.get(impTitleListView.getSelectionModel().getSelectedIndex())));
            resultListView.refresh();

            excelTitleListView.getItems().remove(excelTitleListView.getSelectionModel().getSelectedIndex());
            impTitleListView.getItems().remove(impTitleListView.getSelectionModel().getSelectedIndex());
        }
    }

    @FXML
    public void removeBtnClick(){
        if (resultListView.getSelectionModel().getSelectedIndex() != -1){
            ImpBindBean bean = ((ImpBindBean) resultListView.getItems().get(resultListView.getSelectionModel().getSelectedIndex()));
            excelTitleData.add(bean.getExcelTitleBean());
            impTitleData.add(bean.getImpTitleBean());

            resultListView.getItems().remove(resultListView.getSelectionModel().getSelectedIndex());

            excelTitleListView.refresh();
            impTitleListView.refresh();
            resultListView.refresh();
        }
    }

    @FXML
    public void okBtnClick(){
        if (impBindData.size() != 0){
            List<Record> impDatas = new ArrayList<>();

            Table t = new Table("",Integer.toString(excelType),"","","","","","","","","","","","","","","");
            DbHelper.getInstance().insertNewTable(t);

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                Record record = new Record();

                //应对空行问题
                boolean isAllNull = true;
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    ImpTitleBean titleBean = hasThisCol(j);
                    if (titleBean != null){
                        try {
                            if (row.getCell(j) != null){
                                Method m = Record.class.getMethod(titleBean.getMethodName(),String.class);
                                m.invoke(record,row.getCell(j).getStringCellValue());
                                if (!row.getCell(j).getStringCellValue().equalsIgnoreCase("")){
                                    isAllNull = false;
                                }
                            }
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (!isAllNull){
                    record.setHide("0");
                    record.setDone("0");
                    record.setUuid(UUID.randomUUID().toString());
                    record.setInvestCode(record.getBaseCode());
                    record.setBaseId(t.getId());
                    impDatas.add(record);
                }
            }
            ProgressViewController pvc = ViewUtil.getInstance().showProgressView("导入数据中，请稍后");
            DbHelper.getInstance().insertRecordReal(impDatas, new InsertSuccessCallBack() {
                @Override
                public void insertSuccess() {
                    pvc.mStage.close();
                    mStage.close();
                    ((MainController) preData).openTable(t);
                }
            });
        }else{
            DialogUtil.showDialog("请绑定数据");
        }
    }

    public void setExcelType(int excelType) {
        this.excelType = excelType;
    }

    @Override
    public void onCreatedView() {
        super.onCreatedView();
        File selectFile = DialogUtil.fileChoiceDialog("选择文件",DialogUtil.FILE_CHOOSE_TYPE_EXCEL);

        try {
            if (selectFile.getName().contains(".xlsx")){
                workbook = new XSSFWorkbook(new FileInputStream(selectFile));
            }else {
                workbook = new HSSFWorkbook(new POIFSFileSystem(selectFile));
            }
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(0);
            for (int i = 0;i<row.getLastCellNum();i++){
                Cell cell = row.getCell(i);
                if (cell != null){
                    excelTitleData.add(new ExcelTitleBean(cell.getStringCellValue(),i));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        excelTitleListView.setCellFactory(TextFieldListCell.forListView(new StringConverter<ExcelTitleBean>() {
            @Override
            public String toString(ExcelTitleBean bean) {
                return bean.getTitleName();
            }

            @Override
            public ExcelTitleBean fromString(String string) {
                return null;
            }
        }));
        excelTitleListView.setItems(excelTitleData);

        impTitleListView.setCellFactory(TextFieldListCell.forListView(new StringConverter<ImpTitleBean>() {
            @Override
            public String toString(ImpTitleBean object) {
                return object.getTitleName();
            }

            @Override
            public ImpTitleBean fromString(String string) {
                return null;
            }
        }));

        resultListView.setCellFactory(TextFieldListCell.forListView(new StringConverter<ImpBindBean>() {
            @Override
            public String toString(ImpBindBean object) {
                return object.getExcelTitleBean().getTitleName() + "-" + object.getImpTitleBean().getTitleName();
            }

            @Override
            public ImpBindBean fromString(String string) {
                return null;
            }
        }));
        resultListView.setItems(impBindData);

        if (excelType == 0){
            impTitleData = FXCollections.observableArrayList(Record.wordTilteData);
        }else if (excelType == 1){
            impTitleData = FXCollections.observableArrayList(Record.ciTilteData);
        }else if (excelType == 2){
            impTitleData = FXCollections.observableArrayList(Record.sentenceTilteData);
        }
        impTitleListView.setItems(impTitleData);
    }

    private ImpTitleBean hasThisCol(int index){
        for (int i = 0; i < impBindData.size(); i++) {
            ImpBindBean bindBean = impBindData.get(i);
            if (index == bindBean.getExcelTitleBean().getIndex()){
                return bindBean.getImpTitleBean();
            }
        }
        return null;
    }

    @FXML
    public void cancelBtnClick(){
        mStage.close();
    }


    public interface InsertSuccessCallBack{
        public void insertSuccess();
    }
}
