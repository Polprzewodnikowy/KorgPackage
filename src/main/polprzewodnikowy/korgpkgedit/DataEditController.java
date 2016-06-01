package polprzewodnikowy.korgpkgedit;

import javafx.scene.control.ChoiceBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import polprzewodnikowy.korgpkg.Chunk;
import polprzewodnikowy.korgpkg.DataChunk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by korgeaux on 31.05.2016.
 */
public class DataEditController implements ChunkEditController {

    Stage stage;
    DataChunk dataChunk;
    byte[] tmpData;

    public ChoiceBox type;

    public void setup(Stage stage, Chunk chunk) {
        this.stage = stage;
        this.dataChunk = (DataChunk) chunk;
        type.getItems().add("UPDATE_KERNEL");
        type.getItems().add("UPDATE_RAMDISK");
        type.getItems().add("UPDATE_INSTALLER_APP");
        type.getItems().add("UPDATE_INSTALLER_APP_CONFIG");
        type.getItems().add("SERVICE_KERNEL");
        type.getItems().add("SERVICE_RAMDISK");
        type.getItems().add("SERVICE_APP");
        type.getItems().add("SERVICE_APP_CONFIG");
        type.getItems().add("UPDATE_LAUNCHER_APP");
        type.getItems().add("UPDATE_LAUNCHER_APP_CONFIG");
        type.getItems().add("MLO");
        type.getItems().add("UBOOT");
        type.getItems().add("USER_KERNEL");
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
                tmpData = new byte[(int) file.length()];
                fileInputStream.read(tmpData, 0, (int) file.length());
                fileInputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveChunkAction() {
        dataChunk.setId(type.getSelectionModel().getSelectedIndex() + 2);
        if (tmpData != null) {
            dataChunk.setData(tmpData);
        }
        stage.close();
    }

}
