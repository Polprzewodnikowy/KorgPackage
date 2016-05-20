package korgpackage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by korgeaux on 19.05.2016.
 */
public class AppConfigChunk extends Chunk {

    byte[] data;

    public AppConfigChunk(int id) {
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
            case UPDATE_INSTALLER_APP_CONFIG:
                prefix = "update";
                name = "lfo-pkg-install.xml";
                break;
            case SERVICE_APP_CONFIG:
                prefix = "service";
                name = "lfo-service.xml";
                break;
            case UPDATE_LAUNCHER_APP_CONFIG:
                prefix = "service";
                name = "lfo-pkg-launcher.xml";
                break;
            default:
                prefix = "";
                name = "unknown-app.xml";
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
