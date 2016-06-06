package korgpkg;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by korgeaux on 29.05.2016.
 */
public class SystemFileChunk extends Chunk {

    private File file;

    public SystemFileChunk(int id) {
        this.id = id;
        try {
            file = Files.createTempFile("", ".SystemFileChunk").toFile();
            file.deleteOnExit();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void setData(byte[] data) {
        try {
            writeData(file, data);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public byte[] getData() {
        byte[] tmpData = new byte[0];
        try {
            tmpData = readData(file);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            return tmpData;
        }
    }

    @Override
    public String toString() {
        return "SystemFile: /" + getPrefix() + "/" + getName();
    }

    public void load(RandomAccessFile reader, int size) throws IOException {
        reader.skipBytes(16);
        int dataSize = size - 16;
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
        writer.writeInt(Integer.reverseBytes(data.length + 16));
        writer.write(hash);
        writer.write(data);
    }

    public void export(String path) throws IOException {
        String prefix = getPrefix();
        String name = getName();
        Path tmpPath = Paths.get(path, prefix, name);
        tmpPath.getParent().toFile().mkdirs();
        Files.copy(file.toPath(), tmpPath, REPLACE_EXISTING);
    }

    private String getName() {
        switch (id) {
            case UPDATE_KERNEL:
                return "uImage";
            case UPDATE_RAMDISK:
                return "ramdisk.gz";
            case UPDATE_INSTALLER_APP:
                return "lfo-pkg-install";
            case UPDATE_INSTALLER_APP_CONFIG:
                return "lfo-pkg-install.xml";
            case SERVICE_KERNEL:
                return "uImage";
            case SERVICE_RAMDISK:
                return "ramdisk.gz";
            case SERVICE_APP:
                return "lfo-service";
            case SERVICE_APP_CONFIG:
                return "lfo-service.xml";
            case UPDATE_LAUNCHER_APP:
                return "lfo-pkg-launcher";
            case UPDATE_LAUNCHER_APP_CONFIG:
                return "lfo-pkg-launcher.xml";
            case MLO:
                return "MLO";
            case UBOOT:
                return "u-boot.bin";
            case USER_KERNEL:
                return "uImage";
            default:
                return "unknown";
        }
    }

    private String getPrefix() {
        switch (id) {
            case UPDATE_KERNEL:
            case UPDATE_RAMDISK:
            case UPDATE_INSTALLER_APP:
            case UPDATE_INSTALLER_APP_CONFIG:
                return "update";
            case SERVICE_KERNEL:
            case SERVICE_RAMDISK:
            case SERVICE_APP:
            case SERVICE_APP_CONFIG:
            case UPDATE_LAUNCHER_APP:
            case UPDATE_LAUNCHER_APP_CONFIG:
                return "service";
            case MLO:
            case UBOOT:
                return "boot";
            case USER_KERNEL:
                return "kernel";
            default:
                return "unknown";
        }
    }

}
