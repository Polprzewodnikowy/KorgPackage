package com.polprzewodnikowy.korgpkg;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by korgeaux on 18.05.2016.
 */
public class RootFSChunk extends Chunk {

    String name;
    byte[] data;

    public RootFSChunk() {
        id = ROOT_FS;
        name = "";
        data = new byte[0];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public void load(RandomAccessFile reader, int size) throws IOException {
        reader.skipBytes(16);
        reader.skipBytes(4);
        reader.skipBytes(2);
        name = readString(reader);

        int dataSize = size - 16 - 4 - 2 - name.length() - 1;
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
        writer.writeInt(Integer.reverseBytes(data.length + 16 + 4 + 2 + name.length() + 1));
        writer.write(hash);
        writer.writeInt(Integer.reverseBytes(data.length & 0xFFFFFF00));
        writer.writeShort(0x0200);
        writeString(writer, name);
        writer.write(data);
    }

    @Override
    public void export(String path) throws IOException {
        String tmpName = name.substring(name.indexOf('/') + 1);
        Path tmpPath = Paths.get(path, tmpName);
        tmpPath.getParent().toFile().mkdirs();
        FileOutputStream fileOutputStream = new FileOutputStream(tmpPath.toFile());
        fileOutputStream.write(data);
        fileOutputStream.close();
    }

}
