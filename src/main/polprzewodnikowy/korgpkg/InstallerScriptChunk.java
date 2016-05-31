package polprzewodnikowy.korgpkg;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by korgeaux on 19.05.2016.
 */
public class InstallerScriptChunk extends Chunk {

    File tmpFile;

    short order;
    String name;

    public InstallerScriptChunk() {
        id = INSTALLER_SCRIPT;
        order = -1;
        name = "";
        try {
            tmpFile = Files.createTempFile("", ".InstallerScriptChunk").toFile();
            tmpFile.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public short getOrder() {
        return order;
    }

    public void setOrder(short order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setData(byte[] data) {
        try {
            if (tmpFile.exists())
                tmpFile.delete();
            FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);
            fileOutputStream.write(data);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getData() {
        byte[] tmpData = new byte[0];
        try {
            FileInputStream fileInputStream = new FileInputStream(tmpFile);
            tmpData = new byte[fileInputStream.available()];
            fileInputStream.read(tmpData, 0, fileInputStream.available());
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return tmpData;
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("[" + id + " InstallerScriptChunk]: ");
        str.append(name + " | " + order);
        return str.toString();
    }

    @Override
    public void load(RandomAccessFile reader, int size) throws IOException {
        reader.skipBytes(16);
        order = Short.reverseBytes(reader.readShort());
        name = readString(reader);
        int dataSize = size - 16 - 2 - name.length() - 1;
        byte[] tmpData = new byte[dataSize];
        reader.read(tmpData, 0, dataSize);

        if (tmpFile.exists())
            tmpFile.delete();
        FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);
        fileOutputStream.write(tmpData);
        fileOutputStream.close();
    }

    @Override
    public void save(RandomAccessFile writer) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(tmpFile);
        fileInputStream.available();
        byte[] tmpData = new byte[fileInputStream.available()];
        fileInputStream.read(tmpData, 0, fileInputStream.available());

        byte[] hash = new byte[16];
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(tmpData);
            hash = md5.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        writer.writeInt(Integer.reverseBytes(id));
        writer.writeInt(Integer.reverseBytes(tmpData.length + 16 + 2 + name.length() + 1));
        writer.write(hash);
        writer.writeShort(Short.reverseBytes(order));
        writeString(writer, name);
        writer.write(tmpData);
    }

    @Override
    public void export(String path) throws IOException {
        Path tmpPath = Paths.get(path, "update", name);
        tmpPath.getParent().toFile().mkdirs();
        Files.copy(tmpFile.toPath(), tmpPath, REPLACE_EXISTING);
    }

}
