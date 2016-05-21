package korgpackage;

import javax.xml.bind.DatatypeConverter;
import java.io.*;

/**
 * Created by korgeaux on 08.05.2016.
 */
public class HeaderChunk extends Chunk {

    byte[] unknown;
    String systemType1;
    String systemType2;
    String buildSystem;
    String date;
    String time;
    String packageType1;
    String packageType2;

    public HeaderChunk() {
        id = HEADER;
    }

    @Override
    public void load(RandomAccessFile reader, int size) throws IOException {
        unknown = new byte[12];
        reader.read(unknown, 0, 12); //some unknown bytes
        systemType1 = readString(reader);
        systemType2 = readString(reader);
        buildSystem = readString(reader);
        date = readString(reader);
        time = readString(reader);
        packageType1 = readString(reader);
        packageType2 = readString(reader);
    }

    @Override
    public void export(String path) throws IOException {
        if(path.length() > 0)
            path = path + "/";
        String dirPath = path;
        String filePath = path + "Header.txt";
        new File(dirPath).mkdirs();
        FileWriter fileWriter = new FileWriter(filePath);
        fileWriter.write(DatatypeConverter.printHexBinary(unknown) + "\r\n");
        fileWriter.write(systemType1 + "\r\n");
        fileWriter.write(systemType2 + "\r\n");
        fileWriter.write(buildSystem + "\r\n");
        fileWriter.write(date + "\r\n");
        fileWriter.write(time + "\r\n");
        fileWriter.write(packageType1 + "\r\n");
        fileWriter.write(packageType2 + "\r\n");
        fileWriter.close();
    }

}
