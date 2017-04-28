package sample.controller.NewTableView;

import javafx.collections.ObservableList;
import sample.controller.BaseController;
import sample.entity.Record;
import sample.entity.Topic;
import sample.util.DbHelper;

/**
 * Created by Bee on 2017/4/28.
 */
public class NewTopicEditController extends BaseController {
    private Record record;
    private String kindCode;
    private int baseId;

    private ObservableList<Topic> topics;

    @Override
    public void prepareInit() {
        super.prepareInit();
    }


    public void setBaseMsg(String kindCode,int baseId){
        this.kindCode = kindCode;
        this.baseId = baseId;
        topics = DbHelper.getInstance().insertTopicTable(new Topic(kindCode,baseId));
    }
}
