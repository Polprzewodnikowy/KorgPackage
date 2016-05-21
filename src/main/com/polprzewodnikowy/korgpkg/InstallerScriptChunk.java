package com.polprzewodnikowy.korgpkg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by korgeaux on 19.05.2016.
 */
public class InstallerScriptChunk extends Chunk {

    short order;
    String name;
    byte[] data;

    public InstallerScriptChunk() {
        id = INSTALLER_SCRIPT;
    }

    @Override
    public void load(RandomAccessFile reader, int size) throws IOException {
        reader.skipBytes(16);
        order = Short.reverseBytes(reader.readShort());
        name = readString(reader);
        int dataSize = size - 16 - 2 - name.length() - 1;
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
        writer.writeInt(Integer.reverseBytes(data.length + 16 + 2 + name.length() + 1));
        writer.write(hash);
        writer.writeShort(Short.reverseBytes(order));
        writeString(writer, name);
        writer.write(data);
    }

    @Override
    public void export(String path) throws IOException {
        if(path.length() > 0)
            path = path + "/update/";
        else
            path = "update/";
        String dirPath = path;
        String filePath = path + name;
        new File(dirPath).mkdirs();
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        fileOutputStream.write(data);
        fileOutputStream.close();
    }

}
