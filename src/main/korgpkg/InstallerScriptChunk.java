package korgpkg;

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

    private File file;

    private short order;
    private String name;

    public InstallerScriptChunk() {
        id = INSTALLER_SCRIPT;
        order = -1;
        name = "";
        try {
            file = Files.createTempFile("", ".InstallerScriptChunk").toFile();
            file.deleteOnExit();
        } catch (IOException e) {
            System.err.println(e.getMessage());
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
        return "InstallerScript: " + name;
    }

    public void load(RandomAccessFile reader, int size) throws IOException {
        reader.skipBytes(16);
        order = Short.reverseBytes(reader.readShort());
        name = readString(reader);
        int dataSize = size - 16 - 2 - name.length() - 1;
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
            System.err.println(e.getMessage());
        }
        writer.writeInt(Integer.reverseBytes(id));
        writer.writeInt(Integer.reverseBytes(data.length + 16 + 2 + name.length() + 1));
        writer.write(hash);
        writer.writeShort(Short.reverseBytes(order));
        writeString(writer, name);
        writer.write(data);
    }

    public void export(String path) throws IOException {
        Path tmpPath = Paths.get(path, "update", name);
        tmpPath.getParent().toFile().mkdirs();
        Files.copy(file.toPath(), tmpPath, REPLACE_EXISTING);
    }

}
