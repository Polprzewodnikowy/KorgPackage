package korgpkgedit;

import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import korgpkg.Chunk;
import korgpkg.RootFSChunk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by korgeaux on 31.05.2016.
 */
public class RootFSEditController implements ChunkEditController {

    private Stage stage;
    private RootFSChunk rootFSChunk;
    private byte[] data;

    public TextField name;

    public void setup(Stage stage, Chunk chunk) {
        this.stage = stage;
        this.rootFSChunk = (RootFSChunk) chunk;
        stage.setTitle(rootFSChunk.toString());
        name.setText(rootFSChunk.getName());
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
        rootFSChunk.setName(name.getText());
        if (data != null)
            rootFSChunk.setData(data);
        stage.close();
    }

}
