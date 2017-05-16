package sample.controller.ImportExcel;

/**
 * Created by Bee on 2017/5/13.
 */
public class ImpTitleBean {
    private String titleName;
    private String methodName;

    public ImpTitleBean(String titleName, String methodName) {
        this.titleName = titleName;
        this.methodName = methodName;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
