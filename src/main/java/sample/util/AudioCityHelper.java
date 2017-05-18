package sample.util;

import javafx.scene.control.TableView;
import sample.entity.Table;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenweiqi on 2017/5/18.
 */
public class AudioCityHelper {
    static AudioCityHelper audioCityHelper;

    private AudioCityHelper() {
    }

    public static synchronized AudioCityHelper getInstance(){
        synchronized (AudioCityHelper.class) {
            if (audioCityHelper == null) {
                audioCityHelper = new AudioCityHelper();
            }
        }
        return audioCityHelper;
    }

    public void writeToAc(String filePath, TableView tableView, Table t){

    }

    public List<String> readAttrTitle(String path){
        File file = new File(path);
        if (!file.exists()){
            return null;
        }
        List<String> strings = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String tempLine;
            while ((tempLine= reader.readLine())!=null){
                System.out.println("line" + tempLine);
                if (tempLine.replaceAll(" ","").startsWith("^\\s{0,1}0.0")){
                    System.out.println("start "+tempLine);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return strings;
    }
}
