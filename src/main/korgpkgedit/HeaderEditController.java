package korgpkgedit;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import korgpkg.Chunk;
import korgpkg.HeaderChunk;
import tornadofx.control.DateTimePicker;

import javax.xml.bind.DatatypeConverter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by korgeaux on 31.05.2016.
 */
public class HeaderEditController implements ChunkEditController {

    private Stage stage;
    private HeaderChunk headerChunk;

    public TextField pkgLibVersion;
    public ChoiceBox pkgType;
    public TextField unknown;
    public TextField systemType;
    public TextField buildSystem1;
    public TextField buildSystem2;
    public DateTimePicker dateTime;
    public TextField packageType1;
    public TextField packageType2;

    public void setup(Stage stage, Chunk chunk) {
        this.stage = stage;
        this.headerChunk = (HeaderChunk) chunk;
        stage.setTitle(headerChunk.toString());
        pkgLibVersion.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!Pattern.matches("\\A[1-9]{1,}[.][1-9]{1,}[.][1-9]{1,}[.][1-9]{1,}\\Z", pkgLibVersion.getText())) {
                pkgLibVersion.setText(headerChunk.getPkgLibVersion()[0] + "." + headerChunk.getPkgLibVersion()[1] + "." + headerChunk.getPkgLibVersion()[2] + "." + headerChunk.getPkgLibVersion()[3]);
            }
        });
        pkgLibVersion.setText(headerChunk.getPkgLibVersion()[0] + "." + headerChunk.getPkgLibVersion()[1] + "." + headerChunk.getPkgLibVersion()[2] + "." + headerChunk.getPkgLibVersion()[3]);
        if (headerChunk.getPkgType() == HeaderChunk.PKG_TYPE_UPDATE)
            pkgType.getSelectionModel().select(0);
        else if (headerChunk.getPkgType() == HeaderChunk.PKG_TYPE_FULL)
            pkgType.getSelectionModel().select(1);
        unknown.setText("[" + headerChunk.getUnknown()[0] + ", " + headerChunk.getUnknown()[1] + "]");
        systemType.setText(headerChunk.getSystemType());
        buildSystem1.setText(headerChunk.getBuildSystem1());
        buildSystem2.setText(headerChunk.getBuildSystem2());
        dateTime.setDateTimeValue(LocalDateTime.ofInstant(headerChunk.getDateTime().toInstant(), ZoneId.systemDefault()));
        packageType1.setText(headerChunk.getPackageType1());
        packageType2.setText(headerChunk.getPackageType2());
    }

    public void saveChunkAction() {
        String ver[] = pkgLibVersion.getText().split("\\.");
        headerChunk.setPkgLibVersion(Byte.parseByte(ver[0]), Byte.parseByte(ver[1]), Byte.parseByte(ver[2]), Byte.parseByte(ver[3]));
        if (pkgType.getSelectionModel().getSelectedIndex() == 0)
            headerChunk.setPkgType(HeaderChunk.PKG_TYPE_UPDATE);
        else if (pkgType.getSelectionModel().getSelectedIndex() == 1)
            headerChunk.setPkgType(HeaderChunk.PKG_TYPE_FULL);
        headerChunk.setSystemType(systemType.getText());
        headerChunk.setBuildSystem1(buildSystem1.getText());
        headerChunk.setBuildSystem2(buildSystem2.getText());
        headerChunk.setDateTime(Date.from(dateTime.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant()));
        headerChunk.setPackageType1(packageType1.getText());
        headerChunk.setPackageType2(packageType2.getText());
        stage.close();
    }

}
