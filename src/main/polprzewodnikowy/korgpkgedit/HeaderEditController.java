package polprzewodnikowy.korgpkgedit;

import javafx.scene.control.TextField;
import javafx.stage.Stage;
import polprzewodnikowy.korgpkg.Chunk;
import polprzewodnikowy.korgpkg.HeaderChunk;
import tornadofx.control.DateTimePicker;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by korgeaux on 31.05.2016.
 */
public class HeaderEditController implements ChunkEditController {

    Stage stage;
    HeaderChunk headerChunk;

    public TextField systemType1;
    public TextField systemType2;
    public TextField buildSystem;
    public DateTimePicker dateTime;
    public TextField packageType1;
    public TextField packageType2;

    public void setup(Stage stage, Chunk chunk) {
        this.stage = stage;
        this.headerChunk = (HeaderChunk) chunk;
        systemType1.setText(headerChunk.getSystemType1());
        systemType2.setText(headerChunk.getSystemType2());
        buildSystem.setText(headerChunk.getBuildSystem());
        dateTime.setDateTimeValue(LocalDateTime.ofInstant(headerChunk.getDateTime().toInstant(), ZoneId.systemDefault()));
        packageType1.setText(headerChunk.getPackageType1());
        packageType2.setText(headerChunk.getPackageType2());
    }

    public void saveChunkAction() {
        headerChunk.setSystemType1(systemType1.getText());
        headerChunk.setSystemType2(systemType2.getText());
        headerChunk.setBuildSystem(buildSystem.getText());
        headerChunk.setDateTime(Date.from(dateTime.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant()));
        headerChunk.setPackageType1(packageType1.getText());
        headerChunk.setPackageType2(packageType2.getText());
        stage.close();
    }

}
