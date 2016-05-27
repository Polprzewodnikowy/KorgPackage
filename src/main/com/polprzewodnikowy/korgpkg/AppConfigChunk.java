package com.polprzewodnikowy.korgpkg;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by korgeaux on 19.05.2016.
 */
public class AppConfigChunk extends Chunk {

    byte[] data;

    public AppConfigChunk(int id) {
        this.id = id;
        data = new byte[0];
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("[" + id + " AppConfigChunk]: ");
        str.append(getName());
        return str.toString();
    }

    @Override
    public void load(RandomAccessFile reader, int size) throws IOException {
        reader.skipBytes(16);
        int dataSize = size - 16;
        data = new byte[dataSize];
        reader.read(data, 0, dataSize);
    }

    @Override
    public void save(RandomAccessFile writer) throws IOException {
        byte[] hash = new byte[16];
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(data);
            hash = md5.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        writer.writeInt(Integer.reverseBytes(id));
        writer.writeInt(Integer.reverseBytes(data.length + 16));
        writer.write(hash);
        writer.write(data);
    }

    @Override
    public void export(String path) throws IOException {
        String prefix = getPrefix();
        String name = getName();
        Path tmpPath = Paths.get(path, prefix, name);
        tmpPath.getParent().toFile().mkdirs();
        FileOutputStream fileOutputStream = new FileOutputStream(tmpPath.toFile());
        fileOutputStream.write(data);
        fileOutputStream.close();
    }

    private String getName() {
        switch (id) {
            case UPDATE_INSTALLER_APP_CONFIG:
                return "lfo-pkg-install.xml";
            case SERVICE_APP_CONFIG:
                return "lfo-service.xml";
            case UPDATE_LAUNCHER_APP_CONFIG:
                return "lfo-pkg-launcher.xml";
            default:
                return "unknown-app.xml";
        }
    }

    private String getPrefix() {
        switch (id) {
            case UPDATE_INSTALLER_APP_CONFIG:
                return "update";
            case SERVICE_APP_CONFIG:
                return "service";
            case UPDATE_LAUNCHER_APP_CONFIG:
                return "service";
            default:
                return "";
        }
    }

}
