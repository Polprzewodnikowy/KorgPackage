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
    private String systemType;
    private String buildSystem1;
    private String buildSystem2;
    private String date;
    private String time;
    private String packageType1;
    private String packageType2;

    public HeaderChunk() {
        id = HEADER;
        unknown = new byte[]{3, 6, 4, 3, 3, 0, 0, 0, 2, 0, 1, 0};
        systemType = "103ASTD";
        buildSystem1 = "KORG-SW1";
        buildSystem2 = "WorkHorse2";
        date = "01/01/2014";
        time = "12:00";
        packageType1 = "Z103A package";
        packageType2 = " user package";
    }

    public void setUnknown(byte[] unknown) {
        this.unknown = unknown;
    }

    public byte[] getUnknown() {
        return unknown;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setBuildSystem1(String buildSystem1) {
        this.buildSystem1 = buildSystem1;
    }

    public String getBuildSystem1() {
        return buildSystem1;
    }

    public void setBuildSystem2(String buildSystem2) {
        this.buildSystem2 = buildSystem2;
    }

    public String getBuildSystem2() {
        return buildSystem2;
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

    @Override
    public String toString() {
        return "Header: " + systemType + " " + date + " " + time;
    }

    public void load(RandomAccessFile reader, int size) throws IOException {
        unknown = new byte[12];
        reader.read(unknown, 0, 12);
        systemType = readString(reader);
        buildSystem1 = readString(reader);
        buildSystem2 = readString(reader);
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
        writeString(writer, systemType);
        writeString(writer, buildSystem1);
        writeString(writer, buildSystem2);
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
        fileWriter.write(systemType + "\r\n");
        fileWriter.write(buildSystem1 + "\r\n");
        fileWriter.write(buildSystem2 + "\r\n");
        fileWriter.write(date + "\r\n");
        fileWriter.write(time + "\r\n");
        fileWriter.write(packageType1 + "\r\n");
        fileWriter.write(packageType2 + "\r\n");
        fileWriter.close();
    }

}
