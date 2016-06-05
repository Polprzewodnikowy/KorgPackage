package korgpkgedit;

import javafx.scene.control.TextField;
import javafx.stage.Stage;
import korgpkg.Chunk;
import korgpkg.HeaderChunk;
import tornadofx.control.DateTimePicker;

import javax.xml.bind.DatatypeConverter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by korgeaux on 31.05.2016.
 */
public class HeaderEditController implements ChunkEditController {

    private Stage stage;
    private HeaderChunk headerChunk;

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
        unknown.setText(DatatypeConverter.printHexBinary(headerChunk.getUnknown()));
        systemType.setText(headerChunk.getSystemType());
        buildSystem1.setText(headerChunk.getBuildSystem1());
        buildSystem2.setText(headerChunk.getBuildSystem2());
        dateTime.setDateTimeValue(LocalDateTime.ofInstant(headerChunk.getDateTime().toInstant(), ZoneId.systemDefault()));
        packageType1.setText(headerChunk.getPackageType1());
        packageType2.setText(headerChunk.getPackageType2());
    }

    public void saveChunkAction() {
        headerChunk.setSystemType(systemType.getText());
        headerChunk.setBuildSystem1(buildSystem1.getText());
        headerChunk.setBuildSystem2(buildSystem2.getText());
        headerChunk.setDateTime(Date.from(dateTime.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant()));
        headerChunk.setPackageType1(packageType1.getText());
        headerChunk.setPackageType2(packageType2.getText());
        stage.close();
    }

}
