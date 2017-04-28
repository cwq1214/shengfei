package sample.diycontrol;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import sample.Main;


import java.io.IOException;

/**
 * Created by Bee on 2017/4/28.
 */
public class TopicSpeakControl extends VBox {

    @FXML
    private TextField ipaTF;

    @FXML
    private TextField mwfyTF;

    @FXML
    private TextField spellTF;

    @FXML
    private TextField wordTranTF;

    @FXML
    private TextField freeTranTF;

    @FXML
    private TextField noteTF;

    @FXML
    private TextField englishTF;

    public TopicSpeakControl() {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/control/topicSpeakControl.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
