package sample.util;

import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenweiqi on 2017/4/13.
 */
public class DialogUtil {

    public static final int FILE_CHOOSE_TYPE_IMAGE = 0;
    public static final int FILE_CHOOSE_TYPE_VIDEO = 1;
    public static final int FILE_CHOOSE_TYPE_AUDIO = 2;
    public static final int FILE_CHOOSE_TYPE_EXCEL = 3;
    public static final int FILE_CHOOSE_TYPE_XML = 4;
    public static final int FILE_CHOOSE_TYPE_EAF = 5;
    public static final int FILE_CHOOSE_TYPE_EXB = 6;
    public static final int FILE_CHOOSE_TYPE_AC = 7;
    public static final int FILE_CHOOSE_TYPE_WAV = 8;

    public static Alert showDialog(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, null);
        alert.setHeaderText(content);
        alert.show();
        return alert;
    }

    public static File fileChoiceDialog(String title, int fileType) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);

        if (fileType == FILE_CHOOSE_TYPE_IMAGE) {
            fileChooser.getExtensionFilters().addAll(
//                    new FileChooser.ExtensionFilter("All Images", "*.*"),
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                    new FileChooser.ExtensionFilter("PNG", "*.png"),
                    new FileChooser.ExtensionFilter("GIF", "*.gif")
            );
        } else if (fileType == FILE_CHOOSE_TYPE_VIDEO) {
            fileChooser.getExtensionFilters().addAll(
//                    new FileChooser.ExtensionFilter("All Video", "*.*"),
                    new FileChooser.ExtensionFilter("AVI", "*.avi"),
                    new FileChooser.ExtensionFilter("WMV", "*.wmv")
            );
        }else if (fileType == FILE_CHOOSE_TYPE_EXCEL){
            fileChooser.getExtensionFilters().addAll(
//                    new FileChooser.ExtensionFilter("All Video", "*.*"),
                    new FileChooser.ExtensionFilter("2003Excel", "*.xls"),
                    new FileChooser.ExtensionFilter("2007Excel", "*.xlsx")
            );
        }else if (fileType == FILE_CHOOSE_TYPE_XML){
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("xml", "*.xml")
            );
        }else if (fileType == FILE_CHOOSE_TYPE_EXB){
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Exma", "*.exb")
            );
        }else if (fileType == FILE_CHOOSE_TYPE_EAF){
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Elan", "*.eaf")
            );
        }else if (fileType == FILE_CHOOSE_TYPE_AC){
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Audacity", "*.txt")
            );
        }else if (fileType == FILE_CHOOSE_TYPE_WAV){
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("wav", "*.wav")
            );
        }
        Stage stage = new Stage();
        return fileChooser.showOpenDialog(stage);
    }

    public static File dirChooses(Stage stage){
        DirectoryChooser dc = new DirectoryChooser();
        File choiceDir = dc.showDialog(stage);
        if (!choiceDir.exists()){
            choiceDir.mkdirs();
        }
        return choiceDir;
    }

    public static File exportTqjzchDialog(boolean isExcel){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("请选择文件");
        if (isExcel){
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("2003Excel文件", "*.xls"));
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("2007Excel文件", "*.xlsx"));
        }else{
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("网页", "*.html"));
        }
        chooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        Stage stage = new Stage();
        File saveFile = chooser.showSaveDialog(stage);
        if (saveFile == null) {
            throw new RuntimeException("save File can not be null");
        }
        if (!saveFile.exists()) {
            saveFile.getParentFile().mkdirs();
        }
        return saveFile;
    }

    public static File exportFileDialog(){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("请选择文件");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("2003Excel文件", "*.xls"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("2007Excel文件", "*.xlsx"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("网页", "*.html"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("带音视频网页", "*.htmla"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("手机版网页", "*.htm"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("手机版带音视频网页", "*.htma"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("exmaralda", "*.exb"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ELAN", "*.eaf"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("声飞xml", "*.xml"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audacity label", "*.txt"));
        chooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        Stage stage = new Stage();
        File saveFile = chooser.showSaveDialog(stage);
        if (saveFile == null) {
            throw new RuntimeException("save File can not be null");
        }
        if (!saveFile.exists()) {
            saveFile.getParentFile().mkdirs();
        }
        return saveFile;
    }
    public static File exportFileDialog(FileChooser.ExtensionFilter[] filters){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("请选择文件");
        chooser.getExtensionFilters().addAll(filters);
        chooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        Stage stage = new Stage();
        File saveFile = chooser.showSaveDialog(stage);
        if (saveFile == null) {
            throw new RuntimeException("save File can not be null");
        }
        if (!saveFile.exists()) {
            saveFile.getParentFile().mkdirs();
        }
        return saveFile;
    }

    public static List<File> chooseAudio(boolean multiSel){

        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("audio", "*.wav"));
        if (multiSel){
            return chooser.showOpenMultipleDialog(new Stage());
        }else {
            List list = new ArrayList<>();
            list.add(chooser.showOpenDialog(new Stage()));
            return list;
        }
    }
    public static List<File> chooseVideo(boolean multiSel){

        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("video", "*.mp4"));
        if (multiSel){
            return chooser.showOpenMultipleDialog(new Stage());
        }else {
            List list = new ArrayList<>();
            File file = chooser.showOpenDialog(new Stage());
            if (file!=null)
                list.add(file);
            return list;
        }
    }

    public static File selDir(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        return directoryChooser.showDialog(new Stage());
    }

    public static File saveFileDialog(String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("excel2007", "*.xlsx"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("excel2003", "*.xls"));
        chooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        Stage stage = new Stage();
        File saveFile = chooser.showSaveDialog(stage);
        if (saveFile == null) {
            throw new RuntimeException("save File can not be null");
        }
        if (!saveFile.exists()) {
            saveFile.getParentFile().mkdirs();
        }
        return saveFile;
    }
}
