package com.polprzewodnikowy.korgpkg;

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
public class DataChunk extends Chunk {

    File tmpFile;

    public DataChunk(int id) {
        this.id = id;
        try {
            tmpFile = Files.createTempFile("", ".DataChunk").toFile();
            tmpFile.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setData(byte[] data) {
        try {
            if(tmpFile.exists())
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
        str.append("[" + id + " DataChunk]: ");
        str.append(getPrefix() + "/");
        str.append(getName());
        return str.toString();
    }

    @Override
    public void load(RandomAccessFile reader, int size) throws IOException {
        reader.skipBytes(16);
        int dataSize = size - 16;
        byte[] tmpData = new byte[dataSize];
        reader.read(tmpData, 0, dataSize);

        if(tmpFile.exists())
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
        writer.writeInt(Integer.reverseBytes(tmpData.length + 16));
        writer.write(hash);
        writer.write(tmpData);
    }

    @Override
    public void export(String path) throws IOException {
        String prefix = getPrefix();
        String name = getName();
        Path tmpPath = Paths.get(path, prefix, name);
        tmpPath.getParent().toFile().mkdirs();
        Files.copy(tmpFile.toPath(), tmpPath, REPLACE_EXISTING);
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
                return "";
        }
    }

}
