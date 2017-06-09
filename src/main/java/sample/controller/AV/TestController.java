package sample.controller.AV;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import org.bytedeco.javacv.*;
import sample.controller.BaseController;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Bee on 2017/6/5.
 */
public class TestController extends BaseController {

    @FXML
    private ImageView imgView;

    private Boolean isStop = false;

    private Java2DFrameConverter converter = new Java2DFrameConverter();

    private BeeVideoRecord vRecord;
    private BeeAudioRecord aRecord;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vRecord = BeeVideoRecord.getInstance();
//                vRecord.addImgView(imgView);
            }
        });
    }

    @FXML
    public void recordBtnClick(){
        vRecord.addImgView(imgView);

//        aRecord.setupRecorder("d://test.wav");

        vRecord.setupRecorder("d://output.mp4",800,600);
////        aRecord.setupRecorder(vRecord.getRecorder());
//        aRecord.setListener(new BeeAudioRecord.AudioRecordListener() {
//            @Override
//            public void voiceDB(double db) {
//
//            }
//
//            @Override
//            public void beginRecording() {
//                System.out.println("begin record audio");
//            }
//
//            @Override
//            public void onRecording(long recordTime) {
//                System.out.println(recordTime);
//            }
//
//            @Override
//            public void finishRecording(boolean isStopFromUser) {
//                System.out.println("finish record audio");
//            }
//        });
        vRecord.setListener(new BeeVideoRecord.VideoRecordListener() {
            @Override
            public void beginRecord() {
                System.out.println("begin record video");
            }

            @Override
            public void onRecording(long startTime, long nowRecordTime) {
                System.out.println(startTime+"====="+nowRecordTime);
                if (nowRecordTime >= 2 * 1000 * 1000){
                    vRecord.stopRecorder();
                }
            }

            @Override
            public void finishRecord() {
                System.out.println("finish record video");
            }

            @Override
            public void errorRecord() {
//                aRecord.stopRecorder();
            }
        });
    }

    @FXML
    public void stopBtnClick(){
//        vRecord.stopRecorder();
//        aRecord.stopRecorder();
        vRecord.removeImgView(imgView);
    }

}
