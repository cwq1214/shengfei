package sample.controller;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_videoio;
import org.bytedeco.javacv.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    boolean showVideo;
    boolean changeResolution;

    OpenCVFrameGrabber grabber;
    OpenCVFrameConverter.ToIplImage toIplImage = new OpenCVFrameConverter.ToIplImage();

    ExecutorService webCamThread;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        defaultSel();
        getCamera();
    }

    private void defaultSel(){
//        cb_resolution.getSelectionModel().select(0);


        cb_resolution.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                System.out.println("change");
                changeResolution = true;
            }
        });

        img.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount()==2){
                    try {
                        grabber.close();
                        showVideo =false;
                        grabber=null;
                    } catch (FrameGrabber.Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getCamera(){
        webCamThread = Executors.newSingleThreadExecutor();
        webCamThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    showVideo = true;
                    changeResolution = true;

                    while (showVideo) {
                        if (changeResolution){
//                            if (grabber!=null){
//                                grabber.stop();
//                                grabber.release();
//                                grabber.close();
//                                Thread.sleep(1000);
//                            }
                            grabber = new OpenCVFrameGrabber(0);

                            FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(new File("temp"));
                            //String selItem = ((String)  cb_resolution.getValue());
//                            grabber.setImageWidth(Integer.valueOf(selItem.split("\\*")[0]));
//                            grabber.setImageHeight(Integer.valueOf(selItem.split("\\*")[1]));
//                            grabber.setImageWidth(800);
//                            grabber.setImageHeight(600);
//                            grabber.setBitsPerPixel(4);

                            grabber.start();

                            changeResolution = false;
                        }

                        if (grabber!=null){
                            opencv_core.IplImage iplImage = toIplImage.convert(grabber.grab());
                            BufferedImage image = IplImageToBufferedImage(iplImage);
                            img.setImage(SwingFXUtils.toFXImage(image,new WritableImage(image.getWidth(),image.getHeight())));

                        }



//                        System.out.println(image.getWidth());
//                        System.out.println(image.getHeight());
//                        System.out.println(grabber.getImageWidth());
//                        System.out.println(grabber.getImageHeight());



//                        showVideo = false;
//                        grabber.stop();

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    public static BufferedImage IplImageToBufferedImage(opencv_core.IplImage src) {
        OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();
        Java2DFrameConverter paintConverter = new Java2DFrameConverter();
        Frame frame = grabberConverter.convert(src);
        return paintConverter.getBufferedImage(frame,1);
    }
    @Override
    public void onStop() {
        super.onStop();
//            grabber.release();
//            grabber.stop();
        showVideo = false;
        webCamThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (grabber!=null){
                        grabber.close();
                    }
                } catch (FrameGrabber.Exception e) {
                    e.printStackTrace();
                }
            }
        });





    }

}
