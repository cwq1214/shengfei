package sample.util;

/**
 * Created by chenweiqi on 2017/4/13.
 */
public class AppCache {
    private static AppCache appCache = new AppCache();

    boolean isCertificate = true;

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
}
