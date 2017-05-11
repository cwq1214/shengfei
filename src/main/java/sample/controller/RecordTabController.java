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
import java.net.URL;
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
import sample.controller.widget.VideoPlayer;
import sample.entity.Record;
import sample.util.*;

/**
 * Created by chenweiqi on 2017/4/18.
 */
public class RecordTabController extends BaseController {

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


//    VideoRecorder recordVideoThread;
//    AudioRecorder recordAudioThread;
    Recorder recorder;

    //要录音的数据
    private ObservableList<Record> recordDatas;

    long startRecordTimes;

    //保存的文件名称
    String fileName;

    Record selRecord;

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
            tableView.getColumns().addAll(codeCol, doneCol, contentCol, englishCol, yunCol, noteCol, rankCol, spellCol, IPACol, recordDateCol);
        }else if (tableType.equals("1")){
            tableView.getColumns().addAll(doneCol, codeCol, rankCol, contentCol, mwfyCol, IPACol, spellCol,englishCol, noteCol, recordDateCol);
        }else if (tableType.equals("2")){
            tableView.getColumns().addAll(doneCol, codeCol, rankCol, contentCol, mwfyCol, IPACol, freeTran, noteCol, englishCol, recordDateCol);
        }

        tableView.setItems(recordDatas);
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
                switch (newValue.intValue()){
                    case 0:
                        recordTImeSpace = 2;
                        break;
                    case 1:
                        recordTImeSpace = 4;

                        break;
                    case 2:
                        recordTImeSpace = 6;

                        break;
                }
            }
        });


        //删除录音
        cb_delAudio.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue()==0){//删除选中条目
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
                }else if (newValue.intValue()==1){//删除全部
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
                if (newValue.intValue()==0){//删除选中条目
                    List<Record> selItems = tableView.getSelectionModel().getSelectedItems();
                    if (selItems!=null&&selItems.size()!=0){
                        for (Record r :
                                selItems) {
                            FileUtil.deleteFile(getSelItemVideoPath(r));

                        }
//                        DbHelper.getInstance().updateRecord(selItems);
                    }
                }else if (newValue.intValue()==1){//删除全部
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
                if (maxIndex==0){
                    return;
                }
                looperThread.maxIndex = getLastCount();
                looperThread.isRecording = true;
                new Thread(looperThread).start();

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
        cb_importAudio.setValue(null);
        cb_exportAudio.setValue(null);
        cb_delAudio.setValue(null);
        cb_importVideo.setValue(null);
        cb_exportVideo.setValue(null);
        cb_delVideo.setValue(null);
    }

    //录制一条音频
    private void recordOneAudio(Button btn){
        Image image;
        if (selRecord==null){
            return;
        }

        if (!recordingAudio){
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

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            selRecord.createDate= simpleDateFormat.format(new Date());
            selRecord.done = "1";
            DbHelper.getInstance().updateRecord(selRecord);
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

    //开始录制音频
    private void startRecordAudio(){
        Image image;
        if (selRecord==null){
            return;
        }

        if (!recordingAudio){
            recordingAudio = true;
//            recordAudioThread.startRecordAudio(selRecord.baseId+"/"+selRecord.uuid);
            recorder.startRecordAudio(getAudioNameWithoutSuffix(selRecord));
            image = new Image(Main.class.getResourceAsStream("/sample/resource/img/b5.png"));
            setBtnDisable(btn_recordAudio,false,true);

            System.out.println("开始录音 recordOneAudio");
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(24);
            imageView.setFitHeight(24);

            Platform.runLater(()->{
                btn_recordAudio.graphicProperty().setValue(imageView);
            });
        }

    }
    //停止录制音频
    private void stopRecordAudio(){
        Image image;
        if (selRecord==null){
            return;
        }

        if (recordingAudio){
            startRecordTimes = 0;
            recordingAudio = false;
//            recordAudioThread.stopRecordAudio();
            recorder.stopRecordAudio();
            System.out.println("停止录音 recordOneAudio");

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            selRecord.createDate= simpleDateFormat.format(new Date());
            selRecord.done = "1";
            DbHelper.getInstance().updateRecord(selRecord);
            tableView.refresh();
            image = new Image(Main.class.getResourceAsStream("/sample/resource/img/b1.png"));
            setBtnDisable(btn_recordAudio,false,false);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(24);
            imageView.setFitHeight(24);

            Platform.runLater(()->{
                btn_recordAudio.graphicProperty().setValue(imageView);
            });
        }

    }
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
        }else {
            startRecordTimes = 0;
            recordingVideo = false;
            recorder.stopRecord();
            DbHelper.getInstance().updateRecord(selRecord);
            tableView.refresh();
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
        boolean isRecording= false;
        private RecordFinishCallback recordFinishCallback;
        @Override
        public void run() {
            System.out.println("TimerThread 开始录音");
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
            while (System.currentTimeMillis()-startTime<recordTime*1000&&isRecording){
                System.out.println("recording timer");
                System.out.println(new Date(System.currentTimeMillis()-startTime).getSeconds());
                status = TimerStatus.RECORDING;
            }
            System.out.println("TimerThread 录音时间 "+new Date(System.currentTimeMillis()-startTime).getSeconds());
            System.out.println("TimerThread 录音结束");

//            stopRecordAudio();
            Thread t2 = new Thread(){
                @Override
                public void run() {
                    super.run();
                    recordOneAudio(btn_recordAudio);
                }
            };
            t2.start();


//            while (recorder.isStartRecord()){
//                System.out.println("2");
//            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (t1.isAlive()) {
                t1.stop();

            }
            if (t2.isAlive()){
                t2.stop();
            }
            t1=null;
            t2=null;


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
    public enum TimerStatus {
        RECORDING,START,STOP
    }
    public interface RecordFinishCallback{
        void finish();
    }
}
