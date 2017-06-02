package sample.controller;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
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

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Callback;
import org.apache.poi.xwpf.usermodel.TOC;
import sample.Main;
import sample.controller.YBCC.YBCCBean;
import sample.controller.widget.VideoPlayer;
import sample.entity.Record;
import sample.entity.Table;
import sample.util.*;

/**
 * Created by chenweiqi on 2017/4/18.
 */
public class RecordTabController extends BaseController {

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
    }

    public void startPreview(){
        defaultSetting();
        startPreviewVideo();
        startPreviewAudio();

        setupTablView();
    }

    public void setupTablView(){
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
        codeCol.setCellValueFactory(new PropertyValueFactory<>("investCode"));
        rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
        yunCol.setCellValueFactory(new PropertyValueFactory<>("yun"));
        IPACol.setCellValueFactory(new PropertyValueFactory<>("IPA"));
        spellCol.setCellValueFactory(new PropertyValueFactory<>("spell"));
        englishCol.setCellValueFactory(new PropertyValueFactory<>("english"));
        noteCol.setCellValueFactory(new PropertyValueFactory<>("note"));
        recordDateCol.setCellValueFactory(new PropertyValueFactory<>("createDate"));
        contentCol.setCellValueFactory(new PropertyValueFactory<>("content"));
        mwfyCol.setCellValueFactory(new PropertyValueFactory<>("MWFY"));
        freeTran.setCellValueFactory(new PropertyValueFactory<>("free_trans"));

        if (tableType.equals("0")) {
            tableView.getColumns().addAll(codeCol, doneCol,videoDoneCol, contentCol, englishCol, yunCol, noteCol, rankCol, spellCol, IPACol, recordDateCol);
        }else if (tableType.equals("1")){
            tableView.getColumns().addAll(doneCol,videoDoneCol, codeCol, rankCol, contentCol, mwfyCol, IPACol, spellCol,englishCol, noteCol, recordDateCol);
        }else if (tableType.equals("2")){
            tableView.getColumns().addAll(doneCol,videoDoneCol, codeCol, rankCol, contentCol, mwfyCol, IPACol, freeTran, noteCol, englishCol, recordDateCol);
        }

        recordDatas = DbHelper.getInstance().searchTempRecordKeep(tableType,t.getId());

        tableView.setItems(recordDatas);
    }

    public void refresh(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                recordDatas = DbHelper.getInstance().searchTempRecordKeep(tableType,t.getId());
                try {
                    tableView.getItems().clear();

                }catch (Exception e){
                    e.printStackTrace();
                }
                tableView.setItems(recordDatas);
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
        //点击表格 选中行
        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Record>() {
            @Override
            public void changed(ObservableValue observable, Record oldValue, Record newValue) {
                selRecord = newValue;
                int selIndex = tableView.getSelectionModel().getSelectedIndex();
                Record r = tableView.getItems().get(selIndex);

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
                System.out.println(selRecord);
                if (mediaPlayer!=null){
                    if (mediaPlayer.getStatus()== MediaPlayer.Status.PLAYING){
                        mediaPlayer.stop();
                        ((ImageView) btn_playAudio.getGraphic()).setImage(new Image(Main.class.getResourceAsStream("resource/img/b3.png")));

                    }
                    mediaPlayer = null;
                }
                videoPlayer.setMediaPath(getSelItemVideoPath(selRecord));

                resetChoiceBox();

                isSelRow = true;
                input_number.setText(selIndex+1+"");
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

                tableView.refresh();
            }
        });
        //删除视频
        cb_delVideo.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue()==1){//删除选中条目
                    List<Record> selItems = tableView.getSelectionModel().getSelectedItems();
                    if (selItems!=null&&selItems.size()!=0){
                        for (Record r :
                                selItems) {
                            FileUtil.deleteFile(getSelItemVideoPath(r));

                        }
//                        DbHelper.getInstance().updateRecord(selItems);
                    }
                }else if (newValue.intValue()==2){//删除全部
                    List<Record> records = tableView.getItems();
                    if (records!=null&&records.size()!=0){
                        for (Record r :
                                records) {
                            FileUtil.deleteFile(getSelItemVideoPath(r));
                        }
//                        DbHelper.getInstance().updateRecord(records);

                    }
                }
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
                System.out.println(newValue.toString());
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
                            if (record.done.equals("1")){
                                System.out.println(record.baseCode);
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
                            if (record.done.equals("0")){
                                System.out.println(record.baseCode);
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
                List<File> files = DialogUtil.chooseAudio(!newValue.toString().equals("1"));
                if (files==null||files.size()==0){
                    return;
                }
                importMedia(tableView.getSelectionModel().getSelectedItems(),files,Integer.valueOf(newValue.toString()),true);
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
                List<File> files = DialogUtil.chooseVideo(!newValue.toString().equals("1"));
                if (files==null||files.size()==0){
                    return;
                }
                importMedia(tableView.getSelectionModel().getSelectedItems(),files,Integer.valueOf(newValue.toString()),false);
            }
        });

        //导出音频
        cb_exportAudio.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.toString().equals("-1")){
                    return;
                }
                //0 编码导出选中
                //1 中文导出选中
                //2 英文导出选中
                //3 编码+中文导出选中
                //4 编码导出全部
                //5 中文导出全部
                //6 英文导出全部
                //7 编码+中文导出全部
                File file = DialogUtil.selDir();
                List<Record> records ;
                int type = newValue.intValue();

                if (newValue.intValue()>=4){
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
        });
        //导出视频
        cb_exportVideo.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.toString().equals("-1")){
                    return;
                }
                File file = DialogUtil.selDir();

                //0 编码导出选中
                //1 中文导出选中
                //2 英文导出选中
                //3 编码导出全部
                //4 中文导出全部
                //5 英文导出全部
                int type = newValue.intValue();
                List<Record> records;
                if (type>=3){
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
        });
    }

    private void onRecordFinish(Record record){

        record.createDate= record_simpleDateFormat.format(new Date());
        record.done = "1";
        DbHelper.getInstance().updateRecord(record);
        tableView.refresh();
    }

    private void importMedia(List<Record> records,List<File> files,int type,boolean audio){
        if (type == 0){
            Record record = records.get(0);
            if (audio) {
                copyFile(files.get(0).getPath(), Constant.getAudioPath(record.baseId + "", record.uuid));
                onRecordFinish(record);
            }else {
                copyFile(files.get(0).getPath(), Constant.getVideoPath(record.baseId + "", record.uuid));
            }
        }else if (type == 1
                ||type==2){
            for (int i=0,max=files.size();i<max;i++){
                for (int j=0,max2 = records.size();j<max2;j++){
                    if (type==1&&records.get(j).investCode.equals(files.get(i).getName().split("\\.")[0])
                            ||type==2&&records.get(j).content.equals(files.get(i).getName().split("\\.")[0])
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
            if(index<tableView.getItems().size()-1){
                tableView.getSelectionModel().clearSelection();
                tableView.getSelectionModel().select(index+1);
                playAudio(btn_playNextAudio,"resource/img/b4.png","resource/img/b7.png",true);
            }
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
                        mediaPlayer = null;
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
        if (autoRecordNext){
            if (!looperThread.isRecording) {
                int maxIndex = getTableViewItems().size()-1;
                if (maxIndex<=0){
                    return;
                }
                looperThread.maxIndex = getLastCount();
                looperThread.isRecording = true;
                Thread thread =new Thread(looperThread);
                thread.setName("自动录音线程");
                thread.start();

            }else {
                looperThread.stopRecord();
            }
        }else {
            recordOneAudio((Button) event.getSource());
        }

    }
    //录像
    @FXML
    private void onVideoClick(Event event){


        recordOneVideo((Button) event.getSource());


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
//        recordVideoThread = new VideoRecorder();
//        recordVideoThread.setPreviewImageView(img);
//        recordVideoThread.setTimeCallback(new VideoRecorder.RecordTimeCallback() {
//            @Override
//            public void recordTime(long timeMS) {
//                showTimeOnLabel(timeMS);
//            }
//        });
//        recordVideoThread.start();

        recorder = new Recorder();
        recorder.setName("录音线程");
        recorder.setPreviewImageView(img);
        recorder.setTimeCallback(new Recorder.RecordTimeCallback() {
            @Override
            public void recordTime(long timeMS) {
                showTimeOnLabel(timeMS);
            }
        });
        recorder.setVolumeCallback(new Recorder.RecordVolumeCallback() {
            @Override
            public void callback(int volume) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        pgb_pg1.setProgress(volume*1f/100);
                        pgb_pg2.setProgress(volume*1f/100);
                    }
                });
            }
        });

        recorder.start();

    }

    private void startPreviewAudio(){
//        recordAudioThread = new AudioRecorder();
//        recordAudioThread.setTimeCallback(new AudioRecorder.RecordTimeCallback() {
//            @Override
//            public void callback(long times) {
//                showTimeOnLabel(times);
//            }
//        });
//        recordAudioThread.setVolumeCallback(new AudioRecorder.RecordVolumeCallback() {
//            @Override
//            public void callback(int volume) {
//                Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        pgb_pg1.setProgress(volume*1f/100);
//                        pgb_pg2.setProgress(volume*1f/100);
//                    }
//                });
//            }
//        });
//        recordAudioThread.start();
    }

    public void stopPreview(){
//        if (recordAudioThread!=null)
//            recordAudioThread.stopPreview();
//        if (recordVideoThread!=null)
//            recordVideoThread.stopPreview();
        if (recorder!=null){
            recorder.stopPreview();
        }
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

    //录制一条音频
    private void recordOneAudio(Button btn){
        Image image;
        if (selRecord==null){
            return;
        }

        if (!recordingAudio){
            resetTimeLabel();
            recordingAudio = true;
//            recordAudioThread.startRecordAudio(selRecord.baseId+"/"+selRecord.uuid);
            recorder.startRecordAudio(getAudioNameWithoutSuffix(selRecord));
            image = new Image(Main.class.getResourceAsStream("/sample/resource/img/b5.png"));
            setBtnDisable(btn,false,true);

            System.out.println("开始录音 recordOneAudio");


        }else {
            startRecordTimes = 0;
            recordingAudio = false;
//            recordAudioThread.stopRecordAudio();
            recorder.stopRecordAudio();
            System.out.println("停止录音 recordOneAudio");

            onRecordFinish(selRecord);
            tableView.refresh();
            image = new Image(Main.class.getResourceAsStream("/sample/resource/img/b1.png"));
            setBtnDisable(btn,false,false);

        }
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(24);
        imageView.setFitHeight(24);

        Platform.runLater(()->{
            btn_recordAudio.graphicProperty().setValue(imageView);
        });
    }


    Thread thread;
    //录制一条视频
    private void recordOneVideo(Button btn){
        Image image;
        if (selRecord==null){
            return;
        }
        if (!recordingVideo){
            recordingVideo = true;
//            recordVideoThread.startRecordVideo(getSelItemVideoPath(),cb_cover.getState());
            recorder.startRecordVideo(getVideoPathWithoutSuffix(selRecord),cb_cover.isSelected());
            image = new Image(Main.class.getResourceAsStream("/sample/resource/img/b5.png"));
            setBtnDisable(btn,false,true);
            videoTimerThread = new VideoTimerThread();
            videoTimerThread.setRecordFinishCallback(new RecordFinishCallback() {
                @Override
                public void finish() {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            recordOneVideo(btn_recordVideo);
                            if (thread!=null) {
                                thread.stop();
                            }
                            thread = null;
                        }
                    });

                }
            });
            thread = new Thread(videoTimerThread);
            thread.start();

        }else {
            if (thread!=null){
                thread.stop();
            }
            thread = null;

            startRecordTimes = 0;
            recordingVideo = false;
            recorder.stopRecord();
//            DbHelper.getInstance().updateRecord(selRecord);
            tableView.refresh();

            videoPlayer.setMediaPath(getSelItemVideoPath(selRecord));


            image = new Image(Main.class.getResourceAsStream("/sample/resource/img/b6.png"));
            setBtnDisable(btn,false,false);
        }
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(24);
        imageView.setFitHeight(24);
        btn_recordVideo.graphicProperty().setValue(imageView);

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
                    recordOneAudio(btn_recordAudio);
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
                    recordOneAudio(btn_recordAudio);
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
}
