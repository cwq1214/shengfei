package sample.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.bytedeco.javacpp.opencv_videoio;
import sample.Main;
import sample.util.AppCache;
import sample.util.Constant;

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



    //是否录像
    boolean recordingVideo = false;
    //是否音中
    boolean recordingAudio = false;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss.SSS");


    VideoRecorder recordVideoThread;
    AudioRecorder recordAudioThread;



    long startRecordTimes;

    //保存的文件名称
    String fileName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        defaultSetting();
        startPreviewVideo();
        startPreviewAudio();
    }

    private void defaultSetting(){
//        cb_resolution.getSelectionModel().select(0);
        List<String> cameraName= new ArrayList();

        if (AppCache.getInstance().getOsType()==0) {
            //此内容在windows下读取摄像头数量及名称
            int listDevices = org.bytedeco.javacpp.videoInputLib.videoInput.listDevices();
            for (int i = 0, max = listDevices; i < max; i++) {
                String deviceName = org.bytedeco.javacpp.videoInputLib.videoInput.getDeviceName(i).getString();
                System.out.println(deviceName);
            }

        }else {
            int cameraCount = 0;

            opencv_videoio.VideoCapture capture = new opencv_videoio.VideoCapture();
            while (true){
                capture.open(cameraCount);
                if (capture.isOpened()){
                    cameraName.add(String.valueOf(cameraCount));
                    cameraCount++;
                }else {
                    break;
                }
            }
        }
        cb_cameraName.getItems().add(cameraName);
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
            recordAudioThread.startRecordAudio(Constant.ROOT_FILE_DIR+"/"+fileName);
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
            recordVideoThread.startRecord(Constant.ROOT_FILE_DIR+"/"+fileName);
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


    private void showTimeOnLabel(long time){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                label_recordTime.setText(simpleDateFormat.format(time));
            }
        });    }

}
