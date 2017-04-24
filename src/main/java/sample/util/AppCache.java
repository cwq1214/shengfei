package sample.util;

/**
 * Created by chenweiqi on 2017/4/13.
 */
public class AppCache {
    private static AppCache appCache = new AppCache();

    boolean isCertificate = true;

    private int osType;

    private AppCache() {

    }

    public static AppCache getInstance() {
        return appCache;
    }


    public boolean isCertificate() {
        return isCertificate;
    }

    public void setCertificate(boolean certificate) {
        isCertificate = certificate;
    }

    public int getOsType(){
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().contains("windows")){
            return 0;
        }else if (osName.toLowerCase().contains("mac")){
            return 1;
        }
        return -1;
    }
}
