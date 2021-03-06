package sample.util;

/**
 * Created by chenweiqi on 2017/4/11.
 */
public class Constant {
    public static String ROOT_FILE_DIR = "shengfeiTemp";
    public static String PRAAT = ROOT_FILE_DIR + "/praat";
    public static String DEMO_PIC_DIR = ROOT_FILE_DIR + "/DemoPic";
    public static String TEMP_DIR = ROOT_FILE_DIR + "/temp";
    public static String DEMO_Video_DIR = ROOT_FILE_DIR + "/DemoVideo";
    public static String EXPORT_YD = ROOT_FILE_DIR + "/ExportYD";
    public static String PASSWORD = "ok";

    public static String VIDEO_SUFFIX = ".mp4";
    public static String AUDIO_SUFFIX = ".wav";


    public static String getAudioPath(String baseId,String uuid){
        if (TextUtil.isEmpty(baseId)||TextUtil.isEmpty(uuid))
            return null;

        return Constant.ROOT_FILE_DIR+"/audio/"+baseId+"/"+uuid+AUDIO_SUFFIX;
    }


    public static String getVideoPath(String baseId,String uuid){
        if (TextUtil.isEmpty(baseId)||TextUtil.isEmpty(uuid))
            return null;

        return Constant.ROOT_FILE_DIR+"/video/"+baseId+"/"+uuid+VIDEO_SUFFIX;
    }
}
