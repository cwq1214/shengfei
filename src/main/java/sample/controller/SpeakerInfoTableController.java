package sample.controller;

import javafx.beans.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.util.Callback;
import sample.controller.NewTableView.NewTableView;
import sample.controller.openTable.OpenTableController;
import sample.controller.openTable.OpenTableListener;
import sample.entity.Speaker;
import sample.entity.Table;
import sample.util.*;
import sample.view.Toast;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by chenweiqi on 2017/4/24.
 */
public class SpeakerInfoTableController extends BaseController {


    @FXML
    ListView lv_table;
    @FXML
    ListView lv_speakers;
    @FXML
    TextField input_code;
    @FXML
    TextField input_name;
    @FXML
    TextField input_birthday;
    @FXML
    ChoiceBox cb_gender;
    @FXML
    TextField input_usualLang;
    @FXML
    TextField input_motherLang;
    @FXML
    TextField input_secondLang;
    @FXML
    ChoiceBox cb_education;
    @FXML
    ChoiceBox cb_job;
    @FXML
    TextField input_workRole;
    @FXML
    TextField input_addr;
    @FXML
    TextArea input_notetext;
    @FXML
    Button btn_done;



    //目前显示的说话人
    private Speaker selSpeaker;
    //排序
    boolean isAsc = true;
    //说话人列表
    private List<Speaker> speakerList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        setListener();

