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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javafx.util.Callback;
import org.bytedeco.javacpp.opencv_videoio;
import org.bytedeco.javacv.*;
import sample.Main;
import sample.controller.YBCC.YBCCBean;
import sample.entity.Record;
import sample.util.AppCache;
import sample.util.AudioRecorder;
import sample.util.Constant;
import sample.util.VideoRecorder;

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
    Button btn_playRecord;
    @FXML
    Button btn_nextLine2;
    @FXML
    ProgressBar pgb_pg1;
    @FXML
    ProgressBar pgb_pg2;
    @FXML
    TableView tableView;


    public String tableType;

    //是否录像
    boolean recordingVideo = false;
    //是否音中
    boolean recordingAudio = false;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss.SSS");


    VideoRecorder recordVideoThread;
    AudioRecorder recordAudioThread;


    //要录音的数据
    private ObservableList<Record> recordDatas;

    long startRecordTimes;

    //保存的文件名称
    String fileName;

    public ObservableList<Record> getRecordDatas() {
        return recordDatas;
    }

    public void setRecordDatas(ObservableList<Record> recordDatas) {
        this.recordDatas = recordDatas;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

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


        //分辨率
        cb_resolution.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                String[] pix = ((String) cb_resolution.getItems().get((Integer) newValue)).split("\\*");
                int imageWidth = Integer.parseInt(pix[0]);
                int imageHeight = Integer.parseInt(pix[1]);
                if (recordVideoThread!=null)
                    recordVideoThread.setResolution(imageWidth,imageHeight);
            }
        });

        //位深度
        cb_bit.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                String sizeInBits =((String)cb_bit.getItems().get((Integer) newValue));
//                String sizeInBits = (String) newValue;
                if (recordAudioThread!=null)
                    recordAudioThread.setSizeInBits(Integer.parseInt(sizeInBits));
            }
        });
        //采样率
        cb_sampleRate.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {

                String sampleRate = ((String)cb_sampleRate.getItems().get((Integer) newValue));
//                String sampleRate = (String) newValue;
                if (recordAudioThread!=null)
                    recordAudioThread.setSampleRate(Integer.parseInt(sampleRate));
            }
        });

        //摄像头
        cb_cameraName.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (recordVideoThread!=null)
                    recordVideoThread.setWebCamIndex((Integer) newValue);
            }
        });
    }

    @FXML
    private void onAudioClick(Event event){
        Image image;
        if (!recordingAudio){
            recordingAudio = true;
            recordAudioThread.startRecordAudio("11");
            image = new Image(Main.class.getResourceAsStream("/sample/resource/img/b5.png"));
            setBtnDisable((Button) event.getSource(),false,true);


        }else {
            startRecordTimes = 0;
            recordingAudio = false;


            recordAudioThread.stopRecordAudio();

            image = new Image(Main.class.getResourceAsStream("/sample/resource/img/b1.png"));
            setBtnDisable((Button) event.getSource(),false,false);

        }
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(24);
        imageView.setFitHeight(24);
        btn_recordAudio.graphicProperty().setValue(imageView);

    }
    @FXML
    private void onVideoClick(Event event){
        Image image;
        if (!recordingVideo){
            recordingVideo = true;
            recordVideoThread.startRecord("11");
            image = new Image(Main.class.getResourceAsStream("/sample/resource/img/b5.png"));
            setBtnDisable((Button) event.getSource(),false,true);
        }else {
            startRecordTimes = 0;
            recordingVideo = false;
            recordVideoThread.stopRecord();
//            if (timmerThread!=null)
//                timmerThread.stop();
            image = new Image(Main.class.getResourceAsStream("/sample/resource/img/b6.png"));
            setBtnDisable((Button) event.getSource(),false,false);
        }
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(24);
        imageView.setFitHeight(24);
        btn_recordVideo.graphicProperty().setValue(imageView);


    }

    private void setBtnDisable(Button enableBtn , boolean sourceDisable,boolean allDisable){
        cb_cameraName.setDisable(allDisable);
        cb_sampleRate.setDisable(allDisable);
        cb_bit.setDisable(allDisable);
        cb_resolution.setDisable(allDisable);

        btn_recordVideo.setDisable(allDisable);
        btn_recordAudio.setDisable(allDisable);
        btn_nextLine2.setDisable(allDisable);
        btn_playRecord.setDisable(allDisable);
        enableBtn.setDisable(sourceDisable);
    }


    private void startPreviewVideo(){
        recordVideoThread = new VideoRecorder();
        recordVideoThread.setPreviewImageView(img);
        recordVideoThread.setTimeCallback(new VideoRecorder.RecordTimeCallback() {
            @Override
            public void recordTime(long timeMS) {
                showTimeOnLabel(timeMS);
            }
        });
        recordVideoThread.start();

    }

    private void startPreviewAudio(){
        recordAudioThread = new AudioRecorder();
        recordAudioThread.setTimeCallback(new AudioRecorder.RecordTimeCallback() {
            @Override
            public void callback(long times) {
                showTimeOnLabel(times);
            }
        });
        recordAudioThread.setVolumeCallback(new AudioRecorder.RecordVolumeCallback() {
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
        recordAudioThread.start();
    }

    public void stopPreview(){
        if (recordAudioThread!=null)
            recordAudioThread.stopPreview();
        if (recordVideoThread!=null)
            recordVideoThread.stopPreview();
    }

    private void showTimeOnLabel(long time){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                label_recordTime.setText(simpleDateFormat.format(time));
            }
        });    }

}
