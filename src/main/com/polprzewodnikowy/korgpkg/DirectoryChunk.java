package com.polprzewodnikowy.korgpkg;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by korgeaux on 19.05.2016.
 */
public class DirectoryChunk extends Chunk {

    public final static int ATTR_ARCHIVE = 0x1000;
    public final static int ATTR_READ_ONLY = 0x2000;
    public final static int ATTR_SYSTEM = 0x4000;
    public final static int ATTR_HIDDEN = 0x8000;

    short attributes;
    short order;
    String name;

    public DirectoryChunk() {
        id = DIRECTORY;
        attributes = ATTR_ARCHIVE | ATTR_READ_ONLY | ATTR_SYSTEM;
        order = -1;
        name = "";
    }

    public int getAttributes() {
        return attributes;
    }

    public void setAttributes(int attributes) {
        this.attributes = (short) attributes;
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

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("[" + id + " DirectoryChunk]: ");
        str.append(name + " | [");
        if ((attributes & ATTR_ARCHIVE) == ATTR_ARCHIVE)
            str.append("A");
        if ((attributes & ATTR_READ_ONLY) == ATTR_READ_ONLY)
            str.append("R");
        if ((attributes & ATTR_SYSTEM) == ATTR_SYSTEM)
            str.append("S");
        if ((attributes & ATTR_HIDDEN) == ATTR_HIDDEN)
            str.append("H");
        str.append("] | " + order);
        return str.toString();
    }

    @Override
    public void load(RandomAccessFile reader, int size) throws IOException {
        reader.skipBytes(4);
        attributes = Short.reverseBytes(reader.readShort());
        order = Short.reverseBytes(reader.readShort());
        name = readString(reader);
    }

    @Override
    public void save(RandomAccessFile writer) throws IOException {
        writer.writeInt(Integer.reverseBytes(id));
        long offset = writer.getFilePointer();
        writer.write(new byte[4]);
        writer.write(new byte[4]);
        writer.writeShort(Short.reverseBytes(attributes));
        writer.writeShort(Short.reverseBytes(order));
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
