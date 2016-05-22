package com.polprzewodnikowy.korgpkg;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
        chunks.sort((c1, c2) -> Integer.compare(c1.getId(), c2.getId()));

        RandomAccessFile writer = null;

        try {
            if(file.exists())
                file.delete();

            writer = new RandomAccessFile(file, "rw");

            writer.seek(16);

            int rem;

            for (Chunk chunk: chunks) {
                chunk.save(writer);
                writer.seek(writer.length());
                rem = (int)(writer.getFilePointer() % 4);
                if(rem != 0)
                    writer.write(new byte[4 - rem]);
            }

            rem = (int)(writer.getFilePointer() % 4);
            if(rem != 0)
                writer.write(new byte[4 - rem]);

            writer.seek(16);

            byte[] hash = new byte[16];
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                byte[] tmp = new byte[0xFFFFFF];
                while (writer.getFilePointer() < writer.length()) {
                    int bytes = writer.read(tmp);
                    md5.update(tmp, 0, bytes);
                }
                hash = md5.digest();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            writer.seek(0);
            writer.write(hash);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null)
                    writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
