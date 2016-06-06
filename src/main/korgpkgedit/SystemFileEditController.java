package korgpkgedit;

import javafx.scene.control.ChoiceBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import korgpkg.Chunk;
import korgpkg.SystemFileChunk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by korgeaux on 31.05.2016.
 */
public class SystemFileEditController implements ChunkEditController {

    private Stage stage;
    private SystemFileChunk systemFileChunk;
    private byte[] data;

    public ChoiceBox type;

    public void setup(Stage stage, Chunk chunk) {
        this.stage = stage;
        this.systemFileChunk = (SystemFileChunk) chunk;
        stage.setTitle(systemFileChunk.toString());
        type.getSelectionModel().select(systemFileChunk.getId() - 2);
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
        systemFileChunk.setId(type.getSelectionModel().getSelectedIndex() + 2);
        if (data != null)
            systemFileChunk.setData(data);
        stage.close();
    }

}
