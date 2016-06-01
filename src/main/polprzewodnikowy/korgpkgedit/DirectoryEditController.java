package polprzewodnikowy.korgpkgedit;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import polprzewodnikowy.korgpkg.Chunk;
import polprzewodnikowy.korgpkg.DirectoryChunk;
import polprzewodnikowy.korgpkg.FileChunk;

/**
 * Created by korgeaux on 31.05.2016.
 */
public class DirectoryEditController implements ChunkEditController {

    Stage stage;
    DirectoryChunk directoryChunk;

    public TextField name;
    public CheckBox attrA;
    public CheckBox attrR;
    public CheckBox attrH;
    public CheckBox attrS;
    public TextField group;
    public TextField owner;
    public TextField order;

    public void setup(Stage stage, Chunk chunk) {
        this.stage = stage;
        this.directoryChunk = (DirectoryChunk) chunk;
        name.setText(directoryChunk.getName());
        int attr = directoryChunk.getAttributes();
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
        group.setText(Integer.toString(directoryChunk.getGroup()));
        owner.setText(Integer.toString(directoryChunk.getOwner()));
        order.setText(Integer.toString(directoryChunk.getOrder()));
    }

    public void saveChunkAction() {
        directoryChunk.setName(name.getText());
        int attr = 0;
        if (attrA.isSelected())
            attr |= FileChunk.ATTR_VFAT_ARCHIVE;
        if (attrR.isSelected())
            attr |= FileChunk.ATTR_VFAT_READONLY;
        if (attrH.isSelected())
            attr |= FileChunk.ATTR_VFAT_HIDDEN;
        if (attrS.isSelected())
            attr |= FileChunk.ATTR_VFAT_SYSTEM;
        directoryChunk.setAttributes(attr);
        directoryChunk.setGroup(Short.parseShort(group.getText()));
        directoryChunk.setOwner(Short.parseShort(owner.getText()));
        directoryChunk.setOrder(Short.parseShort(order.getText()));
        stage.close();
    }

}
