package sample.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.avformat;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.*;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;


import static java.lang.Double.NaN;


/**
 * Created by chenweiqi on 2017/5/11.
 */
public class WAVUtil {

    static WAVUtil wavUtil;

    private  WAVUtil(){

    }

    public static synchronized WAVUtil getInstance(){
        if (wavUtil==null)
            wavUtil = new WAVUtil();
        return wavUtil;
    }

    public void join(String savePath, List<String> filesPath) throws FrameRecorder.Exception, FrameGrabber.Exception {
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(savePath,2);
        FFmpegFrameGrabber grabber = null;
        Frame audioSamples;
        boolean in= true;
        for (int i=0,max = filesPath.size();i<max;i++) {

            if (grabber == null){
                grabber = new FFmpegFrameGrabber(new File(filesPath.get(i)));
                if (in) {

                    recorder.setVideoOption("tune", "zerolatency");
                    recorder.setVideoOption("preset", "ultrafast");
                    recorder.setVideoOption("crf","28");
                    recorder.setInterleaved(true);
                    recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
                    recorder.setFormat("mp4");
                    recorder.setSampleRate(44100);
                    recorder.setAudioChannels(2);
                    recorder.start();

                    in = false;
                    System.out.println("start");
                }
                grabber.start();
            }

            System.out.println("" + filesPath.get(i));

            while ((audioSamples = grabber.grabSamples())!=null){
//                recorder.setTimestamp(grabber.getTimestamp());
                recorder.record(audioSamples);
            }

            try {
                grabber.close();
                grabber = null;
            } catch (FrameGrabber.Exception e) {
                e.printStackTrace();
            }
        }
        recorder.close();


    }


    public void dis(String source,List<String> fileName,List<String> times){
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(source);

        FFmpegFrameRecorder recorder=null;

        Frame audioSamples;
        for (int i=0,max = fileName.size();i<max;i++){

            if (recorder ==null){
                recorder = new FFmpegFrameRecorder(fileName.get(i),2);

            }
            try {
                while ( (audioSamples=grabber.grab())!=null){

                    recorder.record(audioSamples);
                }
            } catch (FrameGrabber.Exception e) {
                e.printStackTrace();
            } catch (FrameRecorder.Exception e) {
                e.printStackTrace();
            }
        }

    }

    //返回时长 单位:毫秒
    public double getAudioTimeLine(String filePath) {
        File file = new File(filePath);
        if (file.exists()){
            double mis=0;
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file);
            try {
                grabber.start();
                mis = grabber.getFormatContext().duration();
                System.out.println(mis);
                grabber.close();
            } catch (FrameGrabber.Exception e) {
                e.printStackTrace();
            }

            return mis/1000;
        }

        return 0;
    }




}
