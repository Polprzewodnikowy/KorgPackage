package korgpackage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by korgeaux on 19.05.2016.
 */
public class RamdiskChunk extends Chunk {

    byte[] data;

    public RamdiskChunk(int id) {
        this.id = id;
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
        String prefix;
        switch (id) {
            case UPDATE_RAMDISK:
                prefix = "update";
                break;
            case SERVICE_RAMDISK:
                prefix = "service";
                break;
            default:
                prefix = "";
                break;
        }
        if(path.length() > 0)
            path = path + "/";
        String dirPath = path + prefix;
        String filePath = path + prefix + "/ramdisk.gz";
        new File(dirPath).mkdirs();
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        fileOutputStream.write(data);
        fileOutputStream.close();
    }

}
