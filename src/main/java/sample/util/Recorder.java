package sample.util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.*;
import sample.Main;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * Created by chenweiqi on 2017/5/4.
 */
public class Recorder extends Thread {

    //预览控件
    ImageView img;
    //保存路径
    String fileName;
    //进行录音覆盖
    boolean cover = false;
    RecordTimeCallback timeCallback;
    //分辨率 宽
//    private int imageWidth = 1280;
    private int imageWidth = 800;
    //分辨率 高
//    private int imageHeight = 720;
    private int imageHeight = 600;
    //是否预览
    private boolean startPreview;
    //是否改变分辨率
    private boolean initVideoParameter;
    //是否录像
    private boolean recordingVideo = false;
    //开始录像
    private boolean startRecordVideo;
    //转换器
    private OpenCVFrameConverter.ToIplImage toIplImage = new OpenCVFrameConverter.ToIplImage();
    //摄像头抓取器
    private OpenCVFrameGrabber grabber;
    //录制器
    private FFmpegFrameRecorder recorder;
    //摄像头index
    private int webcam_index = 0;

    FFmpegFrameFilter fFmpegFrameFilter;


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
    //音量回调
    private RecordVolumeCallback volumeCallback;

    //预览音量
    private boolean showAudio;
    //是否改变录音参数
    private boolean changeAudioParameter;
    //录音中
    private boolean recordingAudio = false;
    //
    private boolean startRecordAudio;
    //覆盖录音
    private FFmpegFrameRecorder coverRecorder;

