package korgpkg;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by korgeaux on 19.05.2016.
 */
public class LinkChunk extends Chunk {

    public LinkChunk() {
        id = LINK;
    }

    public void load(RandomAccessFile reader, int size) throws IOException {

    }

    public void save(RandomAccessFile writer) throws IOException {

    }

    public void export(String path) throws IOException {

    }

    @Override
    public String toString() {
        return "Link: ???";
    }

}
