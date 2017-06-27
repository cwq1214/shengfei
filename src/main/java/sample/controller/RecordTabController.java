package sample.controller;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.util.Callback;
import org.apache.poi.xwpf.usermodel.TOC;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import sample.Main;
import sample.controller.AV.BeeAudioRecord;
import sample.controller.AV.BeeVideoRecord;
import sample.controller.YBCC.YBCCBean;
import sample.controller.widget.VideoPlayer;
import sample.entity.Record;
import sample.entity.Table;
import sample.util.*;

/**
 * Created by chenweiqi on 2017/4/18.
 */
public class RecordTabController extends BaseController {

    private BeeVideoRecord vRecord;
    private BeeAudioRecord aRecord;
    private Record selectRecord;
    private boolean isRecordVideo;

    TableColumn<Record,String> doneCol = new TableColumn<>("录音状态");
    TableColumn<Record,String> videoDoneCol = new TableColumn<>("录像状态");
    TableColumn<Record,String> codeCol = new TableColumn<>("编码");
    TableColumn<Record,String> rankCol = new TableColumn<>("分级");
    TableColumn<Record,String> yunCol = new TableColumn<>("音韵");
    TableColumn<Record,String> IPACol = new TableColumn<>("音标注音");
    TableColumn<Record,String> spellCol = new TableColumn<>("拼音");
    TableColumn<Record,String> englishCol = new TableColumn<>("英语");
    TableColumn<Record,String> noteCol = new TableColumn<>("注释");
    TableColumn<Record,String> recordDateCol = new TableColumn<>("录音日期");
    TableColumn<Record,String> contentCol = new TableColumn<>("内容");
    TableColumn<Record,String> mwfyCol = new TableColumn<>("民族文字或方言字");
    TableColumn<Record,String> freeTran = new TableColumn<>("普通话词对译");

    public Table t;

    @FXML
    Tab tab_recordVideo;
    @FXML
    ImageView img;
    @FXML
    ChoiceBox cb_resolution;
    @FXML
    ChoiceBox cb_sampleRate;
    @FXML
    ChoiceBox cb_bit;
    @FXML
    ChoiceBox cb_cameraName;
    @FXML
    Label label_recordTime;
    @FXML
    Button btn_recordAudio;
    @FXML
    Button btn_recordVideo;
    @FXML
    ProgressBar pgb_pg1;
    @FXML
    ProgressBar pgb_pg2;
    @FXML
    TableView<Record> tableView;
    @FXML
    VideoPlayer videoPlayer;
    @FXML
    Button btn_playAudio;
    @FXML
    Button btn_playNextAudio;
    @FXML
    ChoiceBox cb_tableShowMode;
    @FXML
    CheckBox cb_cover;
    @FXML
    ChoiceBox cb_importAudio;
    @FXML
    ChoiceBox cb_exportAudio;
    @FXML
    ChoiceBox cb_delAudio;
    @FXML
    ChoiceBox cb_importVideo;
    @FXML
    ChoiceBox cb_exportVideo;
    @FXML
    ChoiceBox cb_delVideo;
    @FXML
    ToggleGroup recordMode;
    @FXML
    ChoiceBox cb_recordSpace;
    @FXML
    TextField input_number;
    @FXML
    Label label_maxNumber;
    @FXML
    private ImageView demoImgView;
    @FXML
    private VideoPlayer demoVideoPlayer;

    @FXML
    private Tab tab_langPic;

    @FXML
    private TextArea tipTextArea;
    @FXML
    private Slider fontSizeSlider;

    //录音时间间隔
    int recordTImeSpace = 2;

    public String tableType;

    //是否录像
    boolean recordingVideo = false;
    //是否音中
    boolean recordingAudio = false;


    boolean autoRecordNext = false;

    boolean isInputNumber = false;

    boolean isSelRow = false;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss.SSS");
    SimpleDateFormat record_simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//    VideoRecorder recordVideoThread;
//    AudioRecorder recordAudioThread;
    Recorder recorder;

    //要录音的数据
    private ObservableList<Record> recordDatas;

    long startRecordTimes;

    //保存的文件名称
    String fileName;

    Record selRecord;
    VideoTimerThread videoTimerThread;
    //音频播放
    MediaPlayer mediaPlayer;

    public ObservableList<Record> getRecordDatas() {
        return recordDatas;
    }

    public void setRecordDatas(ObservableList<Record> recordDatas) {
        this.recordDatas = recordDatas;
        System.out.println(recordDatas.size());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.getSelectionModel().setCellSelectionEnabled(true);
    }

