package sample.util;

import javafx.scene.Node;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * Created by chenweiqi on 2017/3/29.
 */
public class WidgetUtil {


    public static Tab createNewTab(String title, Node content){
        Tab tab = new Tab();
        tab.setText(title);
        tab.setContent(content);
        return tab;
    }

    public static void addTabToTabPane(TabPane pane,Tab tab){
        pane.getTabs().add(0,tab);

    }

    public static void selectTab(Tab tab){
        SingleSelectionModel<Tab> selectionModel = tab.getTabPane().getSelectionModel();
        selectionModel.select(tab);
    }
}
