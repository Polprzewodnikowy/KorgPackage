package korgpkgedit;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import korgpkg.Chunk;
import korgpkg.DirectoryChunk;
import korgpkg.FileChunk;

/**
 * Created by korgeaux on 31.05.2016.
 */
public class DirectoryEditController implements ChunkEditController {

    private Stage stage;
    private DirectoryChunk directoryChunk;

    public TextField path;
    public CheckBox attrA;
    public CheckBox attrR;
    public CheckBox attrH;
    public CheckBox attrS;
    public TextField group;
    public TextField owner;
    public TextField condition;

    public void setup(Stage stage, Chunk chunk) {
        this.stage = stage;
        this.directoryChunk = (DirectoryChunk) chunk;
        stage.setTitle(directoryChunk.toString());
        path.setText(directoryChunk.getPath());
        int attr = directoryChunk.getAttributes();
        if ((attr & FileChunk.ATTR_VFAT_ARCHIVE) != 0)
            attrA.setSelected(true);
        if ((attr & FileChunk.ATTR_VFAT_READONLY) != 0)
            attrR.setSelected(true);
        if ((attr & FileChunk.ATTR_VFAT_HIDDEN) != 0)
            attrH.setSelected(true);
        if ((attr & FileChunk.ATTR_VFAT_SYSTEM) != 0)
            attrS.setSelected(true);
        group.setText(Integer.toString(directoryChunk.getGroup()));
        owner.setText(Integer.toString(directoryChunk.getOwner()));
        condition.setText(Integer.toString(directoryChunk.getCondition()));
    }

    public void saveChunkAction() {
        directoryChunk.setPath(path.getText());
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
        directoryChunk.setCondition(Short.parseShort(condition.getText()));
        stage.close();
    }

}