    public void startPreview(){
        defaultSetting();
        setupTablView();

        startPreviewVideo();
        startPreviewAudio();



        tipTextArea.setEditable(false);
        fontSizeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double fontSize = (1 - newValue.doubleValue() / 100.0) * (48 - 20) + 20;
                tipTextArea.setFont(new Font(fontSize));
            }
        });
    }

    public void setupTablView(){
        IPACol.setId("test");

        ContextMenu tblContextMenu = new ContextMenu();
        MenuItem impAudio = new MenuItem("导入音频");
        MenuItem impVideo = new MenuItem("导入视频");
        MenuItem delAudio = new MenuItem("删除音频");
        MenuItem delVideo = new MenuItem("删除视频");
        MenuItem expAudio = new MenuItem("导出音频");
        MenuItem expVideo = new MenuItem("导出视频");

        impAudio.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (tableView.getSelectionModel().getSelectedItems().size() == 1){
                    List<File> files = DialogUtil.chooseAudio(false);
                    if (files==null||files.size()==0){
                        return;
                    }
                    importMedia(tableView.getSelectionModel().getSelectedItems(),files,1,true);
                }
            }
        });

        impVideo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                List<File> files = DialogUtil.chooseVideo(false);
                if (files==null||files.size()==0){
                    return;
                }
                importMedia(tableView.getSelectionModel().getSelectedItems(),files,1,false);
            }
        });

        delAudio.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cb_delAudio.getSelectionModel().select(1);
            }
        });

        delVideo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cb_delVideo.getSelectionModel().select(1);
            }
        });

        expAudio.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cb_exportAudio.getSelectionModel().select(1);
            }
        });

        expVideo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cb_exportVideo.getSelectionModel().select(1);
            }
        });

        tblContextMenu.getItems().addAll(impAudio,impVideo,delAudio,delVideo,expAudio,expVideo);
        tableView.setContextMenu(tblContextMenu);

        Callback<TableColumn<Record,String>,TableCell<Record,String>> callback = (TableColumn<Record,String> col) -> new MyCell();

        videoDoneCol.setCellFactory(callback);
        doneCol.setCellFactory(callback);

        doneCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> param) {
                if (param.getValue().getDone().equals("0")){
                    return new ReadOnlyStringWrapper("未录");
                }
                return new ReadOnlyStringWrapper("已录");
            }
        });
        videoDoneCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> param) {
                File f = new File(Constant.ROOT_FILE_DIR+"/video/"+t.getId()+"/"+param.getValue().getUuid()+".mp4");
                if (f.exists()){
                    return new ReadOnlyStringWrapper("已录");
                }
                return new ReadOnlyStringWrapper("未录");
            }
        });
        codeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> param) {
                return new SimpleStringProperty(param.getValue().getInvestCode());
            }
        });
        rankCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> param) {
                return new SimpleStringProperty(param.getValue().getRank());
            }
        });
        yunCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> param) {
                return new SimpleStringProperty(param.getValue().getYun());
            }
        });
        IPACol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> param) {
                return new SimpleStringProperty(param.getValue().getIPA());
            }
        });
        spellCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> param) {
                return new SimpleStringProperty(param.getValue().getSpell());
            }
        });
        englishCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> param) {
                return new SimpleStringProperty(param.getValue().getEnglish());
            }
        });
        noteCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> param) {
                return new SimpleStringProperty(param.getValue().getNote());
            }
        });
        recordDateCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> param) {
                return new SimpleStringProperty(param.getValue().getCreateDate());
            }
        });
        contentCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> param) {
                return new SimpleStringProperty(param.getValue().getContent());
            }
        });
        mwfyCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> param) {
                return new SimpleStringProperty(param.getValue().getMWFY());
            }
        });
        freeTran.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Record, String> param) {
                return new SimpleStringProperty(param.getValue().getFree_trans());
            }
        });

        //设置header contextMenu
        doneCol.setContextMenu(setupHeaderMenu(true,false,true,true,true));
        codeCol.setContextMenu(setupHeaderMenu(true,true,true,true,true));
        rankCol.setContextMenu(setupHeaderMenu(true,true,true,true,true));
        contentCol.setContextMenu(setupHeaderMenu(true,true,true,true,true));
        yunCol.setContextMenu(setupHeaderMenu(true,true,true,true,true));
        IPACol.setContextMenu(setupHeaderMenu(true,true,true,true,true));
        spellCol.setContextMenu(setupHeaderMenu(true,true,true,true,true));
        englishCol.setContextMenu(setupHeaderMenu(true,true,true,true,true));
        noteCol.setContextMenu(setupHeaderMenu(true,true,true,true,true));
        recordDateCol.setContextMenu(setupHeaderMenu(true,true,true,true,true));
        mwfyCol.setContextMenu(setupHeaderMenu(true,true,true,true,true));
        freeTran.setContextMenu(setupHeaderMenu(true,true,true,true,true));
        videoDoneCol.setContextMenu(setupHeaderMenu(true,true,true,true,true));

        recordDatas = FXCollections.observableArrayList(DbHelper.getInstance().searchTempRecordKeep(tableType,t.getId()));
        tableView.setItems(recordDatas);

        if (tableType.equals("0")) {
            tableView.getColumns().addAll(codeCol, doneCol,videoDoneCol, contentCol, englishCol, yunCol, noteCol, rankCol, spellCol, IPACol, recordDateCol);
        }else if (tableType.equals("1")){
            tableView.getColumns().addAll(codeCol,doneCol,videoDoneCol, rankCol, contentCol, mwfyCol, IPACol, spellCol,englishCol, noteCol, recordDateCol);
        }else if (tableType.equals("2")){
            tableView.getColumns().addAll(codeCol,doneCol,videoDoneCol, rankCol, contentCol, mwfyCol, IPACol, freeTran, noteCol, englishCol, recordDateCol);
        }


    }

    public void praatClick(){
        if (tableView.getSelectionModel().getSelectedItems().size() != 0){
            if (tableView.getSelectionModel().getSelectedItems().get(0).getDone().equals("0")){
                ToastUtil.show("该条目未录音");
                return;
            }
            try {
                Runtime.getRuntime().exec("cmd /c start "+ new File(Constant.PRAAT + "/praat.exe").getAbsolutePath());
                Thread.sleep(500);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String param = "#Read from file... " + new File(getSelItemAudioPath(tableView.getSelectionModel().getSelectedItems().get(0))).getAbsolutePath() + "#   #Edit# ";
            param = param.replace("#","\"");
            try {
                Runtime.getRuntime().exec("cmd /c start "+ new File(Constant.PRAAT + "/sendpraat.exe").getAbsolutePath() + " praat "+ param);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ContextMenu setupHeaderMenu(boolean sort,boolean change,boolean hide,boolean search,boolean replace){
        ContextMenu headerMenu = new ContextMenu();
        MenuItem sortItem = new MenuItem("记录排序");
        MenuItem changeItem = new MenuItem("修改列");
        MenuItem hideItem = new MenuItem("隐藏列");
        MenuItem searchItem = new MenuItem("在当前列查找");
        MenuItem replaceItem = new MenuItem("在当前列替换");
        MenuItem showHideItem = new MenuItem("显示隐藏列");

        sortItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TableColumn c = searchColumn(headerMenu);
                if (tableView.getSortOrder().size() == 0){
                    c.setSortType(TableColumn.SortType.ASCENDING);
                    tableView.getSortOrder().setAll(c);
                }else{
                    if (tableView.getSortOrder().get(0).equals(c)){
                        if (c.getSortType() == TableColumn.SortType.ASCENDING){
                            c.setSortType(TableColumn.SortType.DESCENDING);
                            tableView.getSortOrder().setAll(c);
                        }else {
                            tableView.getSortOrder().clear();
                        }
                    }else{
                        c.setSortType(TableColumn.SortType.ASCENDING);
                        tableView.getSortOrder().setAll(c);
                    }
                }
                tableView.sort();
            }
        });
        changeItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TableColumn c = searchColumn(headerMenu);
                System.out.println(c.getText());
            }
        });
        hideItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TableColumn c = searchColumn(headerMenu);
                tableView.getColumns().remove(c);
            }
        });
        searchItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    SearchViewController searchViewController = (SearchViewController) ViewUtil.getInstance().openSearchDialog();
                    List columnsName = getTableColumnNameList();
                    searchViewController.setSearchTableTitleName(columnsName);
                    searchViewController.setSearchTableTitleShowIndex(tableView.getColumns().indexOf(searchColumn(headerMenu)));
                    searchViewController.setOnDoneClickCallback(new SearchViewController.OnDoneClickCallback() {
                        @Override
                        public void onClick(SearchViewController controller) {
                            String inputText = controller.getInputText();
                            boolean isAccurate = controller.isChecked();
                            int index = controller.getSelTableTitleIndex();

                            Pattern pattern = Pattern.compile("[,，;；]");
                            String[] ips = pattern.split(inputText);

                            TableColumn col = ((TableColumn) tableView.getColumns().get(index));

                            List<Record> temp = new ArrayList<>();

                            for (int i = 0; i < recordDatas.size(); i++) {
                                Record bean = recordDatas.get(i);
                                String colData = ((StringProperty) col.getCellValueFactory().call(new TableColumn.CellDataFeatures<>(tableView, col, recordDatas.get(i)))).get();
                                if (isAccurate){
                                    for (String s : ips) {
                                        if (colData.contains(s)){
                                            temp.add(bean);
                                            break;
                                        }
                                    }
                                }else{
                                    for (String s : ips) {
                                        Pattern p = Pattern.compile(getMHSearRegEx(s));
                                        if (p.matcher(colData).find()){
                                            temp.add(bean);
                                            break;
                                        }
                                    }
                                }
                            }

                            recordDatas = FXCollections.observableArrayList(temp);
                            tableView.setItems(recordDatas);
                            tableView.refresh();
                            searchViewController.close();
                        }
                    });
                    searchViewController.show();
                }catch (Exception e){

                }

            }
        });
        replaceItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{
                    ReplaceViewController controller = (ReplaceViewController) ViewUtil.getInstance().openReplaceDialog();
                    controller.setChoiceBoxItems(getTableColumnNameList());
                    controller.setTableNameShowIndex(tableView.getColumns().indexOf(searchColumn(headerMenu)));
                    controller.setOnDoneClickCallback(new ReplaceViewController.OnDoneClickCallback() {
                        @Override
                        public void click(ReplaceViewController controller) {
                            String replaceContent = controller.getInputReplaceContent();
                            String searchContent = controller.getInputSearchContent();
                            boolean isChecked = controller.getIsSelCheckBox();
                            int index = controller.getChoiceBoxSelItemIndex();

                            TableColumn col = ((TableColumn) tableView.getColumns().get(index));

                            for (int i = 0; i < recordDatas.size(); i++) {
                                String colData = ((StringProperty) col.getCellValueFactory().call(new TableColumn.CellDataFeatures<>(tableView, col, recordDatas.get(i)))).get();
                                if (isChecked){
                                    if (!colData.equalsIgnoreCase(searchContent)){
                                        continue;
                                    }
                                }
                                col.getOnEditCommit().handle(new TableColumn.CellEditEvent<>(tableView,new TablePosition<>(tableView,i,col),TableColumn.editCommitEvent() ,colData.replaceAll(searchContent,replaceContent)));
                            }

                            tableView.refresh();

                        }
                    });
                    controller.show();
                }catch (Exception e){

                }
            }
        });
        showHideItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setupAllColumn();
            }
        });

        sortItem.setDisable(!sort);
        changeItem.setDisable(!change);
        hideItem.setDisable(!hide);
        showHideItem.setDisable(!hide);
        searchItem.setDisable(!search);
        replaceItem.setDisable(!replace);

        headerMenu.getItems().addAll(sortItem,hideItem,searchItem,showHideItem);
        return headerMenu;
    }

    public void setupAllColumn(){
        tableView.getColumns().clear();
        if (tableType.equals("0")) {
            tableView.getColumns().addAll(codeCol, doneCol,videoDoneCol, contentCol, englishCol, yunCol, noteCol, rankCol, spellCol, IPACol, recordDateCol);
        }else if (tableType.equals("1")){
            tableView.getColumns().addAll(doneCol,videoDoneCol, codeCol, rankCol, contentCol, mwfyCol, IPACol, spellCol,englishCol, noteCol, recordDateCol);
        }else if (tableType.equals("2")){
            tableView.getColumns().addAll(doneCol,videoDoneCol, codeCol, rankCol, contentCol, mwfyCol, IPACol, freeTran, noteCol, englishCol, recordDateCol);
        }
    }

    public TableColumn searchColumn(ContextMenu menu){
        for (int i = 0; i < tableView.getColumns().size(); i++) {
            if (((TableColumn) tableView.getColumns().get(i)).contextMenuProperty().get().equals(menu)){
                return ((TableColumn) tableView.getColumns().get(i));
            }
        }
        return null;
    }

    private List getTableColumnNameList() {
        List<String> columnsName = new ArrayList<>();
        for (int i = 0;i<tableView.getColumns().size();i++){
            columnsName.add(tableView.getColumns().get(i).getText());
        }
        return columnsName;
    }

    public void refresh(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                recordDatas = FXCollections.observableArrayList(DbHelper.getInstance().searchTempRecordKeep(tableType,t.getId()));
                try {
                    tableView.getItems().clear();
                }catch (Exception e){
                    e.printStackTrace();
                }
                tableView.setItems(recordDatas);
                MainController.setStatusContent("刷新，共："+tableView.getItems().size()+"条");
            }
        });

    }

    private void defaultSetting(){
//        cb_resolution.getSelectionModel().select(0);
        List<String> cameraName= new ArrayList();

        if (AppCache.getInstance().getOsType()==0) {
            //此内容在windows下读取摄像头数量及名称
            int listDevices = org.bytedeco.javacpp.videoInputLib.videoInput.listDevices();
            for (int i = 0, max = listDevices; i < max; i++) {
                String deviceName = org.bytedeco.javacpp.videoInputLib.videoInput.getDeviceName(i).getString();
                cameraName.add(deviceName);
            }

        }else {
            int cameraCount = 0;
//            opencv_videoio.VideoCapture capture ;
//            while (true){
//                capture = new opencv_videoio.VideoCapture(cameraCount);
//                if (capture.isOpened()){
//                    cameraName.add(String.valueOf(cameraCount));
//                    cameraCount++;
//                    capture.close();
//                }else {
//                    break;
//                }
//            }
//            OpenCVFrameGrabber grabber;
//            while (true){
//                grabber = new OpenCVFrameGrabber(cameraCount);
//                try {
//                    grabber.start();
//                    grabber.grab();
//                    System.out.println(cameraCount);
//                    cameraName.add(cameraCount+"");
//                    cameraCount++;
//                    grabber=null;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    break;
//                }
//            }


        }
        cb_cameraName.setItems(FXCollections.observableArrayList(cameraName));
        cb_cameraName.getSelectionModel().select(0);

        //表格内容变化监听
        tableView.itemsProperty().addListener(new ChangeListener<ObservableList<Record>>() {
            @Override
            public void changed(ObservableValue<? extends ObservableList<Record>> observable, ObservableList<Record> oldValue, ObservableList<Record> newValue) {
                label_maxNumber.setText(newValue.size()+"");
            }
        });

        //输入行列
        input_number.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (isSelRow){
                    isSelRow = false;
                    return;
                }
                if (newValue.matches("\\d+")) {
                    tableView.getSelectionModel().clearSelection();
                    tableView.getSelectionModel().select(Integer.parseInt(newValue)-1);
                }
            }
        });

        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int selIndex = tableView.getSelectionModel().getSelectedIndex();
                if (selIndex != -1) {
                    Record r = tableView.getItems().get(selIndex);

                    if (tableView.getSelectionModel().getSelectedCells().size() != 0) {
                        TablePosition position = tableView.getSelectionModel().getSelectedCells().get(0);
                        tipTextArea.setText(((StringProperty) position.getTableColumn().getCellValueFactory().call(new TableColumn.CellDataFeatures<>(tableView, position.getTableColumn(), r))).get());
                    }
                }
            }
        });

        //点击表格 选中行
        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Record>() {
            @Override
            public void changed(ObservableValue observable, Record oldValue, Record newValue) {
                selRecord = newValue;
                int selIndex = tableView.getSelectionModel().getSelectedIndex();
                if (selIndex != -1){
                    Record r = tableView.getItems().get(selIndex);

                    if (tableView.getSelectionModel().getSelectedCells().size() != 0) {
                        TablePosition position = tableView.getSelectionModel().getSelectedCells().get(0);
                        tipTextArea.setText(((StringProperty) position.getTableColumn().getCellValueFactory().call(new TableColumn.CellDataFeatures<>(tableView, position.getTableColumn(), r))).get());
                    }

                    File demoPFile = FileUtil.searchFile( Constant.DEMO_PIC_DIR + "/",r.getUuid());
                    File demoVFile = FileUtil.searchFile(Constant.DEMO_Video_DIR + "/",r.getUuid());
                    String demoP = null;
                    String demoV = null;
                    if (demoPFile != null) demoP = demoPFile.getAbsolutePath();
                    if (demoVFile != null) demoV = demoVFile.getAbsolutePath();


                    if (demoP != null && demoP.length() != 0){
                        try {
                            FileInputStream fip = new FileInputStream(demoP);
                            Image img = new Image(fip);
                            demoImgView.setImage(img);
                            fip.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        demoImgView.setImage(null);
                    }
                    if (demoV != null && demoV.length() != 0){
                        demoVideoPlayer.setMediaPath(demoV);
                    }else {
                        demoVideoPlayer.setMediaPath("");
                    }

                    if (selRecord==null){
                        return;
                    }
//                    System.out.println(selRecord);
//                    System.out.println(getSelItemVideoPath(selRecord));
                    videoPlayer.setMediaPath(getSelItemVideoPath(selRecord));

                    resetChoiceBox();

                    isSelRow = true;
                    input_number.setText(selIndex+1+"");
                }
            }
        });
        //录音模式
        recordMode.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (((RadioButton) newValue).getText().equals("手动录音")){
                    autoRecordNext = false;
                }else if (((RadioButton) newValue).getText().equals("自动录音")){
                    autoRecordNext = true;
                }

            }
        });
        //自动录音 间隔
        cb_recordSpace.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue.intValue()!=-1){
                    recordTImeSpace = newValue.intValue()*2+2;
                }else {

                }
            }
        });


        //删除录音
        cb_delAudio.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                MainController.setStatusContent(cb_delAudio.getItems().get(Integer.parseInt(newValue.toString())).toString());
                if (newValue.intValue()==1){//删除选中条目
                    List<Record> selItems = tableView.getSelectionModel().getSelectedItems();
                    if (selItems!=null&&selItems.size()!=0){
                        for (Record r :
                                selItems) {
                            FileUtil.deleteFile(getSelItemAudioPath(r));
                            r.done= "0";
                            r.createDate="";
                        }
                        DbHelper.getInstance().updateRecord(selItems);
                    }
                }else if (newValue.intValue()==2){//删除全部
                    List<Record> records = tableView.getItems();
                    if (records!=null&&records.size()!=0){
                        for (Record r :
                                records) {
                            r.done= "0";
                            r.createDate="";
                            FileUtil.deleteFile(getSelItemAudioPath(r));
                        }
                        DbHelper.getInstance().updateRecord(records);

                    }
                }
                cb_delAudio.getSelectionModel().select(0);
                tableView.refresh();
            }
        });
        //删除视频
        cb_delVideo.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                MainController.setStatusContent(cb_delVideo.getItems().get(Integer.parseInt(newValue.toString())).toString());
                if (newValue.intValue()==1){//删除选中条目
                    List<Record> selItems = tableView.getSelectionModel().getSelectedItems();
                    if (selItems!=null&&selItems.size()!=0){
                        for (Record r :
                                selItems) {
                            FileUtil.deleteFile(getSelItemVideoPath(r));
                            tableView.refresh();
                        }
                    }
                }else if (newValue.intValue()==2){//删除全部
                    List<Record> records = tableView.getItems();
                    if (records!=null&&records.size()!=0){
                        for (Record r :
                                records) {
                            FileUtil.deleteFile(getSelItemVideoPath(r));
                        }
                        tableView.refresh();

                    }
                }
                cb_delVideo.getSelectionModel().select(0);
            }
        });
        //分辨率
        cb_resolution.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                String[] pix = ((String) cb_resolution.getItems().get((Integer) newValue)).split("\\*");
                int imageWidth = Integer.parseInt(pix[0]);
                int imageHeight = Integer.parseInt(pix[1]);
