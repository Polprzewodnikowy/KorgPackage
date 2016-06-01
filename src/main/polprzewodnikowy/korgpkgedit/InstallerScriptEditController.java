package polprzewodnikowy.korgpkgedit;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import polprzewodnikowy.korgpkg.Chunk;
import polprzewodnikowy.korgpkg.InstallerScriptChunk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by korgeaux on 31.05.2016.
 */
public class InstallerScriptEditController implements ChunkEditController {

    Stage stage;
    InstallerScriptChunk installerScriptChunk;
    byte[] tmpData;

    public TextField name;
    public TextField order;

    public void setup(Stage stage, Chunk chunk) {
        this.stage = stage;
        this.installerScriptChunk = (InstallerScriptChunk) chunk;
        name.setText(installerScriptChunk.getName());
        order.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        order.setText(Integer.toString(installerScriptChunk.getOrder()));
    }

    public void importDataAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All files", "*.*")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                tmpData = new byte[(int) file.length()];
                fileInputStream.read(tmpData, 0, (int) file.length());
                fileInputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveChunkAction() {
        installerScriptChunk.setName(name.getText());
        installerScriptChunk.setOrder(Short.parseShort(order.getText()));
        if (tmpData != null) {
            installerScriptChunk.setData(tmpData);
        }
        stage.close();
    }

}
