package sample.controller.AV;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.*;
import sample.Main;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.bytedeco.javacpp.FlyCapture2.FRAME_RATE;

/**
 * Created by Bee on 2017/6/5.
 */
public class BeeVideoRecord {
    private ImageView nowImgView;
    private Java2DFrameConverter converter = new Java2DFrameConverter();
    private Boolean isStop = false;
    private boolean isDestroy = false;
    private Boolean canRecordVideo = false;
    private Boolean canRecordAudio = false;
    private Thread thread;

    private FrameGrabber grabber;
    private FFmpegFrameRecorder recorder;

    private long startTime = 0;
    private long videoTS = 0;

    private VideoRecordListener listener;

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
        isDestroy = true;
        stopRecorder();
    }

    public BeeVideoRecord(ImageView nowImgView) {
        this.nowImgView = nowImgView;
        new Thread(new Runnable() {
            @Override
            public void run() {
                init();
            }
        }).start();
    }

    public void setupRecorder(String outPutFile,int width,int height){
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
        nowImgView.setImage(SwingFXUtils.toFXImage(img,null));
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
            isDestroy = false;
            grabber = FrameGrabber.createDefault(0);
            grabber.start();

            System.out.println("new:"+grabber);

            Frame frame;
            while (!isDestroy && nowImgView != null && (frame = grabber.grab()) != null) {
                BufferedImage img = converter.convert(frame);
                videoRecording(frame, img);
            }
            stopRecorder();
            grabber.stop();
            grabber.close();
            grabber.release();
        } catch (FrameGrabber.Exception e) {
            System.out.println("wrong:"+this);
//            e.printStackTrace();
        }
    }


    public interface VideoRecordListener{
        public void beginRecord();
        public void onRecording(long startTime,long nowRecordTime);
        public void finishRecord();
        public void errorRecord();
    }
}
