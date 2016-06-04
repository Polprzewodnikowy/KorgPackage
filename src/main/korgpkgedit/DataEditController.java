package korgpkgedit;

import javafx.scene.control.ChoiceBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import korgpkg.Chunk;
import korgpkg.DataChunk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by korgeaux on 31.05.2016.
 */
public class DataEditController implements ChunkEditController {

    private Stage stage;
    private DataChunk dataChunk;
    private byte[] data;

    public ChoiceBox type;

    public void setup(Stage stage, Chunk chunk) {
        this.stage = stage;
        this.dataChunk = (DataChunk) chunk;
        stage.setTitle(dataChunk.toString());
        type.getSelectionModel().select(dataChunk.getId() - 2);
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
        dataChunk.setId(type.getSelectionModel().getSelectedIndex() + 2);
        if (data != null)
            dataChunk.setData(data);
        stage.close();
    }

}
