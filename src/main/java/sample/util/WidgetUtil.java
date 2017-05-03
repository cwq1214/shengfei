package sample.util;

import javafx.scene.Node;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import sample.controller.BaseController;

import java.util.List;

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

    public static void addTabToTabPane(TabPane pane, Tab tab, boolean single,BaseController vc){
        tab.setUserData(vc);
        addTabToTabPane(pane,tab,single);
    }

    public static void addTabToTabPane(TabPane pane, Tab tab){
        addTabToTabPane(pane, tab, false);
    }

    public static void addTabToTabPane(TabPane pane, Tab tab, boolean single) {
        if (single) {
            List<Tab> tabs = pane.getTabs();
            for (int i = tabs.size() - 1; i >= 0; i--) {
                if (tabs.get(i).getText().equals(tab.getText())) {
                    tabs.remove(i);
                    if (tabs.get(i).getOnClosed()!=null)
                        tabs.get(i).getOnClosed().handle(null);
                }
            }
        }
        pane.getTabs().add(0,tab);
    }

    public static void selectTab(Tab tab){
        SingleSelectionModel<Tab> selectionModel = tab.getTabPane().getSelectionModel();
        selectionModel.select(tab);
    }
}
