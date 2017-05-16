package sample.controller.ImportExcel;

/**
 * Created by Bee on 2017/5/13.
 */
public class ImpBindBean {
    private ExcelTitleBean excelTitleBean;
    private ImpTitleBean impTitleBean;

    public ImpBindBean(ExcelTitleBean excelTitleBean, ImpTitleBean impTitleBean) {
        this.excelTitleBean = excelTitleBean;
        this.impTitleBean = impTitleBean;
    }

    public ExcelTitleBean getExcelTitleBean() {
        return excelTitleBean;
    }

    public void setExcelTitleBean(ExcelTitleBean excelTitleBean) {
        this.excelTitleBean = excelTitleBean;
    }

    public ImpTitleBean getImpTitleBean() {
        return impTitleBean;
    }

    public void setImpTitleBean(ImpTitleBean impTitleBean) {
        this.impTitleBean = impTitleBean;
    }
}
