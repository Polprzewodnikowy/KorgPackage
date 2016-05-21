package korgpackage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

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
    public void export(String path) throws IOException {
        if(path.length() > 0)
            path = path + "/";
        String dirPath = path + name.substring(name.indexOf('/') + 1);
        new File(dirPath).mkdirs();
    }

}
