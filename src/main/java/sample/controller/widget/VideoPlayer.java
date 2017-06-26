package sample.controller.widget;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import sample.Main;
import sample.controller.BaseController;
import sample.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by chenweiqi on 2017/5/2.
 */
public class VideoPlayer extends VBox implements Initializable {

    @FXML
    Button btn_play;
    @FXML
    Button btn_stop;
    @FXML
    Slider sl_progress;
    @FXML
    Slider sl_volume;
    @FXML
    MediaView mv_mediaView;


    Parent parent;
    VideoPlayer videoPlayer;

    Media media;
    MediaPlayer mediaPlayer;

    boolean bCanPlay;

    boolean isPlaying = false;

    String path;

    boolean init = false;

    public VideoPlayer() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/videoPlayer.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Node getRootNode(){
        return parent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //拖动播放条调整媒体播放进度
        sl_progress.setOnMouseDragged(e->{
            if(bCanPlay){
                System.out.println(""+sl_progress.getValue()*media.getDuration().toMillis());
                mediaPlayer.seek(new Duration(sl_progress.getValue()*media.getDuration().toMillis()));
            }
        });
    }


    public void setMediaPath(String path){
        this.path = FileUtil.mp4Copy2Temp(path);
        System.out.println("media path:"+this.path);
        Platform.runLater(new Runnable() {
                              @Override
                              public void run() {
            if (mediaPlayer!=null){
                if (mediaPlayer.getStatus()== MediaPlayer.Status.PLAYING){
                    mediaPlayer.stop();
                }

                btn_play.setGraphic(new ImageView(new Image(Main.class.getResourceAsStream("/sample/resource/img/播放.png"))));
            }

//                mediaPlayer=null;
//                mv_mediaView.setMediaPlayer(null);
            }
        });



        File file = new File(this.path);
        if (file.exists()){
            bCanPlay = true;
            init = true;
            onPlayClick();
            mediaPlayer.pause();
        }else {
            bCanPlay = false;
            init = false;
            if (mediaPlayer != null){
                mediaPlayer.stop();
                mediaPlayer = null;
                mv_mediaView.setMediaPlayer(null);
            }

            return;
        }
    }

    public void reset(){
        if (mediaPlayer != null){
            mediaPlayer.dispose();
            media = null;
            setMediaPath("");
        }
    }

    @FXML
    private void onPlayClick(){
        if (init){
            init = false;
            mediaPlayer = new MediaPlayer(media = new Media(new File(path).toURI().toString()));
            sl_progress.setMax(1f);
            sl_progress.setMin(0f);
            mediaPlayer.volumeProperty().bind(sl_volume.valueProperty().divide(50));
            sl_volume.setValue(100);
            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                    sl_progress.setValue(newValue.toMillis()/media.getDuration().toMillis());
                }
            });

            mediaPlayer.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    isPlaying = false;

                    btn_play.setGraphic(new ImageView(new Image(Main.class.getResourceAsStream("/sample/resource/img/播放.png"))));

                    mediaPlayer.stop();


                }
            });
            mediaPlayer.setOnPlaying(new Runnable() {
                @Override
                public void run() {
                    isPlaying = true;

                    btn_play.setGraphic(new ImageView(new Image(Main.class.getResourceAsStream("/sample/resource/img/暂停.png"))));

                }
            });
            mediaPlayer.setOnPaused(new Runnable() {
                @Override
                public void run() {
                    btn_play.setGraphic(new ImageView(new Image(Main.class.getResourceAsStream("/sample/resource/img/播放.png"))));
                }
            });
            mv_mediaView.setMediaPlayer(mediaPlayer);

            mediaPlayer.play();
            return;
        }
        if (mediaPlayer==null){
            return;
        }


        if (mediaPlayer.getStatus()== MediaPlayer.Status.PLAYING){
            mediaPlayer.pause();
        }else if (mediaPlayer.getStatus() == MediaPlayer.Status.STOPPED
                ||mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED || mediaPlayer.getStatus() == MediaPlayer.Status.READY){

            if (mediaPlayer.getStatus()== MediaPlayer.Status.STOPPED){
                sl_progress.setValue(0);
            }

            mediaPlayer.play();
        }
    }

    @FXML
    private void onStopClick(){
        System.out.println("stop");

        if (mediaPlayer!=null)
            mediaPlayer.stop();
//        mediaPlayer.seek(Duration.ZERO);
    }
}
