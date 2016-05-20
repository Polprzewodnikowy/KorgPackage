package korgpackage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by korgeaux on 19.05.2016.
 */
public class AppChunk extends Chunk {

    byte[] data;

    public AppChunk(int id) {
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
        String name;
        switch (id) {
            case UPDATE_INSTALLER_APP:
                prefix = "update";
                name = "lfo-pkg-install";
                break;
            case SERVICE_APP:
                prefix = "service";
                name = "lfo-service";
                break;
            case UPDATE_LAUNCHER_APP:
                prefix = "service";
                name = "lfo-pkg-launcher";
                break;
            default:
                prefix = "";
                name = "unknown-app";
                break;
        }
        if(path.length() > 0)
            path = path + "/";
        String dirPath = path + prefix;
        String filePath = path + prefix + "/" + name;
        new File(dirPath).mkdirs();
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        fileOutputStream.write(data);
        fileOutputStream.close();
    }

}
