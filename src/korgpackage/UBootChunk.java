package korgpackage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by korgeaux on 19.05.2016.
 */
public class UBootChunk extends Chunk {

    byte[] data;

    public UBootChunk() {
        id = UBOOT;
    }

    @Override
    public void load(RandomAccessFile reader, int size) throws IOException {
        reader.skipBytes(16); //MD5
        int dataSize = size - 16;
        data = new byte[dataSize];
        reader.read(data, 0, dataSize);
    }

    @Override
    public void export(String path) throws IOException {
        if(path.length() > 0)
            path = path + "/";
        String dirPath = path + "boot";
        String filePath = path + "boot/UBoot";
        new File(dirPath).mkdirs();
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        fileOutputStream.write(data);
        fileOutputStream.close();
    }

}
