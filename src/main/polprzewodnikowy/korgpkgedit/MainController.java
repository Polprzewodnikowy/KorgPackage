package polprzewodnikowy.korgpkgedit;

import polprzewodnikowy.korgpkg.Chunk;
import polprzewodnikowy.korgpkg.FileChunk;
import polprzewodnikowy.korgpkg.PackageReader;
import polprzewodnikowy.korgpkg.PackageWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by korgeaux on 31.05.2016.
 */
public class MainController {

    private Stage stage;
    public ListView chunksListView;
    public Label statusLabel;

    @FXML
    public void initialize() {
        ContextMenu chunksContextMenu = new ContextMenu();
        MenuItem menuItem;

        menuItem = new MenuItem("Edit");
        menuItem.setOnAction((ex) -> editChunkAction());
        chunksContextMenu.getItems().add(menuItem);

        menuItem = new MenuItem("Export");
        menuItem.setOnAction((ex) -> exportChunkAction());
        chunksContextMenu.getItems().add(menuItem);

        menuItem = new MenuItem("Remove");
        menuItem.setOnAction((ex) -> removeChunkAction());
        chunksContextMenu.getItems().add(menuItem);

        chunksListView.setContextMenu(chunksContextMenu);
        chunksListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void setup(Stage stage) {
        this.stage = stage;
    }

    public void refreshList() {
        ObservableList<Chunk> chunks = chunksListView.getItems();
        chunksListView.setItems(null);
        chunksListView.setItems(chunks);
    }

    public void openPkgAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open pkg File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PKG file", "*.pkg"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            List<Chunk> chunks = new PackageReader(file).load();
            chunksListView.setItems(FXCollections.observableList(chunks));
            statusLabel.setText("Finished reading package");
        }
    }

    public void savePkgAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save pkg File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PKG file", "*.pkg"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            PackageWriter packageWriter = new PackageWriter(file);
            List<Chunk> chunks = new ArrayList<>(chunksListView.getItems());
            packageWriter.save(chunks);
            statusLabel.setText("Finished saving package");
        }
    }

    public void closeAppAction() {
        stage.close();
    }

    public void editChunkAction() {
        Chunk chunk = (Chunk) chunksListView.getSelectionModel().getSelectedItem();
        if (chunk != null) {
            try {
                Stage editWindow = new Stage();
                FXMLLoader loader;
                Parent root;
                String title;
                switch (chunk.getId()) {
                    case Chunk.FILE:
                        loader = new FXMLLoader(getClass().getClassLoader().getResource("FileEditWindow.fxml"));
                        root = loader.load();
                        FileEditController controller = loader.getController();
                        controller.setup(editWindow, (FileChunk) chunk);
                        title = "File edit";
                        break;
                    default:
                        return;
                }
                editWindow.setScene(new Scene(root, 510, 250));
                editWindow.setTitle(title);
                editWindow.setResizable(false);
                editWindow.initModality(Modality.APPLICATION_MODAL);
                editWindow.showAndWait();
                refreshList();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void exportChunkAction() {
        Chunk chunk = (Chunk) chunksListView.getSelectionModel().getSelectedItem();
        if (chunk != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose directory");
            File dir = directoryChooser.showDialog(stage);
            if (dir != null) {
                try {
                    ObservableList<Chunk> chunks = chunksListView.getSelectionModel().getSelectedItems();
                    for (Chunk c : chunks) {
                        c.export(dir.getPath());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void removeChunkAction() {
        Chunk chunk = (Chunk) chunksListView.getSelectionModel().getSelectedItem();
        if (chunk != null) {
            chunksListView.getItems().removeAll(chunksListView.getSelectionModel().getSelectedItems());
        }
    }

}
