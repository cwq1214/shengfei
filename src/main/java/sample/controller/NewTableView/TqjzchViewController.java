package sample.controller.NewTableView;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import sample.controller.BaseController;

/**
 * Created by Bee on 2017/5/21.
 */
public class TqjzchViewController extends BaseController {

    private TqjzchListener listener;

    public void setListener(TqjzchListener listener) {
        this.listener = listener;
    }

    @FXML
    private RadioButton normalFormat;

    @FXML
    private RadioButton horFormat;

    @FXML
    public void exportExcelClick(){
        listener.exportExcel(normalFormat.isSelected()?0:1);
    }

    @FXML
    public void exportHtmlClick(){
        listener.exportHtml();
    }

    public interface TqjzchListener{
        public void exportExcel(int format);
        public void exportHtml();
    }
}
