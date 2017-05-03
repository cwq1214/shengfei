package sample.controller.widget;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import sample.Main;

import java.io.File;
import java.io.IOException;

/**
 * Created by chenweiqi on 2017/5/3.
 */
public class AudioPlayer extends VBox {

    @FXML
    Slider sl_progress;
    @FXML
    Slider sl_volume;
    @FXML
    Button btn_play;
    @FXML
    Button btn_stop;



    Media media;
    MediaPlayer mediaPlayer;
    String path;

    boolean init;


    public AudioPlayer() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/audioPlayer"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAudioPath(String path){
        this.path = path;
        File file = new File(path);
        if (file.exists()){
            init = true;
        }else {
        }
    }

    @FXML
    private void onPlayClick(){

        if (init){

            mediaPlayer = new MediaPlayer(media = new Media(new File(path).toURI().toString()));
            sl_progress.setMax(1f);
            sl_progress.setMin(0f);
            mediaPlayer.volumeProperty().bind(sl_volume.valueProperty().divide(50));
            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                    sl_progress.setValue(newValue.toMillis()/media.getDuration().toMillis());
                }
            });

            mediaPlayer.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {

                    btn_play.setGraphic(new ImageView(new Image(Main.class.getResourceAsStream("/sample/resource/img/播放.png"))));

                    mediaPlayer.stop();


                }
            });
            mediaPlayer.setOnPlaying(new Runnable() {
                @Override
                public void run() {

                    btn_play.setGraphic(new ImageView(new Image(Main.class.getResourceAsStream("/sample/resource/img/暂停.png"))));

                }
            });
            mediaPlayer.setOnPaused(new Runnable() {
                @Override
                public void run() {
                    btn_play.setGraphic(new ImageView(new Image(Main.class.getResourceAsStream("/sample/resource/img/播放.png"))));

                }
            });
            mediaPlayer.play();

            init =false;
        }


        if (mediaPlayer==null){
            return;
        }

        if (mediaPlayer.getStatus()== MediaPlayer.Status.STOPPED
                ||mediaPlayer.getStatus()== MediaPlayer.Status.PAUSED){
            if (mediaPlayer.getStatus()== MediaPlayer.Status.STOPPED){
                sl_progress.setValue(0);

            }
            mediaPlayer.play();
        }else if (mediaPlayer.getStatus()==MediaPlayer.Status.PLAYING){
            mediaPlayer.pause();
        }
    }
    @FXML
    private void onStopClick(){
        if (mediaPlayer!=null){
            mediaPlayer.stop();
        }
    }
}