        getSpeakerListAndShow();
        clearInput();
    }


    private void setListener() {
        lv_speakers.selectionModelProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                System.out.println("sel");
            }
        });

        lv_speakers.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                return new ListCell(){
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty && item != null)
                            this.setText(((Speaker) item).getRealname());
                    }
                };
            }
        });

        lv_table.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                return new ListCell() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty && item != null) {
                            this.setText(((Table) item).getTitle());
                        }else {
                            this.setText(null);
                        }
                    }
                };
            }
        });
        lv_speakers.getSelectionModel().selectedItemProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(javafx.beans.Observable observable) {
//                selSpeaker = (Speaker) lv_speakers.getSelectionModel().getSelectedItem();
//                edit = false;
//                showSelSpeakerInfo();
            }
        });
    }


    private void getSpeakerListAndShow(){
        speakerList = DbHelper.getInstance().getSpeakers();
        lv_speakers.setItems(FXCollections.observableArrayList(speakerList));


        selSpeaker = null;
        System.out.println(Arrays.toString(speakerList.toArray()));
    }

    //添加
    @FXML
    private void onSelTableClick(){
        OpenTableController vc = ((OpenTableController) ViewUtil.getInstance().showView("view/openTable.fxml", "打开表", -1, -1, ""));
        vc.setListener(new OpenTableListener() {
            @Override
            public void onOpenTable(Table t) {
               lv_table.getItems().add(t);
            }
        });
        vc.setOpen(true);

        vc.mStage.setResizable(false);
        vc.mStage.initModality(Modality.APPLICATION_MODAL);
        vc.mStage.show();

    }

    //删除
    @FXML
    private void onDelSelTableClick(){
        if (lv_table.getSelectionModel().getSelectedItems().size()!=0){
            lv_table.getItems().removeAll(lv_table.getSelectionModel().getSelectedItems());
        }
    }

    //确定
    @FXML
    private void onDoneClick(){

        if (selSpeaker==null){
            System.out.println("selSpeaker==null  return");
            return;
        }
        if (!validateInput()){
            return;
        }

        setSpeakerInfo(selSpeaker);
        DbHelper.getInstance().addOrUpdate(selSpeaker);

        List<Table> tables = lv_table.getItems();
        for (int i=0,max = tables.size();i<max;i++){
            DbHelper.getInstance().addSpeakerToTable(tables.get(i),selSpeaker);
        }

        getSpeakerListAndShow();
        clearInput();

        btn_done.setText("确定");
    }

    //取消
    @FXML
    private void onCancelClick(){
        if (selSpeaker!=null){
            showSelSpeakerInfo();
        }else {
            clearInput();
        }

    }

    //添加说话人
    @FXML
    private void onAddSpeakerClick(){
        clearInput();
        selSpeaker = new Speaker();
        btn_done.setText("添加");
    }

    //编辑说话人
    @FXML
    private void onEditSpeakerClick(){

        selSpeaker = (Speaker) lv_speakers.getSelectionModel().getSelectedItem();
        if (selSpeaker==null){
            return;
        }else {
            System.out.println(selSpeaker.toString());
        }
        clearInput();
        showSelSpeakerInfo();

        btn_done.setText("修改");
    }

    //删除说话人
    @FXML
    private void onDelSpeakerClick(){
        Speaker speaker = (Speaker) lv_speakers.getSelectionModel().getSelectedItem();
        if (speaker!=null)
            DbHelper.getInstance().delete(speaker);

        getSpeakerListAndShow();
        clearInput();
    }

    //说话人排序
    @FXML
    private void onSortSpeakerClick(){
        isAsc=!isAsc;
        lv_speakers.getItems().sort(new Comparator<Speaker>() {
            @Override
            public int compare(Speaker o1, Speaker o2) {
                if (isAsc){
                    return o1.getRealname().compareTo(o2.getRealname());
                }else {
                    return o2.getRealname().compareTo(o1.getRealname());
                }

            }
        });
        lv_speakers.refresh();
    }


    private void showSelSpeakerInfo(){
        input_addr.setText(selSpeaker.addr);
        input_birthday.setText(selSpeaker.birth);
        input_code.setText(selSpeaker.speakcode);
        input_motherLang.setText(selSpeaker.motherLang);
        input_notetext.setText(selSpeaker.notetext);
        input_secondLang.setText(selSpeaker.secondLang);
        input_usualLang.setText(selSpeaker.usualLang);
        input_workRole.setText(selSpeaker.workRole);
        input_name.setText(selSpeaker.realname);
        cb_education.getSelectionModel().select(selSpeaker.education);
        cb_gender.getSelectionModel().select(selSpeaker.sex);
        cb_job.getSelectionModel().select(selSpeaker.job);


        List tables = new ArrayList();
        if (!TextUtil.isEmpty(selSpeaker.withtable)) {
            String[] tableId = selSpeaker.withtable.split(",");

            for (String id :
                    tableId) {
                Table table = DbHelper.getInstance().getTableById(Integer.valueOf(id));
                if (table != null) {
                    tables.add(table);
                }
            }
        }
        lv_table.setItems(FXCollections.observableArrayList(tables));

    }

    private Speaker setSpeakerInfo(Speaker selSpeaker){
        if (selSpeaker==null) {
             selSpeaker = new Speaker();
        }
        selSpeaker.addr=input_addr.getText();
        selSpeaker.birth=input_birthday.getText();
        selSpeaker.speakcode = input_code.getText();
        selSpeaker.motherLang = input_motherLang.getText();
        selSpeaker.notetext = input_notetext.getText();
        selSpeaker.secondLang = input_secondLang.getText();
        selSpeaker.usualLang = input_usualLang.getText();
        selSpeaker.workRole = input_workRole.getText();
        selSpeaker.realname = input_name.getText();
        selSpeaker.education = (String) cb_education.getSelectionModel().getSelectedItem();
        selSpeaker.sex = (String) cb_gender.getSelectionModel().getSelectedItem();
        selSpeaker.job = (String) cb_job.getSelectionModel().getSelectedItem();

        List<Table> tables = lv_table.getItems();
        StringBuffer stringBuffer = new StringBuffer();
        for (Table table:
                tables) {
            stringBuffer.append(table.getId());
            stringBuffer.append(",");
        }
        selSpeaker.withtable = stringBuffer.toString();
        return selSpeaker;
    }

    private void clearInput(){
        input_addr.setText(null);
        input_birthday.setText(null);
        input_code.setText(null);
        input_motherLang.setText(null);
        input_notetext.setText(null);
        input_secondLang.setText(null);
        input_usualLang.setText(null);
        input_workRole.setText(null);
        input_name.setText(null);
        cb_education.getSelectionModel().select(-1);
        cb_gender.getSelectionModel().select(-1);
        cb_job.getSelectionModel().select(-1);
        lv_table.getItems().clear();

    }

    private boolean validateInput(){
        boolean pass = true;
        if (lv_table.getItems().size()==0
            ||(TextUtil.isEmpty(input_name.getText()))
            ||(TextUtil.isEmpty(input_usualLang.getText()))
            ||(TextUtil.isEmpty(input_birthday.getText()))
            ||(TextUtil.isEmpty(input_motherLang.getText()))
            ||(TextUtil.isEmpty(input_workRole.getText()))
            ||(TextUtil.isEmpty(input_addr.getText()))
            ||cb_gender.getSelectionModel().getSelectedItem()==null
                ){
            ToastUtil.show("带**为必填项");
            pass = false;
        }else {
            if (!TextUtil.isEmpty(input_birthday.getText())) {
                Pattern pattern  = Pattern.compile("\\d{4}/\\d{2}/\\d{2}");
                if (!pattern.matcher(input_birthday.getText()).matches()){
                    pass = false;
                    ToastUtil.show("出生日期格式有误");
                }

            }

        }


        return pass;
    }
}