    private boolean isStartRecord = false;
    MediaPlayer mediaPlayer;
    AudioStream as;
    public Recorder() {
        try {
            mediaPlayer = new MediaPlayer(new Media(Main.class.getResource("resource/sound/14.wav").toURI().toString()));
            as = new AudioStream(Main.class.getResourceAsStream("resource/sound/14.wav"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        super.run();
        try {
            startPreview = true;
            initVideoParameter = true;
            changeAudioParameter = true;

            // 初始化音频缓冲区(size是音频采样率*通道数)
            int audioBufferSize ;

            byte[] audioBytes =null;
            ShortBuffer sBuff = null;
            int nBytesRead;
            int nSamplesRead;

            while (startPreview) {
                if (initVideoParameter) {//修改视频参数
                    grabber = null;
                    Thread.sleep(1000);
                    grabber = new OpenCVFrameGrabber(webcam_index);
                    grabber.setImageWidth(imageWidth);
                    grabber.setImageHeight(imageHeight);
                    grabber.setFrameRate(60);
                    grabber.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
                    grabber.setVideoCodec(avcodec.AV_CODEC_ID_H264);
                    System.out.println("initVideoParameter" + imageHeight);
                    System.out.println("initVideoParameter" + imageWidth);
                    grabber.start();
                    initVideoParameter = false;
                }
                if (changeAudioParameter){//修改音频参数
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
                    // 获取当前音频通道数量
                    int numChannels = audioFormat.getChannels();
                    audioBufferSize = sampleRate * numChannels;
                    audioBytes = new byte[audioBufferSize];
                    changeAudioParameter = false;
                }

                // 非阻塞方式读取
                int len = line.available();
                if (len>audioBytes.length){
                    len = audioBytes.length;
                }
                nBytesRead = line.read(audioBytes, 0, len);


                if (volumeCallback!=null){
                    volumeCallback.callback(calculateRMSLevel(audioBytes));
                }
                long timeStamp = System.currentTimeMillis();

//                grabber.setTimestamp(timeStamp);
                //获取摄像头帧
                Frame frame = grabber.grab();
                opencv_core.IplImage iplImage = toIplImage.convert(frame);


                //是否录制视频
                if (recordingVideo) {
                    if (startRecordVideo) {
                        System.out.println("startRecordVideo");
                        recorder = initRecorder(Constant.ROOT_FILE_DIR+"/video/"+fileName+Constant.VIDEO_SUFFIX,true);
                        recorder.start();
                        playSound();
                        startRecordVideo = false;
                        startRecordTimes = System.currentTimeMillis();

                    }

//                    System.out.println("frame width " + frame.imageWidth);
//                    System.out.println("frame height " + frame.imageHeight);

                    recorder.record(frame);
                    nSamplesRead = nBytesRead / 2;
                    short[] samples = new short[nSamplesRead];
                    ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
                    sBuff = ShortBuffer.wrap(samples, 0, nSamplesRead);
                    recorder.recordSamples(sampleRate, numChannels, sBuff);

                } else if (recordingAudio){//是否录制音频
                    if (startRecordAudio){
//                        System.out.println("startRecordAudio");
//                        System.out.println(this.fileName);
                        recorder = initRecorder(Constant.ROOT_FILE_DIR+"/audio/"+fileName+Constant.AUDIO_SUFFIX,true);
                        recorder.start();

                        Thread.sleep(250);
                        playSound();
                        startRecordAudio = false;
                        startRecordTimes = System.currentTimeMillis();

                    }
                    try {
                        nSamplesRead = nBytesRead / 2;
                        short[] samples = new short[nSamplesRead];
                        ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
                        sBuff = ShortBuffer.wrap(samples, 0, nSamplesRead);

                        isStartRecord = true;
//                        System.out.println("record");
                        recorder.recordSamples(sampleRate, numChannels, sBuff);
                    } catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
                        // do nothing
                        e.printStackTrace();
                    }
                }else {
                    startRecordTimes = 0;
                    if (recorder!=null){
//                        System.out.println("close recorder");
                        recorder.close();
                        isStartRecord = false;
                        recorder = null;

                    }

                    if (coverRecorder!=null){
                        coverRecorder.close();
                        coverRecorder=null;
                    }

                    if(cover){
//                        System.out.println("cover");
                        FileUtil.copyFile(new File(Constant.ROOT_FILE_DIR+"/video/"+fileName+Constant.VIDEO_SUFFIX)
                                ,new File(Constant.ROOT_FILE_DIR+"/audio/"+fileName+Constant.AUDIO_SUFFIX));

                        cover = false;
                    }

                }



                //预览摄像头
                if (img != null) {
                    BufferedImage image = IplImageToBufferedImage(iplImage);
                    img.setImage(SwingFXUtils.toFXImage(image, new WritableImage(image.getWidth(), image.getHeight())));
                }
                if (startRecordTimes != 0&&timeCallback!=null) {
                    long sub = System.currentTimeMillis() - startRecordTimes;
                    if (timeCallback != null)
                        timeCallback.recordTime(sub);
                }
                Thread.sleep(100);
            }

            if (line!=null){
                line.close();
            }

            if (grabber != null) {
                grabber.stop();
                grabber.release();
                grabber = null;

                System.gc();
            }

//                    Thread.sleep(100);

        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show(e.getMessage());

            throw new RuntimeException("");
        }
    }


    //修改分辨率
    public void setResolution(int width, int height) {
        this.imageWidth = width;
        this.imageHeight = height;
        initVideoParameter = true;
    }

    //更换摄像头
    public void setWebCamIndex(int index) {
        this.webcam_index = index;
        initVideoParameter = true;
    }

    //开始录像
    public void startRecordVideo(String fileName, boolean cover) {
        this.fileName = fileName;
        startRecordVideo = true;
        recordingVideo = true;
        this.cover = cover;

    }

    //停止录像
    public void stopRecord() {

        recordingVideo = false;
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

    //设置预览控件
    public void setPreviewImageView(ImageView img) {
        this.img = img;
    }

    //录制时间监听
    public void setTimeCallback(RecordTimeCallback timeCallback) {
        this.timeCallback = timeCallback;
    }

    public void setVolumeCallback(RecordVolumeCallback volumeCallback) {
        this.volumeCallback = volumeCallback;
    }

    //初始化recorder
    private FFmpegFrameRecorder initRecorder(String fileName,boolean haveImage) {
        File file = new File(fileName);
        if (!file.exists()){
            file.getParentFile().mkdirs();
        }
        if (haveImage){



            recorder = new FFmpegFrameRecorder(fileName, grabber.getImageWidth(), grabber.getImageHeight(), 2);
            recorder.setVideoOption("tune", "zerolatency");
            recorder.setVideoOption("preset", "ultrafast");
            recorder.setVideoOption("crf","28");
            recorder.setInterleaved(true);
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
            recorder.setSampleRate(sampleRate);
//            recorder.setAudioBitrate(192000);
//            recorder.setVideoBitrate(2000000);
//            recorder.setFrameRate(grabber.getFrameRate());
            recorder.setFrameRate(grabber.getFrameRate()/5);
            recorder.setFormat("MP4");
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
//            recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
        }else {


            recorder = new FFmpegFrameRecorder(fileName, 2);
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
            recorder.setSampleRate(sampleRate);
            // 双通道(立体声)
            recorder.setAudioChannels(2);
        }



        return recorder;

    }


    public BufferedImage IplImageToBufferedImage(opencv_core.IplImage src) {
        OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();
        Java2DFrameConverter paintConverter = new Java2DFrameConverter();
        Frame frame = grabberConverter.convert(src);
        return paintConverter.getBufferedImage(frame, 1);
    }
    private   int calculateRMSLevel(byte[] audioData)
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

    public boolean isStartRecord() {
        return isStartRecord;
    }

    public void stopPreview() {
        startPreview = false;
    }


    public interface RecordTimeCallback {
        void recordTime(long timeMS);
    }

    public interface RecordVolumeCallback {
        void callback(int volume);
    }



    public void playSound(){
        System.out.println("play sound");
        new Thread(){
            @Override
            public void run() {
                super.run();
//                if (AudioPlayer.player.isAlive()){
//                    AudioPlayer.player.stop(as);
//                }
                try {
                    AudioPlayer.player.start(new AudioStream(Main.class.getResourceAsStream("resource/sound/14.wav")));
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                if (mediaPlayer==null){
//                    return;
//                }
//                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING){
//                    mediaPlayer.stop();
//                }
//                mediaPlayer.play();
            }
        }.start();

    }
}
