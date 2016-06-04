package korgpkg;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by korgeaux on 19.05.2016.
 */
public class DirectoryChunk extends Chunk {

    private short owner;
    private short group;
    private short attributes;
    private short order;
    private String name;

    public DirectoryChunk() {
        id = DIRECTORY;
        owner = 0;
        group = 0;
        attributes = ATTR_VFAT_ARCHIVE | ATTR_VFAT_READONLY | ATTR_VFAT_SYSTEM;
        order = -1;
        name = "";
    }

    public short getOwner() {
        return owner;
    }

    public void setOwner(short owner) {
        this.owner = owner;
    }

    public short getGroup() {
        return group;
    }

    public void setGroup(short group) {
        this.group = group;
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
        return "Directory: " + name;
    }

    public void load(RandomAccessFile reader, int size) throws IOException {
        owner = Short.reverseBytes(reader.readShort());
        group = Short.reverseBytes(reader.readShort());
        attributes = Short.reverseBytes(reader.readShort());
        order = Short.reverseBytes(reader.readShort());
        name = readString(reader);
    }

    public void save(RandomAccessFile writer) throws IOException {
        writer.writeInt(Integer.reverseBytes(id));
        long offset = writer.getFilePointer();
        writer.write(new byte[4]);
        writer.writeShort(Short.reverseBytes(owner));
        writer.writeShort(Short.reverseBytes(group));
        writer.writeShort(Short.reverseBytes(attributes));
        writer.writeShort(Short.reverseBytes(order));
        writeString(writer, name);
        int size = (int) (writer.getFilePointer() - offset - 4);
        writer.seek(offset);
        writer.writeInt(Integer.reverseBytes(size));
    }

    public void export(String path) throws IOException {
        String tmpName = name.charAt(0) == '/' ? name.substring(1) : name;
        Path tmpPath = Paths.get(path, tmpName);
        tmpPath.toFile().mkdirs();
    }

}