//                if (recordVideoThread!=null)
//                    recordVideoThread.setResolution(imageWidth,imageHeight);
                if (recorder!=null)
                    recorder.setResolution(imageWidth,imageHeight);
            }
        });
        //位深度
        cb_bit.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                String sizeInBits =((String)cb_bit.getItems().get((Integer) newValue));
//                String sizeInBits = (String) newValue;
//                if (recordAudioThread!=null)
//                    recordAudioThread.setSizeInBits(Integer.parseInt(sizeInBits));
                if (recorder!=null)
                    recorder.setSizeInBits(Integer.parseInt(sizeInBits));
            }
        });
        //采样率
        cb_sampleRate.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {

                String sampleRate = ((String)cb_sampleRate.getItems().get((Integer) newValue));
//                String sampleRate = (String) newValue;
//                if (recordAudioThread!=null)
//                    recordAudioThread.setSampleRate(Integer.parseInt(sampleRate));
                if (recorder!=null)
                    recorder.setSampleRate(Integer.parseInt(sampleRate));
            }
        });

        //摄像头
        cb_cameraName.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
//                if (recordVideoThread!=null)
//                    recordVideoThread.setWebCamIndex((Integer) newValue);
                if (recorder!=null)
                    recorder.setWebCamIndex((Integer) newValue);
            }
        });

        //显示记录
        cb_tableShowMode.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                MainController.setStatusContent(cb_tableShowMode.getItems().get(Integer.parseInt(newValue.toString())).toString());
                if (newValue.intValue()==0){//显示全部
                    tableView.setItems(recordDatas.filtered(new Predicate<Record>() {
                        @Override
                        public boolean test(Record record) {
                            System.out.println(record.baseCode);
                            return true;
                        }
                    }));
                    tableView.refresh();

                }else if (newValue.intValue()==1){//显示已录
                    tableView.setItems(recordDatas.filtered(new Predicate<Record>() {
                        @Override
                        public boolean test(Record record) {
                            File demoVFile = new File(getSelItemVideoPath(record));
                            if (record.done.equals("1") || demoVFile.exists()){
                                return true;
                            }
                            return false;
                        }
                    }));
                    tableView.refresh();

                }else if (newValue.intValue()==2){//显示未录
                    tableView.setItems(recordDatas.filtered(new Predicate<Record>() {
                        @Override
                        public boolean test(Record record) {
                            File demoVFile = new File(getSelItemVideoPath(record));
                            if (record.done.equals("0") && !demoVFile.exists()){
                                return true;
                            }
                            return false;
                        }
                    }));
                    tableView.refresh();
                }
            }
        });

        //导入音频
        cb_importAudio.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue.toString().equals("-1")
                        ||newValue.toString().endsWith("0")){
                    return;
                }
                MainController.setStatusContent(cb_importAudio.getItems().get(Integer.parseInt(newValue.toString())).toString());
                cb_importAudio.getSelectionModel().select(0);

                ObservableList temp = Integer.parseInt(newValue.toString()) == 1?tableView.getSelectionModel().getSelectedItems():tableView.getItems();
                if (temp != null && temp.size() != 0){
                    List<File> files = DialogUtil.chooseAudio(!newValue.toString().equals("1"));
                    if (files==null||files.size()==0){
                        return;
                    }

                    importMedia(temp,files,Integer.valueOf(newValue.toString()),true);
                }
            }
        });


        //导入视频
        cb_importVideo.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.toString().equals("-1")
                        ||newValue.toString().equals("0")){
                    return;
                }
                MainController.setStatusContent(cb_importVideo.getItems().get(Integer.parseInt(newValue.toString())).toString());
                cb_importVideo.getSelectionModel().select(0);

                ObservableList temp = Integer.parseInt(newValue.toString()) == 1?tableView.getSelectionModel().getSelectedItems():tableView.getItems();
                if (temp != null && temp.size() != 0) {
                    List<File> files = DialogUtil.chooseVideo(!newValue.toString().equals("1"));
                    if (files==null||files.size()==0){
                        return;
                    }
                    importMedia(temp, files, Integer.valueOf(newValue.toString()), false);
                }
            }
        });

        //导出音频
        cb_exportAudio.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.toString().equals("-1") || newValue.toString().equals("0")){
                    return;
                }
                MainController.setStatusContent(cb_exportAudio.getItems().get(Integer.parseInt(newValue.toString())).toString());
                cb_exportAudio.getSelectionModel().select(0);
                //1 编码导出选中
                //2 中文导出选中
                //3 英文导出选中
                //4 编码+中文导出选中
                //5 编码导出全部
                //6 中文导出全部
                //7 英文导出全部
                //8 编码+中文导出全部
                if (tableView.getSelectionModel().getSelectedItems().size() != 0 || newValue.intValue() >= 5){
                    File file = DialogUtil.selDir();
                    if (file != null){
                        List<Record> records ;
                        int type = newValue.intValue();

                        if (newValue.intValue()>=5){
                            type -=4;
                            records = tableView.getItems();
                        }else {
                            records = tableView.getSelectionModel().getSelectedItems();
                        }

                        try {
                            exportMedia(records,file,type,true);
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        //导出视频
        cb_exportVideo.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.toString().equals("-1") || newValue.toString().equals("0")){
                    return;
                }
                MainController.setStatusContent(cb_exportVideo.getItems().get(Integer.parseInt(newValue.toString())).toString());
                cb_exportVideo.getSelectionModel().select(0);
                if (tableView.getSelectionModel().getSelectedItems().size() != 0 || newValue.intValue() >= 4){
                    File file = DialogUtil.selDir();

                    //1 编码导出选中
                    //2 中文导出选中
                    //3 英文导出选中
                    //4 编码导出全部
                    //5 中文导出全部
                    //6 英文导出全部
                    int type = newValue.intValue();
                    List<Record> records;
                    if (type>=4){
                        type -= 3;
                        records = tableView.getItems();
                    }else {
                        records = tableView.getSelectionModel().getSelectedItems();
                    }
                    try {
                        exportMedia(records,file,type,false);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void onRecordFinish(Record record){

        record.createDate= record_simpleDateFormat.format(new Date());
        record.done = "1";
        DbHelper.getInstance().updateRecord(record);
        tableView.refresh();
    }

    private void importMedia(List<Record> records,List<File> files,int type,boolean audio){
        if (type == 1){
            Record record = records.get(0);
            if (audio) {
                copyFile(files.get(0).getPath(), Constant.getAudioPath(record.baseId + "", record.uuid));
                onRecordFinish(record);
            }else {
                copyFile(files.get(0).getPath(), Constant.getVideoPath(record.baseId + "", record.uuid));
            }
        }else if (type==2||type==3){
            for (int i=0,max=files.size();i<max;i++){
                for (int j=0,max2 = records.size();j<max2;j++){
                    System.out.println("record code:"+records.get(j).investCode);
                    System.out.println("file name:"+files.get(i).getName().split("\\.")[0]);
                    if (type==2&&records.get(j).investCode.equalsIgnoreCase(files.get(i).getName().split("\\.")[0])
                            ||type==3&&records.get(j).content.equalsIgnoreCase(files.get(i).getName().split("\\.")[0])
                            ) {
                        String path;
                        if (audio){
                            path = Constant.getAudioPath(records.get(j).baseId + "", records.get(j).uuid);
                            onRecordFinish(records.get(j));
                        }else {
                            path = Constant.getVideoPath(records.get(j).baseId + "", records.get(j).uuid);
                        }
                        copyFile(files.get(i).getPath(),path);
                    }
                }
            }
        }

        ToastUtil.show("导入完成");
        tableView.refresh();
    }

    private void exportMedia(List<Record> records,File dir,int type,boolean audio) throws NoSuchFieldException, IllegalAccessException {

        Field[] fields = new Field[0];
        if (type==0){//编码导出选中
            fields = new Field[]{Record.class.getDeclaredField("investCode")};
        }else if (type == 1){//中文导出选中
            fields = new Field[]{Record.class.getDeclaredField("content")};
        }else if (type == 2){//英文导出选中
            fields = new Field[]{Record.class.getDeclaredField("english")};
        }else if (type == 3){//编码+中文导出选中
            fields = new Field[]{Record.class.getDeclaredField("investCode"),Record.class.getDeclaredField("content")};
        }


        for (int i=0,max = records.size();i<max;i++){
            Record record = records.get(i);
            File source;
            if (audio){
                source = new File(Constant.getAudioPath(record.baseId+"",record.uuid));
            }else {
                source = new File(Constant.getVideoPath(record.baseId+"",record.uuid));
            }
            if (!source.exists()){
                continue;
            }
            String name = "";
            for (int j=0;j<fields.length;j++){
                name += fields[j].get(record);
                if (j!=fields.length-1){
                    name += "_";
                }
            }
            if (audio){
                name += Constant.AUDIO_SUFFIX;
            }else {
                name += Constant.VIDEO_SUFFIX;
            }
            File des = new File(dir.getPath()+"/"+name);

            FileUtil.copyFile(source,des);

        }
        ToastUtil.show("导出完成");

    }



    //播放音频
    @FXML
    private void onPlayAudioClick(){
        MainController.setStatusContent("播放音频");
        if (mediaPlayer==null){
            playAudio(btn_playAudio,"resource/img/b3.png","resource/img/b2.png",false);
        }else {
            if (mediaPlayer.getStatus()== MediaPlayer.Status.PLAYING){
                mediaPlayer.pause();
            }else if (mediaPlayer.getStatus()== MediaPlayer.Status.PAUSED
                    ||mediaPlayer.getStatus()==MediaPlayer.Status.STOPPED
                    ){
                mediaPlayer.play();
            }
        }
    }

    //播放下一条音频
    @FXML
    private void onPlayNextAudioClick(){
        if(mediaPlayer==null){
            int index = tableView.getSelectionModel().getSelectedIndex();
            if(index<tableView.getItems().size()){
                tableView.getSelectionModel().clearSelection();
                tableView.getSelectionModel().select(index);
                playAudio(btn_playNextAudio,"resource/img/b4.png","resource/img/b7.png",true);
            }
        }else {
            mediaPlayer.stop();
            ((ImageView) btn_playNextAudio.getGraphic()).setImage(new Image(Main.class.getResourceAsStream("resource/img/b4.png")));
            mediaPlayer.dispose();
            mediaPlayer = null;
//            if (mediaPlayer.getStatus()== MediaPlayer.Status.PLAYING){
//                mediaPlayer.pause();
//            }else if (mediaPlayer.getStatus()== MediaPlayer.Status.PAUSED
//                    ||mediaPlayer.getStatus()==MediaPlayer.Status.STOPPED
//                    ){
//                mediaPlayer.play();
//            }
        }
    }

    private void playAudio(Button btn,String norResourcePath,String selResourcePath,boolean playAfterRelease){
        File audioFile = new File(getSelItemAudioPath(selRecord));
        if (audioFile.exists()){
            mediaPlayer = new MediaPlayer(new Media(audioFile.toURI().toString()));

            mediaPlayer.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    mediaPlayer.stop();
                    ((ImageView) btn.getGraphic()).setImage(new Image(Main.class.getResourceAsStream(norResourcePath)));
//                    if (playAfterRelease){
                    mediaPlayer.dispose();
                    mediaPlayer = null;
                    if (btn == btn_playNextAudio){
                        tableView.getSelectionModel().selectNext();
                        onPlayNextAudioClick();
                    }


//                    }
                }
            });

            mediaPlayer.setOnPaused(new Runnable() {
                @Override
                public void run() {
                    ((ImageView) btn.getGraphic()).setImage(new Image(Main.class.getResourceAsStream(norResourcePath)));
                }
            });
            mediaPlayer.setOnPlaying(new Runnable() {
                @Override
                public void run() {
                    ((ImageView) btn.getGraphic()).setImage(new Image(Main.class.getResourceAsStream(selResourcePath)));

                }
            });
            mediaPlayer.play();

        }
    }


    LooperThread looperThread = new LooperThread();
    //录音
    @FXML
    private void onAudioClick(Event event){
        if (!vRecord.isRecording()){
            isRecordVideo = false;
            if (!aRecord.isRecording()){
                if (tableView.getSelectionModel().getSelectedItems().size() == 1){
                    selectRecord = tableView.getSelectionModel().getSelectedItem();
                    aRecord.setRecorders(new FFmpegFrameRecorder[]{aRecord.setupRecorderWithRecorder(getSelItemAudioPath(tableView.getSelectionModel().getSelectedItem()))},true);
                }
            }else {
                aRecord.stopRecorder(true);
            }

        }
    }
    //录像
    @FXML
    private void onVideoClick(Event event){
        if (!aRecord.isRecording() || (aRecord.isRecording() && vRecord.isRecording())){
            isRecordVideo = true;
            if (!vRecord.isRecording()){
                if (tableView.getSelectionModel().getSelectedItems().size() == 1){
                    selectRecord = tableView.getSelectionModel().getSelectedItem();
                    vRecord.setupRecorder(getSelItemVideoPath(tableView.getSelectionModel().getSelectedItem()),800,600);
                    if (cb_cover.isSelected()){
                        aRecord.setRecorders(new FFmpegFrameRecorder[]{vRecord.getRecorder(),aRecord.setupRecorderWithRecorder(getSelItemAudioPath(tableView.getSelectionModel().getSelectedItem()))},false);
                    }else{
                        aRecord.setRecorders(new FFmpegFrameRecorder[]{vRecord.getRecorder()},false);
                    }
                }
            }else {
                vRecord.stopRecorder();
                aRecord.stopRecorder(false);
            }
        }

    }

    private void setBtnVisableChange(){
        boolean isA = aRecord.isRecording();
        boolean isV = vRecord.isRecording();

        System.out.println(isA +"\t"+isV);

        btn_playAudio.setDisable(isA || isV);
        btn_playNextAudio.setDisable(isA || isV);

        if (vRecord.isRecording()){
            btn_recordVideo.setDisable(false);
            btn_recordAudio.setDisable(true);
        }else if (aRecord.isRecording()){
            btn_recordAudio.setDisable(false);
            btn_recordVideo.setDisable(true);
        }else{
            btn_recordAudio.setDisable(false);
            btn_recordVideo.setDisable(false);
        }

    }

    //获取tableview 选中行
    private List getTableViewSelItems(){
        return tableView.getSelectionModel().getSelectedItems();
    }
    //获取tableview 所有行
    private List getTableViewItems(){
        return tableView.getItems();
    }

    //获取tableview 选中行index，如果没有选中，默认第一行
    private int getSelIndex(){
        return tableView.getSelectionModel().getSelectedIndex();
    }
    //获取tableview 选中行开始  剩下的列表个数
    private int getLastCount(){
        int begin = getSelIndex();
        int end = getTableViewItems().size()-1;
        return  end-begin;
    }


    private void setBtnDisable(Button enableBtn , boolean sourceDisable,boolean allDisable){
        cb_cameraName.setDisable(allDisable);
        cb_sampleRate.setDisable(allDisable);
        cb_bit.setDisable(allDisable);
        cb_resolution.setDisable(allDisable);

        btn_recordVideo.setDisable(allDisable);
        btn_recordAudio.setDisable(allDisable);
        btn_playAudio.setDisable(allDisable);
        btn_playNextAudio.setDisable(allDisable);
        enableBtn.setDisable(sourceDisable);
    }




    private void startPreviewVideo(){
        vRecord = BeeVideoRecord.getInstance();
        vRecord.addImgView(img);
        vRecord.setListener(new BeeVideoRecord.VideoRecordListener() {
            @Override
            public void beginRecord() {
                System.out.println("begin record video");
                MainController.setStatusContent("开始视频录制");
                setBtnVisableChange();
            }

            @Override
            public void onRecording(long startTime, long nowRecordTime) {
//                System.out.println("recording:"+nowRecordTime);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        label_recordTime.setText(simpleDateFormat.format(nowRecordTime/1000));
                    }
                });
//                if (nowRecordTime >= 2 * 1000 * 1000){
//                    vRecord.stopRecorder();
//                    aRecord.stopRecorder(false);
//                    videoPlayer.setMediaPath(getSelItemVideoPath(selectRecord));
//                }
            }

            @Override
            public void finishRecord() {
                System.out.println("finish record video");
                MainController.setStatusContent("停止视频录制");
                if (cb_cover.isSelected()){
                    selectRecord.setCreateDate(record_simpleDateFormat.format(new Date()));
                }
                videoPlayer.setMediaPath(getSelItemVideoPath(selectRecord));
                tableView.refresh();
                setBtnVisableChange();
            }

            @Override
            public void errorRecord() {
                MainController.setStatusContent("视频录制发生错误");
                if (aRecord!=null){
                    aRecord.stopRecorder(false);
                }
            }
        });
    }

    private void startPreviewAudio(){
        aRecord = new BeeAudioRecord();
        aRecord.setListener(new BeeAudioRecord.AudioRecordListener() {

            @Override
            public void errorRecord() {
                MainController.setStatusContent("音频录制发生错误");
//                if (aRecord!=null){
//                    aRecord.stopRecorder(false);
//                }
            }

            @Override
            public void voiceDB(double db) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        pgb_pg1.setProgress(db/100.0);
                        pgb_pg2.setProgress(db/100.0);
                    }
                });
            }

            @Override
            public void beginRecording() {
                System.out.println("begin record audio");
                MainController.setStatusContent("开始音频录制");
                setBtnVisableChange();
            }

            @Override
            public void onRecording(long recordTime) {
//                System.out.println("recording audio:"+recordTime);
                if (!vRecord.isRecording()){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            label_recordTime.setText(simpleDateFormat.format(recordTime/1000));
                        }
                    });

                    if (autoRecordNext && recordTime >= Integer.parseInt(cb_recordSpace.getSelectionModel().getSelectedItem().toString()) * 1000 * 1000){
                        aRecord.stopRecorder(false);
                    }
                }
            }

            @Override
            public void finishRecording(boolean isStopFromUser) {
                System.out.println("finish record audio:"+isStopFromUser);
                if (!isRecordVideo){
                    MainController.setStatusContent("停止音频录制");
                    selectRecord.setDone("1");
                    selectRecord.setCreateDate(record_simpleDateFormat.format(new Date()));
                    DbHelper.getInstance().updateRecord(selectRecord);
                    tableView.refresh();

                    if (autoRecordNext && !isStopFromUser){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                int index = tableView.getSelectionModel().getSelectedIndex();
                                if (index != -1 && index != tableView.getItems().size() - 1){
                                    try {
                                        Thread.sleep(2000);

                                        TablePosition positin = null;
                                        if (tableView.getSelectionModel().getSelectedCells().size() == 1){
                                            positin = tableView.getSelectionModel().getSelectedCells().get(0);
                                            if (positin.getRow() < tableView.getItems().size() - 1){
                                                tableView.getSelectionModel().clearAndSelect(positin.getRow() + 1,positin.getTableColumn());
                                            }else {
                                                tableView.getSelectionModel().clearSelection();
                                            }
                                        }

                                        onAudioClick(null);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();
                    }
                }

                setBtnVisableChange();
            }
        });
    }

    public void stopPreview(){
//        vRecord.destroyRecorder();
        vRecord.removeImgView(img);
        aRecord.destroyRecorder();
    }

    private void showTimeOnLabel(long time){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                label_recordTime.setText(simpleDateFormat.format(time));
            }
        });
    }


    private void resetChoiceBox(){
        cb_importAudio.setValue("导入音频");
        cb_exportAudio.setValue("导出音频");
        cb_delAudio.setValue("删除音频");
        cb_importVideo.setValue("导入视频");
        cb_exportVideo.setValue("导出视频");
        cb_delVideo.setValue("删除视频");
    }


    //第一行
    @FXML
    private void toFirstLine(){
        if (tableView.getItems()!=null) {
            tableView.getSelectionModel().clearSelection();
            tableView.getSelectionModel().select(0);
        }
    }

    @FXML
    private void  toNextLine(){
        if (tableView.getItems()!=null) {
            int selIndex = tableView.getSelectionModel().getSelectedIndex()+1;
            if (selIndex>=tableView.getItems().size()){
                return;
            }
            System.out.println("sel index "+selIndex);
            tableView.getSelectionModel().clearSelection();
            tableView.getSelectionModel().select(selIndex);
        }
    }
    @FXML
    private void toPreviousLine(){
        if (tableView.getItems()!=null){
            int selIndex = tableView.getSelectionModel().getSelectedIndex()-1;
            tableView.getSelectionModel().clearSelection();
            tableView.getSelectionModel().select(selIndex);
        }
    }
    @FXML
    private void toLastLine(){
        if (tableView.getItems()!=null){
            tableView.getSelectionModel().clearSelection();
            tableView.getSelectionModel().select(tableView.getItems().size()-1);
        }
    }

    private String getSelItemAudioPath(Record record){
        return Constant.ROOT_FILE_DIR+"/audio/"+getAudioNameWithoutSuffix(record)+Constant.AUDIO_SUFFIX;
    }

    private String getAudioNameWithoutSuffix(Record record){
        return record.baseId+"/"+record.uuid;
    }

    private String getSelItemVideoPath(Record record){
        return Constant.ROOT_FILE_DIR+"/video/"+getVideoPathWithoutSuffix(record)+Constant.VIDEO_SUFFIX;
    }


    private String getVideoPathWithoutSuffix(Record record){
        return record.baseId+"/"+record.uuid;
    }



    public class LooperThread implements Runnable{
        TimerThread timerThread;

        int currentIndex = 0;
        int maxIndex = 0;
        boolean isRecording;

        Thread thread;
        public LooperThread() {
            timerThread = new TimerThread();
            timerThread.setLooperThread(this);
            timerThread.setRecordFinishCallback(new RecordFinishCallback() {
                @Override
                public void finish() {
                    if (isRecording) {
                        System.out.println("++");
                        toNextLine();
                        currentIndex++;

                    }

                    thread = null;


                }
            });
        }
        @Override
        public void run() {
            currentIndex = 0;

            while (currentIndex<=maxIndex&&isRecording){


                if (currentIndex>=maxIndex){
                    isRecording = false;
                }

                if (thread==null){
                    thread = new Thread(timerThread);
                    thread.setName("单次录音线程");
                    thread.start();
                }


                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            isRecording = false;

        }

        public void stopRecord(){

            timerThread.isRecording =false;
            this.isRecording = false;
        }

    }

    public class TimerThread implements Runnable{

        LooperThread looperThread;
        TimerStatus status = TimerStatus.STOP;
        long startTime;
        int recordTime = 2;
        boolean isRecording= false;//外部控制跳出循环
        private RecordFinishCallback recordFinishCallback;
        @Override
        public void run() {
//            System.out.println("TimerThread 开始录音");
            status = TimerStatus.START;

            isRecording = true;

//            recordOneAudio(btn_recordAudio);
            Thread t1 = new Thread(){
                @Override
                public void run() {
                    super.run();
//                    recordOneAudio(btn_recordAudio);
                }
            };
            t1.start();
//            while (!recorder.isStartRecord()){
//                System.out.println("1");
//            }
            startTime = System.currentTimeMillis();


            //循环等待
            while (parseRecordTime() < (recordTime*1000)&&isRecording){
//                System.out.println("recording timer");
//                System.out.println(new Date(System.currentTimeMillis()-startTime).getSeconds());
                status = TimerStatus.RECORDING;
            }


            System.out.println("TimerThread 录音时间 "+new Date(System.currentTimeMillis()-startTime).getSeconds());
            System.out.println("TimerThread 录音结束");
            resetTimeLabel();
//            stopRecordAudio();
            Thread t2 = new Thread(){
                @Override
                public void run() {
                    super.run();
//                    recordOneAudio(btn_recordAudio);
                }
            };
            t2.start();


            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//            while (recorder.isStartRecord()){
//                System.out.println("2");
//            }

//            if (t1.isAlive()) {
//                t1.stop();
//
//            }
//            if (t2.isAlive()){
//                t2.stop();
//            }
//            t1=null;
//            t2=null;


            isRecording = false;
            if (recordFinishCallback!=null)
                recordFinishCallback.finish();

            status = TimerStatus.STOP;


        }

        public void setLooperThread(LooperThread looperThread) {
            this.looperThread = looperThread;
        }

        public void setRecordFinishCallback(RecordFinishCallback recordFinishCallback) {
            this.recordFinishCallback = recordFinishCallback;
        }


    }
    public long parseRecordTime(){
        String time = label_recordTime.getText();
        String min = time.split(":")[0];
        String secAndMs = time.split(":")[1];

        long ms = Long.valueOf(secAndMs.split("\\.")[0])*1000;
//        System.out.println(ms);
        return ms;
    }
    public enum TimerStatus {
        RECORDING,START,STOP
    }
    public interface RecordFinishCallback{
        void finish();
    }

    //录制视频时长最长两分钟
    public class VideoTimerThread implements Runnable{
        RecordFinishCallback recordFinishCallback;
        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            long currentTime = 0;
            while (currentTime-startTime<2*60*1000){
                System.out.println(currentTime-startTime);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                currentTime = System.currentTimeMillis();
            }

            recordFinishCallback.finish();
        }
        public void setRecordFinishCallback(RecordFinishCallback callback){
            this.recordFinishCallback = callback;
        }
    }



    public void copyFile(String sourcePath,String desPath){
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()){
            return;
        }
