package korgpkgedit;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import korgpkg.Chunk;
import korgpkg.InstallerScriptChunk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by korgeaux on 31.05.2016.
 */
public class InstallerScriptEditController implements ChunkEditController {

    private Stage stage;
    private InstallerScriptChunk installerScriptChunk;
    byte[] data;

    public TextField name;
    public TextField condition;

    public void setup(Stage stage, Chunk chunk) {
        this.stage = stage;
        this.installerScriptChunk = (InstallerScriptChunk) chunk;
        stage.setTitle(installerScriptChunk.toString());
        name.setText(installerScriptChunk.getName());
        condition.setText(Integer.toString(installerScriptChunk.getCondition()));
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
                data = new byte[(int) file.length()];
                fileInputStream.read(data, 0, (int) file.length());
                fileInputStream.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void saveChunkAction() {
        installerScriptChunk.setName(name.getText());
        installerScriptChunk.setCondition(Short.parseShort(condition.getText()));
        if (data != null)
            installerScriptChunk.setData(data);
        stage.close();
    }

}
