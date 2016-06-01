package polprzewodnikowy.korgpkgedit;

import polprzewodnikowy.korgpkg.Chunk;
import polprzewodnikowy.korgpkg.FileChunk;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import tornadofx.control.DateTimePicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by korgeaux on 31.05.2016.
 */
public class FileEditController implements ChunkEditController {

    Stage stage;
    FileChunk fileChunk;
    byte[] tmpData;

    public TextField name;
    public DateTimePicker date;
    public ChoiceBox compression;
    public CheckBox attrA;
    public CheckBox attrR;
    public CheckBox attrH;
    public CheckBox attrS;
    public TextField group;
    public TextField owner;
    public TextField order;

    public void setup(Stage stage, Chunk chunk) {
        this.stage = stage;
        this.fileChunk = (FileChunk) chunk;
        name.setText(fileChunk.getName());
        date.setDateTimeValue(LocalDateTime.ofInstant(fileChunk.getDateTime().toInstant(), ZoneId.systemDefault()));
        compression.getItems().add("RAW");
        compression.getItems().add("Zlib");
        compression.getSelectionModel().select(fileChunk.getCompressionType());
        int attr = fileChunk.getAttributes();
        if ((attr & FileChunk.ATTR_VFAT_ARCHIVE) != 0)
            attrA.setSelected(true);
        if ((attr & FileChunk.ATTR_VFAT_READONLY) != 0)
            attrR.setSelected(true);
        if ((attr & FileChunk.ATTR_VFAT_HIDDEN) != 0)
            attrH.setSelected(true);
        if ((attr & FileChunk.ATTR_VFAT_SYSTEM) != 0)
            attrS.setSelected(true);
        group.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        owner.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        order.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        group.setText(Integer.toString(fileChunk.getGroup()));
        owner.setText(Integer.toString(fileChunk.getOwner()));
        order.setText(Integer.toString(fileChunk.getOrder()));
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
        fileChunk.setName(name.getText());
        fileChunk.setDateTime(Date.from(date.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant()));
        fileChunk.setCompressionType((byte) compression.getSelectionModel().getSelectedIndex());
        int attr = 0;
        if (attrA.isSelected())
            attr |= FileChunk.ATTR_VFAT_ARCHIVE;
        if (attrR.isSelected())
            attr |= FileChunk.ATTR_VFAT_READONLY;
        if (attrH.isSelected())
            attr |= FileChunk.ATTR_VFAT_HIDDEN;
        if (attrS.isSelected())
            attr |= FileChunk.ATTR_VFAT_SYSTEM;
        fileChunk.setAttributes(attr);
        fileChunk.setGroup(Short.parseShort(group.getText()));
        fileChunk.setOwner(Short.parseShort(owner.getText()));
        fileChunk.setOrder(Short.parseShort(order.getText()));
        if (tmpData != null) {
            fileChunk.setData(tmpData);
        }
        stage.close();
    }

}
