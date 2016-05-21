package com.polprzewodnikowy.korgpkg;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by korgeaux on 19.05.2016.
 */
public class DirectoryChunk extends Chunk {

    int unknown1;
    short permissions;
    short unknown2;
    String name;

    public DirectoryChunk() {
        id = DIRECTORY;
    }

    @Override
    public void load(RandomAccessFile reader, int size) throws IOException {
        unknown1 = Integer.reverseBytes(reader.readInt());
        permissions = Short.reverseBytes(reader.readShort());
        unknown2 = Short.reverseBytes(reader.readShort());
        name = readString(reader);
    }

    @Override
    public void save(RandomAccessFile writer) throws IOException {
        writer.writeInt(Integer.reverseBytes(id));
        long offset = writer.getFilePointer();
        writer.write(new byte[4]);
        writer.writeInt(Integer.reverseBytes(unknown1));
        writer.writeShort(Short.reverseBytes(permissions));
        writer.writeShort(Short.reverseBytes(unknown2));
        writeString(writer, name);
        int size = (int)(writer.getFilePointer() - offset - 4);
        writer.seek(offset);
        writer.writeInt(Integer.reverseBytes(size));
    }

    @Override
    public void export(String path) throws IOException {
        if(path.length() > 0)
            path = path + "/";
        String dirPath = path + name.substring(name.indexOf('/') + 1);
        new File(dirPath).mkdirs();
    }

}
