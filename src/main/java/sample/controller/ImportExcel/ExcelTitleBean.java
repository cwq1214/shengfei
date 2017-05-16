package sample.controller.ImportExcel;

/**
 * Created by Bee on 2017/5/13.
 */
public class ExcelTitleBean {
    private String titleName;
    private int index;

    public ExcelTitleBean(String titleName, int index) {
        this.titleName = titleName;
        this.index = index;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
