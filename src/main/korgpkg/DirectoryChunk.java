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
    private short condition;
    private String path;

    public DirectoryChunk() {
        id = DIRECTORY;
        owner = 0;
        group = 0;
        attributes = ATTR_VFAT_ARCHIVE | ATTR_VFAT_READONLY | ATTR_VFAT_SYSTEM;
        condition = -1;
        path = "/omega_sys/directory";
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

    public short getCondition() {
        return condition;
    }

    public void setCondition(short condition) {
        this.condition = condition;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Directory: " + path;
    }

    public void load(RandomAccessFile reader, int size) throws IOException {
        owner = Short.reverseBytes(reader.readShort());
        group = Short.reverseBytes(reader.readShort());
        attributes = Short.reverseBytes(reader.readShort());
        condition = Short.reverseBytes(reader.readShort());
        path = readString(reader);
    }

    public void save(RandomAccessFile writer) throws IOException {
        writer.writeInt(Integer.reverseBytes(id));
        long offset = writer.getFilePointer();
        writer.write(new byte[4]);
        writer.writeShort(Short.reverseBytes(owner));
        writer.writeShort(Short.reverseBytes(group));
        writer.writeShort(Short.reverseBytes(attributes));
        writer.writeShort(Short.reverseBytes(condition));
        writeString(writer, path);
        int size = (int) (writer.getFilePointer() - offset - 4);
        writer.seek(offset);
        writer.writeInt(Integer.reverseBytes(size));
    }

    public void export(String path) throws IOException {
        String tmpName = this.path.charAt(0) == '/' ? this.path.substring(1) : this.path;
        Path tmpPath = Paths.get(path, tmpName);
        tmpPath.toFile().mkdirs();
    }

}
