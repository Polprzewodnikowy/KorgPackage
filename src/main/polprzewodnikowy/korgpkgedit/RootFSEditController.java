package polprzewodnikowy.korgpkgedit;

import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import polprzewodnikowy.korgpkg.Chunk;
import polprzewodnikowy.korgpkg.RootFSChunk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by korgeaux on 31.05.2016.
 */
public class RootFSEditController implements ChunkEditController {

    Stage stage;
    RootFSChunk rootFSChunk;
    byte[] tmpData;

    public TextField name;

    public void setup(Stage stage, Chunk chunk) {
        this.stage = stage;
        this.rootFSChunk = (RootFSChunk) chunk;
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
                tmpData = new byte[(int) file.length()];
                fileInputStream.read(tmpData, 0, (int) file.length());
                fileInputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveChunkAction() {
        rootFSChunk.setName(name.getText());
        if (tmpData != null) {
            rootFSChunk.setData(tmpData);
        }
        stage.close();
    }

}
