package korgpkg;

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

    private File file;

    private String name;

    public RootFSChunk() {
        id = ROOT_FS;
        name = "";
        try {
            file = Files.createTempFile("", ".RootFSChunk").toFile();
            file.deleteOnExit();
        } catch (IOException e) {
            System.err.println(e.getMessage());
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
            writeData(file, data);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public byte[] getData() {
        byte[] data = new byte[0];
        try {
            data = readData(file);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            return data;
        }
    }

    @Override
    public String toString() {
        return "FileSystem: " + name;
    }

    public void load(RandomAccessFile reader, int size) throws IOException {
        reader.skipBytes(16);
        reader.skipBytes(4);
        reader.skipBytes(2);
        name = readString(reader);
        int dataSize = size - 16 - 4 - 2 - name.length() - 1;
        byte[] data = new byte[dataSize];
        reader.read(data, 0, dataSize);
        writeData(file, data);
    }

    public void save(RandomAccessFile writer) throws IOException {
        byte[] data = readData(file);
        byte[] hash = new byte[16];
        try {
            hash = MessageDigest.getInstance("MD5").digest(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        writer.writeInt(Integer.reverseBytes(id));
        writer.writeInt(Integer.reverseBytes(data.length + 16 + 4 + 2 + name.length() + 1));
        writer.write(hash);
        writer.writeInt(Integer.reverseBytes(data.length & 0xFFFFFF00));
        writer.writeShort(0x0200);
        writeString(writer, name);
        writer.write(data);
    }

    public void export(String path) throws IOException {
        String tmpName = name.charAt(0) == '/' ? name.substring(1) : name;
        Path tmpPath = Paths.get(path, tmpName);
        tmpPath.getParent().toFile().mkdirs();
        Files.copy(file.toPath(), tmpPath, REPLACE_EXISTING);
    }

}
