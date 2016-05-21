package korgpackage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

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
        reader.skipBytes(16); //MD5
        reader.skipBytes(4); //Size & 0xFFFFFF00 ???
        reader.skipBytes(2); //unknown
        name = readString(reader);

        int dataSize = size - 16 - 4 - 2 - name.length() - 1;
        data = new byte[dataSize];
        reader.read(data, 0, dataSize);
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
