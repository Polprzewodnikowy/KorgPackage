package korgpackage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * Created by korgeaux on 08.05.2016.
 */
public class PackageWriter {

    File file;

    public PackageWriter(String path) {
        file = new File(path);
    }

    public PackageWriter(File file) {
        this.file = file;
    }

    public void save(List<Chunk> chunks) {
        try {
            RandomAccessFile writer = new RandomAccessFile(file, "w");

            //TODO: save package to file

        } catch (IOException e) {

        }

    }

}
