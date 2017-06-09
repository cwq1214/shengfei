package sample.controller.AV;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.*;
import sample.Main;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Bee on 2017/6/5.
 */
public class BeeVideoRecord {
    static BeeVideoRecord record;

    private ArrayList<ImageView> imgViews;
    private Java2DFrameConverter converter = new Java2DFrameConverter();
    private boolean isStop = false;
    private Boolean canRecordVideo = false;

    private FrameGrabber grabber;
    private FFmpegFrameRecorder recorder;
    private Runnable runnable;

    private long startTime = 0;
    private long videoTS = 0;

    private VideoRecordListener listener;

    public static synchronized BeeVideoRecord getInstance(){
        if (record == null){
            record = new BeeVideoRecord();
        }
        return record;
    }

    public boolean isUsedNow(){
        return imgViews.size() != 0;
    }

    public void addImgView(ImageView iv){
        if (imgViews.size() == 0){
            try {
                if (grabber == null){
                    grabber = FrameGrabber.createDefault(0);
                    grabber.start();
                }else{
                    grabber.restart();
                }
                isStop = false;
                new Thread(runnable).start();
            } catch (FrameGrabber.Exception e) {
                e.printStackTrace();
            }
        }
        imgViews.add(iv);
    }

    public void removeImgView(ImageView iv){
        imgViews.remove(iv);
        if (imgViews.size() == 0){
            isStop = true;
        }
    }

    public FFmpegFrameRecorder getRecorder() {
        return recorder;
    }

    public boolean isRecording(){
        return startTime != 0;
    }

    public void setListener(VideoRecordListener listener) {
        this.listener = listener;
    }

    public void destroyRecorder(){
        isStop = true;
        stopRecorder();
    }

    public BeeVideoRecord() {
        imgViews = new ArrayList<>();
        runnable = new Runnable() {
            @Override
            public void run() {
                init();
            }
        };
    }

    public void setupRecorder(String outPutFile,int width,int height){
        File oFile = new File(outPutFile).getParentFile();
        if (!oFile.exists()){
            oFile.mkdirs();
        }

        try {
            recorder = FFmpegFrameRecorder.createDefault(outPutFile,width,height);
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setFrameRate(30);
            recorder.setVideoQuality(20);
            recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
            recorder.setAudioBitrate(192000);
            recorder.setSampleRate(44100);
            recorder.setAudioChannels(2);
            recorder.start();

            try {
                AudioPlayer.player.start(new AudioStream(Main.class.getResourceAsStream("resource/sound/14.wav")));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        canRecordVideo = true;
                        if (listener != null){
                            listener.beginRecord();
                        }
                    }
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
            stopRecorder();

            if (listener != null){
                listener.errorRecord();
            }
        }
    }

    public void stopRecorder(){
        if (recorder != null){
            try {
                canRecordVideo = false;
                recorder.stop();
                recorder.release();
                startTime = 0;
                videoTS = 0;
                recorder = null;

                if (listener != null){
                    listener.finishRecord();
                }
            } catch (FrameRecorder.Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void videoRecording(Frame frame,BufferedImage img) {
        if (imgViews != null){
            for (ImageView iv : imgViews) {
                iv.setImage(SwingFXUtils.toFXImage(img,null));
            }
        }
        if (canRecordVideo){
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }
            videoTS = 1000 * (System.currentTimeMillis() - startTime);

            if (videoTS > recorder.getTimestamp()) {
                recorder.setTimestamp(videoTS);
                try {
                    recorder.record(frame);
                    if (listener != null){
                        listener.onRecording(startTime,videoTS);
                    }
                } catch (FrameRecorder.Exception e) {
                    e.printStackTrace();
                    if (listener != null){
                        listener.errorRecord();
                    }
                }
            }
        }
    }

    private void init() {
        try {
            Frame frame;
            while (!isStop && (frame = grabber.grab()) != null) {
                BufferedImage img = converter.convert(frame);
                videoRecording(frame, img);
            }
            grabber.close();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
    }


    public interface VideoRecordListener{
        public void beginRecord();
        public void onRecording(long startTime,long nowRecordTime);
        public void finishRecord();
        public void errorRecord();
    }
}
