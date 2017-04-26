package sample.util;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.*;
import sample.util.AppCache;
import sample.util.Constant;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by chenweiqi on 2017/4/21.
 */
public class VideoRecorder extends Thread {
    //分辨率 宽
    private int imageWidth = 1280;
    //分辨率 高
    private int imageHeight = 720;

    //是否预览摄像头视频
    private boolean showVideo;
    //是否改变分辨率
    private boolean initVideoParameter;
    //是否录像
    private boolean recordingVideo = false;
    //开始录像
    private boolean startRecordVideo;

    //转换器
    private OpenCVFrameConverter.ToIplImage toIplImage = new OpenCVFrameConverter.ToIplImage();
    //摄像头抓取器
    private OpenCVFrameGrabber grabber ;
    //录制器
    private FrameRecorder recorder;
    //摄像头index
    private int webcam_index = 0;
    //预览控件
    ImageView img;
    //保存路径
    String fileName;

    long startRecordTimes=0;

    RecordTimeCallback timeCallback;

    public VideoRecorder() {

    }

    @Override
    public void run() {
        super.run();
        try {
            showVideo = true;
            initVideoParameter = true;
            while (showVideo) {
                if (initVideoParameter){
                    grabber = null;
                    Thread.sleep(1000);
                    grabber = new OpenCVFrameGrabber(webcam_index);
                    grabber.setImageWidth(imageWidth);
                    grabber.setImageHeight(imageHeight);
                    grabber.setFrameRate(60);
                    System.out.println("initVideoParameter"+imageHeight);
                    System.out.println("initVideoParameter"+imageWidth);
                    grabber.start();
                    initVideoParameter = false;
                }


                //获取摄像头帧
                Frame frame = grabber.grab();
                opencv_core.IplImage iplImage = toIplImage.convert(frame);



                //是否录制
                if (recordingVideo){
                    if (startRecordVideo){
                        recorder = initRecorder(Constant.ROOT_FILE_DIR+"/video/"+fileName+"_"+System.currentTimeMillis()+".flv",imageWidth,imageHeight);
                        recorder.start();
                        startRecordVideo = false;
                        startRecordTimes = System.currentTimeMillis();

                    }

                    System.out.println("frame width "+frame.imageWidth);
                    System.out.println("frame height "+frame.imageHeight);

                    recorder.record(frame);
                }else {
                    startRecordTimes = 0;
                    if (recorder!=null){
                        recorder.close();
                        recorder = null;
                    }

                }

                //预览摄像头
                if (img!=null) {
                    BufferedImage image = IplImageToBufferedImage(iplImage);
                    img.setImage(SwingFXUtils.toFXImage(image, new WritableImage(image.getWidth(), image.getHeight())));
                }
                if (startRecordTimes!=0){
                    long sub = System.currentTimeMillis()-startRecordTimes;
                    if (timeCallback!=null)
                        timeCallback.recordTime(sub);
                }
            }
            if (grabber!=null){
                grabber.stop();
                grabber.release();
                grabber = null;

                System.gc();
            }
//                    Thread.sleep(100);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //修改分辨率
    public void setResolution(int width ,int height){
        this.imageWidth = width;
        this.imageHeight = height;
        initVideoParameter = true;
    }

    //更换摄像头
    public void setWebCamIndex(int index){
        this.webcam_index = index;
        initVideoParameter = true;
    }

    //开始录像
    public void startRecord(String fileName){
        this.fileName = fileName;
        startRecordVideo = true;
        recordingVideo = true;

    }

    //停止录像
    public void stopRecord(){

        recordingVideo = false;
    }

    //设置预览控件
    public void setPreviewImageView(ImageView img) {
        this.img = img;
    }

    //录制时间监听
    public void setTimeCallback(RecordTimeCallback timeCallback) {
        this.timeCallback = timeCallback;
    }


    //初始化recorder
    private FrameRecorder initRecorder(String fileName, int captureWidth, int captureHeight){
        File parentFile =new File(fileName).getParentFile();
        if (!parentFile.exists()){
            parentFile.mkdirs();
        }
        recorder= new FFmpegFrameRecorder(fileName,captureWidth,captureHeight,0);
        /**
         * 该参数用于降低延迟 参考FFMPEG官方文档：https://trac.ffmpeg.org/wiki/StreamingGuide
         * 官方原文参考：ffmpeg -f dshow -i video="Virtual-Camera" -vcodec libx264
         * -tune zerolatency -b 900k -f mpegts udp://10.1.0.102:1234
         */

        recorder.setVideoOption("tune", "zerolatency");
        /**
         * 权衡quality(视频质量)和encode speed(编码速度) values(值)：
         * ultrafast(终极快),superfast(超级快), veryfast(非常快), faster(很快), fast(快),
         * medium(中等), slow(慢), slower(很慢), veryslow(非常慢)
         * ultrafast(终极快)提供最少的压缩（低编码器CPU）和最大的视频流大小；而veryslow(非常慢)提供最佳的压缩（高编码器CPU）的同时降低视频流的大小
         * 参考：https://trac.ffmpeg.org/wiki/Encode/H.264 官方原文参考：-preset ultrafast
         * as the name implies provides for the fastest possible encoding. If
         * some tradeoff between quality and encode speed, go for the speed.
         * This might be needed if you are going to be transcoding multiple
         * streams on one machine.
         */

        recorder.setFrameRate(grabber.getFrameRate());
        recorder.setVideoOption("preset", "ultrafast");
        recorder.setInterleaved(true);
        return recorder;

    }


    public BufferedImage IplImageToBufferedImage(opencv_core.IplImage src) {
        OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();
        Java2DFrameConverter paintConverter = new Java2DFrameConverter();
        Frame frame = grabberConverter.convert(src);
        return paintConverter.getBufferedImage(frame,1);
    }

    public void stopPreview(){
        showVideo = false;
    }


    public interface RecordTimeCallback{
        void recordTime(long timeMS);
    }

}
