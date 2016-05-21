package com.polprzewodnikowy.korgpkg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
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
        if(path.length() > 0)
            path = path + "/";
        String dirPath = path + name.substring(name.indexOf('/') + 1, name.lastIndexOf('/'));
        String filePath = path + name.substring(name.indexOf('/') + 1);
        new File(dirPath).mkdirs();
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        fileOutputStream.write(data);
        fileOutputStream.close();
    }

}
