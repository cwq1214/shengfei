package sample.controller.AV;

import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameRecorder;
import sample.Main;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.sound.sampled.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
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
public class BeeAudioRecord {

    private FFmpegFrameRecorder[] recorders;
    private long startTime = 0;
    private boolean isStop = true;
    private boolean isStopFromUser = false;

    private TargetDataLine line;
    private double deltaDB = 0;
    private int caculateDeltaTime = 0;
    private final int collectTime = 30;

    private AudioRecordListener listener;

    public BeeAudioRecord() {
        AudioFormat audioFormat = new AudioFormat(44100.0F, 16, 2, true, false);

        // 通过AudioSystem获取本地音频混合器信息
        Mixer.Info[] minfoSet = AudioSystem.getMixerInfo();
        // 通过AudioSystem获取本地音频混合器
        Mixer mixer = AudioSystem.getMixer(minfoSet[4]);
        // 通过设置好的音频编解码器获取数据线信息
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
        try {
            // 打开并开始捕获音频
            // 通过line可以获得更多控制权
            // 获取设备：TargetDataLine line
            // =(TargetDataLine)mixer.getLine(dataLineInfo);
            line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            line.open(audioFormat);
            line.start();
            // 获得当前音频采样率
            int sampleRate = (int) audioFormat.getSampleRate();
            // 获取当前音频通道数量
            int numChannels = audioFormat.getChannels();
            // 初始化音频缓冲区(size是音频采样率*通道数)
            int audioBufferSize = sampleRate * numChannels;
            byte[] audioBytes = new byte[audioBufferSize];

            ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
            exec.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 非阻塞方式读取
                        int nBytesRead = line.read(audioBytes, 0, line.available());
                        // 因为我们设置的是16位音频格式,所以需要将byte[]转成short[]
                        int nSamplesRead = nBytesRead / 2;
                        short[] samples = new short[nSamplesRead];
                        /**
                         * ByteBuffer.wrap(audioBytes)-将byte[]数组包装到缓冲区
                         * ByteBuffer.order(ByteOrder)-按little-endian修改字节顺序，解码器定义的
                         * ByteBuffer.asShortBuffer()-创建一个新的short[]缓冲区
                         * ShortBuffer.get(samples)-将缓冲区里short数据传输到short[]
                         */
                        ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
                        // 将short[]包装到ShortBuffer
                        ShortBuffer sBuff = ShortBuffer.wrap(samples, 0, nSamplesRead);
                        // 按通道录制shortBuffer

                        if (caculateDeltaTime < collectTime){
                            if (caculateDeltaTime > 9){
                                deltaDB += caculateVoiceDB(audioBytes,nBytesRead);
                            }
                            caculateDeltaTime++;
                        }else{
                            listener.voiceDB(caculateVoiceDB(audioBytes,nBytesRead));
                        }

                        if (!isStop){
                            if (startTime == 0){
                                startTime = System.currentTimeMillis();
                                if (listener != null){
                                    listener.beginRecording();
                                }
                            }

                            for (FFmpegFrameRecorder rc :recorders) {
                                rc.recordSamples(sampleRate, numChannels, sBuff);
                            }

                            if (listener != null){
                                listener.onRecording(1000 * (System.currentTimeMillis() - startTime));
                            }
                        }


                    } catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, (long) 1000 / FRAME_RATE, TimeUnit.MILLISECONDS);
        } catch (LineUnavailableException e1) {
            e1.printStackTrace();
        }
    }

    private double caculateVoiceDB(byte[] datas,int realUser){
        //计算RMS
        long allPow = 0;
        for (int i = 0; i < realUser; i++) {
            allPow += datas[i] * datas[i];
        }

        if (allPow != 0){
            double rms = Math.sqrt(allPow / realUser);
            if (rms == 1){
                rms = 10;
            }
            double db = Math.log10(rms)*20;
            if (caculateDeltaTime < collectTime){
                return db == Double.NEGATIVE_INFINITY?34:db;
            }else {
                return db + (db - deltaDB / ((collectTime - 10) * 1.0))*15;
            }
        }
        return 0;
    }

    public boolean isRecording(){
        return startTime != 0;
    }

    public void setListener(AudioRecordListener listener) {
        this.listener = listener;
    }

    public void stopRecorder(boolean isFromUser){
        isStop = true;
        startTime = 0;
        isStopFromUser = isFromUser;

        if (recorders != null){
            for (FFmpegFrameRecorder rc :recorders) {
                try {
                    rc.stop();
                } catch (FrameRecorder.Exception e) {
                    e.printStackTrace();
                }
            }

            recorders = null;

            if (listener != null){
                listener.finishRecording(isStopFromUser);
            }
        }


    }

    public void destroyRecorder(){
        stopRecorder(false);
        line.close();
        line.stop();
    }

    public void setRecorders(FFmpegFrameRecorder[] recorders,boolean playSound) {
        this.recorders = recorders;
        if (playSound){
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
                        isStop = false;
                    }
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public FFmpegFrameRecorder setupRecorderWithRecorder(String fileName){
        FFmpegFrameRecorder rc = new FFmpegFrameRecorder(fileName,2);
        rc.setFormat("wav");
        try {
            rc.start();
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        }
        return rc;
    }

    public interface AudioRecordListener{
        public void beginRecording();
        public void onRecording (long recordTime);
        public void finishRecording(boolean isStopFromUser);
        public void voiceDB(double db);
    }
}
