package korgpkg;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by korgeaux on 08.05.2016.
 */
public class HeaderChunk extends Chunk {

    private byte[] unknown;
    private String systemType1;
    private String systemType2;
    private String buildSystem;
    private String date;
    private String time;
    private String packageType1;
    private String packageType2;

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

    public void setUnknown(byte[] unknown) {
        this.unknown = unknown;
    }

    public byte[] getUnknown() {
        return unknown;
    }

    public void setPackageType1(String packageType1) {
        this.packageType1 = packageType1;
    }

    public String getPackageType1() {
        return packageType1;
    }

    public void setPackageType2(String packageType2) {
        this.packageType2 = packageType2;
    }

    public String getPackageType2() {
        return packageType2;
    }

    public void setBuildSystem(String buildSystem) {
        this.buildSystem = buildSystem;
    }

    public String getBuildSystem() {
        return buildSystem;
    }

    public Date getDateTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        Date tmpDate = new Date();
        try {
            tmpDate = simpleDateFormat.parse(date + " " + time);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
        return tmpDate;
    }

    public void setDateTime(Date date) {
        this.date = String.format("%tm/%<td/%<tY", date);
        this.time = String.format("%tH:%<tM", date);
    }

    public void setSystemType1(String systemType1) {
        this.systemType1 = systemType1;
    }

    public String getSystemType1() {
        return systemType1;
    }

    public void setSystemType2(String systemType2) {
        this.systemType2 = systemType2;
    }

    public String getSystemType2() {
        return systemType2;
    }

    @Override
    public String toString() {
        return "Header: " + systemType1 + " " + date + " " + time;
    }

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