//        File desFile = new File(desPath+"/"+sourceFile.getName());

        try {
            File desFile = new File(desPath);
            File desParent = desFile.getParentFile();
            if (!desParent.exists()){
                desParent.mkdirs();
            }

            Files.copy(sourceFile.toPath(),desFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void resetTimeLabel(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                label_recordTime.setText("00:00.000");
            }
        });
    }

    private String getMHSearRegEx(String str){
        StringBuilder sb = new StringBuilder(str);
        for (int i = str.length() - 1; i > 0; i--) {
            sb.insert(i,".*");
        }
        return sb.toString();
    }

    private class MyCell extends TextFieldTableCell{
        @Override
        public void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);
            super.updateItem(item, empty);
            if (empty && getIndex()<0){

            }else {
                if (getTableRow() != null && getTableRow().getIndex() < getTableViewItems().size()) {
                    String colName = getTableColumn().getText();
                    Record r = ((Record) getTableViewItems().get(getTableRow().getIndex()));
                    if (colName.equalsIgnoreCase("录音状态")){
                        if (r.getDone().equalsIgnoreCase("0")){
                            setStyle("-fx-text-fill: #ff0000");
                        }else {
                            setStyle("-fx-text-fill: #000000");
                        }
                    }else if (colName.equalsIgnoreCase("录像状态")){
                        File f = new File(Constant.ROOT_FILE_DIR+"/video/"+t.getId()+"/"+r.getUuid()+".mp4");
                        if (!f.exists()){
                            setStyle("-fx-text-fill: #ff0000");
                        }else {
                            setStyle("-fx-text-fill: #000000");
                        }
                    }
                }
            }
        }
    }
}
