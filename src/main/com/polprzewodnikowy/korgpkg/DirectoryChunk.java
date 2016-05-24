package com.polprzewodnikowy.korgpkg;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by korgeaux on 19.05.2016.
 */
public class DirectoryChunk extends Chunk {

    int unknown1;
    short attributes;
    short unknown2;
    String name;

    public DirectoryChunk() {
        id = DIRECTORY;
        unknown1 = 0;
        attributes = 0x7000;
        unknown2 = -1;
        name = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void load(RandomAccessFile reader, int size) throws IOException {
        unknown1 = Integer.reverseBytes(reader.readInt());
        attributes = Short.reverseBytes(reader.readShort());
        unknown2 = Short.reverseBytes(reader.readShort());
        name = readString(reader);
    }

    @Override
    public void save(RandomAccessFile writer) throws IOException {
        writer.writeInt(Integer.reverseBytes(id));
        long offset = writer.getFilePointer();
        writer.write(new byte[4]);
        writer.writeInt(Integer.reverseBytes(unknown1));
        writer.writeShort(Short.reverseBytes(attributes));
        writer.writeShort(Short.reverseBytes(unknown2));
        writeString(writer, name);
        int size = (int) (writer.getFilePointer() - offset - 4);
        writer.seek(offset);
        writer.writeInt(Integer.reverseBytes(size));
    }

    @Override
    public void export(String path) throws IOException {
        String tmpName = name.charAt(0) == '/' ? name.substring(1) : name;
        Path tmpPath = Paths.get(path, tmpName);
        tmpPath.toFile().mkdirs();
    }

}
