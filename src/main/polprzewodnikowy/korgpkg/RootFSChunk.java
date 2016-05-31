package polprzewodnikowy.korgpkg;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by korgeaux on 18.05.2016.
 */
public class RootFSChunk extends Chunk {

    String name;
    File tmpFile;

    public RootFSChunk() {
        id = ROOT_FS;
        name = "";
        try {
            tmpFile = Files.createTempFile("", ".RootFSChunk").toFile();
            tmpFile.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        str.append("[" + id + " RootFSChunk]: ");
        str.append(name);
        return str.toString();
    }

    @Override
    public void load(RandomAccessFile reader, int size) throws IOException {
        reader.skipBytes(16);
        reader.skipBytes(4);
        reader.skipBytes(2);
        name = readString(reader);
        int dataSize = size - 16 - 4 - 2 - name.length() - 1;
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
        writer.writeInt(Integer.reverseBytes(tmpData.length + 16 + 4 + 2 + name.length() + 1));
        writer.write(hash);
        writer.writeInt(Integer.reverseBytes(tmpData.length & 0xFFFFFF00));
        writer.writeShort(0x0200);
        writeString(writer, name);
        writer.write(tmpData);
    }

    @Override
    public void export(String path) throws IOException {
        String tmpName = name.charAt(0) == '/' ? name.substring(1) : name;
        Path tmpPath = Paths.get(path, tmpName);
        tmpPath.getParent().toFile().mkdirs();
        Files.copy(tmpFile.toPath(), tmpPath, REPLACE_EXISTING);
    }

}
