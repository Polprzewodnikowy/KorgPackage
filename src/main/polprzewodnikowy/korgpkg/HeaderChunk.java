package polprzewodnikowy.korgpkg;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        unknown = new byte[12];
        systemType1 = "";
        systemType2 = "";
        buildSystem = "";
        date = "";
        time = "";
        packageType1 = "";
        packageType2 = "";
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("[" + id + " HeaderChunk]: ");
        str.append(systemType1 + " " + date + " " + time);
        return str.toString();
    }

    @Override
    public void load(RandomAccessFile reader, int size) throws IOException {
        unknown = new byte[12];
        reader.read(unknown, 0, 12);
        systemType1 = readString(reader);
        systemType2 = readString(reader);
        buildSystem = readString(reader);
        date = readString(reader);
        time = readString(reader);
        packageType1 = readString(reader);
        packageType2 = readString(reader);
    }

    @Override
    public void save(RandomAccessFile writer) throws IOException {
        writer.writeInt(Integer.reverseBytes(id));
        long offset = writer.getFilePointer();
        writer.write(new byte[4]);
        writer.write(unknown);
        writeString(writer, systemType1);
        writeString(writer, systemType2);
        writeString(writer, buildSystem);
        writeString(writer, date);
        writeString(writer, time);
        writeString(writer, packageType1);
        writeString(writer, packageType2);
        int size = (int) (writer.getFilePointer() - offset - 4);
        writer.seek(offset);
        writer.writeInt(Integer.reverseBytes(size));
    }

    @Override
    public void export(String path) throws IOException {
        Path tmpPath = Paths.get(path, "Header.txt");
        tmpPath.getParent().toFile().mkdirs();
        FileWriter fileWriter = new FileWriter(tmpPath.toFile());
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
