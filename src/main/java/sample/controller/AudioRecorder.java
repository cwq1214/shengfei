package sample.controller;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import sample.controller.RecordTabController;
import sample.util.Constant;

import javax.sound.sampled.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.Arrays;

/**
 * Created by chenweiqi on 2017/4/21.
 */
public class AudioRecorder extends Thread {


    //预览音量
    private boolean showAudio;
    //是否改变录音参数
    private boolean changeAudioParameter;
    //录音中
    private boolean recordingAudio = false;
    //
    private boolean startRecordAudio;

    private FFmpegFrameRecorder recorder;

    //采样率
    private int sampleRate = 44100;
    //位深度
    private int sizeInBits = 16;
    //音频通道数
    private int numChannels = 2;
    //从音频捕获设备获取其数据的数据行
    private TargetDataLine line;
    //开始时间
    private long startRecordTimes = 0;

    private RecordTimeCallback timeCallback;
    private RecordVolumeCallback volumeCallback;

    private String fileName;

    public AudioRecorder() {

    }

    @Override
    public void run() {
        super.run();
        try {
            showAudio = true;
            changeAudioParameter = true;
//            timmerThread = new RecordTabController.TimmerThread();
//            timmerThread.start();

//            AudioSystem.write(
//                    new AudioInputStream(line),
//                    targetType,
//                    new File(System.currentTimeMillis()+"audio.wav"));
            // 初始化音频缓冲区(size是音频采样率*通道数)
            int audioBufferSize ;

            byte[] audioBytes =null;
            ShortBuffer sBuff = null;
            int nBytesRead;
            int nSamplesRead;
            while (showAudio) {

                if (changeAudioParameter){
                    if (line != null){
                        line.close();
                        line=null;
                    }
//                    AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,sampleRate,sizeInBits,2,4,sampleRate,false);
                    AudioFormat audioFormat =   new AudioFormat(sampleRate,sizeInBits,2,true,false);
                    DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
                    try {
                        line= (TargetDataLine) AudioSystem.getLine(info);
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                    }

                    line.open(audioFormat);
                    AudioFileFormat.Type	targetType = AudioFileFormat.Type.WAVE;
                    line.start();
                    // 获得当前音频采样率
                    int sampleRate = (int) audioFormat.getSampleRate();
                    System.out.println("sampleRate "+sampleRate);
                    System.out.println("sampleSizeInBitseRate "+audioFormat.getSampleSizeInBits());
                    // 获取当前音频通道数量
                    int numChannels = audioFormat.getChannels();
                    audioBufferSize = sampleRate * numChannels;
                    audioBytes = new byte[audioBufferSize];
                    changeAudioParameter = false;
                }

                // 非阻塞方式读取
                int len = line.available();
//                if (len>0)
//                    System.out.println("len "+len);
                if (len>audioBytes.length){
                    len = audioBytes.length;
                }
                nBytesRead = line.read(audioBytes, 0, len);


                if (volumeCallback!=null){
                    volumeCallback.callback(calculateRMSLevel(audioBytes));
                }

//                if (line.isControlSupported(FloatControl.Type.VOLUME)){
//                    FloatControl floatControl = (FloatControl) line.getControl(FloatControl.Type.VOLUME);
//                    System.out.println(floatControl.getValue());
//                }else {
//                    System.out.println(Arrays.toString(line.getControls()));
//                    System.out.println("not support");
//                }
                if (recordingAudio){
                    if (startRecordAudio){
                        String filePath = Constant.ROOT_FILE_DIR+"/audio/"+fileName+"_"+System.currentTimeMillis()+".wav";
                        recorder = initRecorder(filePath);
                        recorder.start();
                        startRecordAudio = false;
                        startRecordTimes = System.currentTimeMillis();
                    }
                    // 按通道录制shortBuffer
                    try {
                        // 因为我们设置的是16位音频格式,所以需要将byte[]转成short[]
                        nSamplesRead = nBytesRead / 2;
                        short[] samples = new short[nSamplesRead];
                        /**
                         * ByteBuffer.wrap(audioBytes)-将byte[]数组包装到缓冲区
                         * ByteBuffer.order(ByteOrder)-按little-endian修改字节顺序，解码器定义的
                         * ByteBuffer.asShortBuffer()-创建一个新的short[]缓冲区
                         * ShortBuffer.get(samples)-将缓冲区里short数据传输到short[]
                         */
                        ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
                        // 将short[]包装到ShortBuffer
                        sBuff = ShortBuffer.wrap(samples, 0, nSamplesRead);
                        recorder.recordSamples(sampleRate, numChannels, sBuff);
                    } catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
                        // do nothing
                    }
                }else {
                    startRecordTimes = 0;
                    if (recorder!=null){
                        recorder.close();
                        recorder = null;
                    }

                }

                if (startRecordTimes!=0&&timeCallback!=null){
                    timeCallback.callback(System.currentTimeMillis()-startRecordTimes);
                }
                Thread.sleep(100);

            }

                if (line!=null)
                    line.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //开始录制
    public void startRecordAudio(String fileName){
        this.fileName = fileName;
        startRecordAudio = true;
        recordingAudio = true;

    }

    //停止录制
    public void stopRecordAudio(){
        recordingAudio = false;
    }

    //设置采样率
    public void setSampleRate(int sampleRate){
        this.sampleRate = sampleRate;
        System.out.println("setSampleRate "+ sampleRate);
        changeAudioParameter = true;
    }
    //设置位深度
    public void setSizeInBits(int sizeInBits){
        this.sizeInBits = sizeInBits;
        System.out.println("setSizeInBits "+sizeInBits);
        changeAudioParameter = true;
    }

    public void setTimeCallback(RecordTimeCallback timeCallback) {
        this.timeCallback = timeCallback;
    }

    public void setVolumeCallback(RecordVolumeCallback volumeCallback) {
        this.volumeCallback = volumeCallback;
    }



    private FFmpegFrameRecorder initRecorder(String fileName) throws IOException {
        File parentFile =new File(fileName).getParentFile();
        if (!parentFile.exists()){
            parentFile.mkdirs();
        }


        recorder= new FFmpegFrameRecorder(fileName,2);

//        // 不可变(固定)音频比特率
//        recorder.setAudioOption("crf", "0");
//        // 最高质量
//        recorder.setAudioQuality(0);
//        // 音频比特率
//        recorder.setAudioBitrate(192000);
        // 音频采样率
        recorder.setSampleRate(sampleRate);
        // 双通道(立体声)
        recorder.setAudioChannels(2);
        // 音频编/解码器
//        recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
//        recorder.setInterleaved(true);
        return recorder;
    }

    protected static int calculateRMSLevel(byte[] audioData)
    { // audioData might be buffered data read from a data line
        long lSum = 0;
        for(int i=0; i<audioData.length; i++)
            lSum = lSum + audioData[i];

        double dAvg = lSum / audioData.length;

        double sumMeanSquare = 0d;
        for(int j=0; j<audioData.length; j++)
            sumMeanSquare = sumMeanSquare + Math.pow(audioData[j] - dAvg, 2d);

        double averageMeanSquare = sumMeanSquare / audioData.length;
        return (int)(Math.pow(averageMeanSquare,0.5d) + 0.5);
    }



    public interface RecordVolumeCallback{
        void callback(int volume);
    }
    public interface RecordTimeCallback{
        void callback(long times);
    }
}
