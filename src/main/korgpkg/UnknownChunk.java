package korgpkg;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by korgeaux on 16.05.2017.
 */
public class UnknownChunk extends Chunk {

    private int size;
    private long pos;
    private File file;

    public UnknownChunk(int id, int size, long pos) {
        this.id = id;
        this.size = size;
        this.pos = pos;
        try {
            file = Files.createTempFile("", ".UnknownChunk").toFile();
            file.deleteOnExit();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void setData(byte[] data) {
        try {
            writeData(file, data);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public byte[] getData() {
        byte[] data = new byte[0];
        try {
            data = readData(file);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            return data;
        }
    }

    @Override
    public String toString() {
        return "UnknownChunk: ID: 0x" + Integer.toHexString(id) + " LEN: 0x" + Integer.toHexString(size) + " AT: 0x" + Long.toHexString(pos);
    }

    @Override
    public void load(RandomAccessFile reader, int size) throws IOException {
        byte[] data = new byte[size];
        reader.read(data, 0, size);
        writeData(file, data);
    }

    @Override
    public void save(RandomAccessFile writer) throws IOException {
        byte[] data = readData(file);
        writer.writeInt(Integer.reverseBytes(id));
        long offset = writer.getFilePointer();
        writer.write(new byte[4]);
        writer.writeInt(Integer.reverseBytes(data.length));
        writer.write(data);

        int size = (int) (writer.getFilePointer() - offset - 4);
        writer.seek(offset);
        writer.writeInt(Integer.reverseBytes(size));
    }

    @Override
    public void export(String path) throws IOException {
        String tmpName = "ID_0x" + Integer.toHexString(this.id) + "_LEN_0x" + Long.toHexString(file.length()) + "_AT_0x" + Long.toHexString(pos);
        Path tmpPath = Paths.get(path, tmpName);
        tmpPath.getParent().toFile().mkdirs();
        Files.copy(file.toPath(), tmpPath, REPLACE_EXISTING);
    }
}
