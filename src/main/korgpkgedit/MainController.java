package korgpkgedit;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import korgpkg.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by korgeaux on 31.05.2016.
 */
public class MainController {

    private Stage stage;

    public ListView chunksListView;
    public Label statusLabel;

    public void setup(Stage stage) {
        this.stage = stage;
        chunksListView.setItems(FXCollections.observableArrayList(new ArrayList<Chunk>()));
    }

    public void refreshList() {
        ObservableList<Chunk> chunks = chunksListView.getItems();
        List<Integer> selected = new ArrayList<>();
        chunksListView.getSelectionModel().getSelectedIndices().forEach((i) -> selected.add((Integer) i));
        chunksListView.setItems(FXCollections.observableList(new ArrayList<Chunk>()));
        chunksListView.setItems(chunks);
        for (Integer i : selected) {
            chunksListView.getSelectionModel().select(i.intValue());
        }
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
            chunksListView.setItems(FXCollections.observableArrayList(chunks));
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
            String view;
            switch (chunk.getId()) {
                case Chunk.HEADER:
                    view = "HeaderEditWindow.fxml";
                    break;
                case Chunk.UPDATE_KERNEL:
                case Chunk.UPDATE_RAMDISK:
                case Chunk.UPDATE_INSTALLER_APP:
                case Chunk.UPDATE_INSTALLER_APP_CONFIG:
                case Chunk.SERVICE_KERNEL:
                case Chunk.SERVICE_RAMDISK:
                case Chunk.SERVICE_APP:
                case Chunk.SERVICE_APP_CONFIG:
                case Chunk.UPDATE_LAUNCHER_APP:
                case Chunk.UPDATE_LAUNCHER_APP_CONFIG:
                case Chunk.MLO:
                case Chunk.UBOOT:
                case Chunk.USER_KERNEL:
                    view = "SystemFileEditWindow.fxml";
                    break;
                case Chunk.INSTALLER_SCRIPT:
                    view = "InstallerScriptEditWindow.fxml";
                    break;
                case Chunk.DIRECTORY:
                    view = "DirectoryEditWindow.fxml";
                    break;
                case Chunk.FILE:
                    view = "FileEditWindow.fxml";
                    break;
                case Chunk.ROOT_FS:
                    view = "RootFSEditWindow.fxml";
                    break;
                default:
                    return;
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(view));
                Parent root = loader.load();
                ChunkEditController controller = loader.getController();
                Stage editWindow = new Stage();
                controller.setup(editWindow, chunk);
                editWindow.setScene(new Scene(root));
                editWindow.setResizable(false);
                editWindow.initModality(Modality.APPLICATION_MODAL);
                editWindow.showAndWait();
                refreshList();
            } catch (IOException e) {
                System.err.println(e.getMessage());
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
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    public void removeChunkAction() {
        Chunk chunk = (Chunk) chunksListView.getSelectionModel().getSelectedItem();
        if (chunk != null) {
            ObservableList<Chunk> chunks = chunksListView.getSelectionModel().getSelectedItems();
            chunksListView.getItems().removeAll(chunks);
        }
    }

    public void moveUpChunkAction() {
        if (chunksListView.getSelectionModel().getSelectedItem() != null) {
            int pos = chunksListView.getSelectionModel().getSelectedIndex();
            if (pos >= 1) {
                Collections.swap(chunksListView.getItems(), pos - 1, pos);
                chunksListView.getSelectionModel().clearAndSelect(pos - 1);
            }
        }
    }

    public void moveDownChunkAction() {
        if (chunksListView.getSelectionModel().getSelectedItem() != null) {
            int pos = chunksListView.getSelectionModel().getSelectedIndex();
            if (pos < chunksListView.getItems().size() - 1) {
                Collections.swap(chunksListView.getItems(), pos, pos + 1);
                chunksListView.getSelectionModel().clearAndSelect(pos + 1);
            }
        }
    }

    public void addChunk(ActionEvent e) {
        Chunk chunk;
        MenuItem menuItem = (MenuItem) e.getSource();
        if (menuItem.getText().equals("Header")) {
            chunk = new HeaderChunk();
        } else if (menuItem.getText().equals("System file")) {
            chunk = new SystemFileChunk(Chunk.UPDATE_KERNEL);
        } else if (menuItem.getText().equals("Installer script")) {
            chunk = new InstallerScriptChunk();
        } else if (menuItem.getText().equals("Directory")) {
            chunk = new DirectoryChunk();
        } else if (menuItem.getText().equals("File")) {
            chunk = new FileChunk();
        } else if (menuItem.getText().equals("File system")) {
            chunk = new RootFSChunk();
        } else {
            return;
        }
        Chunk c = (Chunk) chunksListView.getSelectionModel().getSelectedItem();
        if (c != null) {
            int index = chunksListView.getSelectionModel().getSelectedIndex();
            chunksListView.getItems().add(index + 1, chunk);
        } else {
            chunksListView.getItems().add(chunk);
        }
        chunksListView.getSelectionModel().clearSelection();
        chunksListView.getSelectionModel().select(chunk);
    }

    public void showAboutAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("AboutWindow.fxml"));
            Parent root = loader.load();
            Stage editWindow = new Stage();
            editWindow.setScene(new Scene(root));
            editWindow.setResizable(false);
            editWindow.initModality(Modality.APPLICATION_MODAL);
            editWindow.showAndWait();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

}
