package com.polprzewodnikowy.korgpkg;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by korgeaux on 08.05.2016.
 */
public class PackageReader {

    File file;

    public PackageReader(String path) {
        file = new File(path);
    }

    public PackageReader(File file) {
        this.file = file;
    }

    public List<Chunk> load() {
        List<Chunk> chunks = new ArrayList<>();
        RandomAccessFile reader = null;

        try {
            reader = new RandomAccessFile(file, "r");
            byte[] hash = new byte[16];
            reader.read(hash, 0, 16);
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                byte[] tmp = new byte[0xFFFFFF];
                while (reader.getFilePointer() < reader.length()) {
                    int bytes = reader.read(tmp);
                    md5.update(tmp, 0, bytes);
                }
                if (!Arrays.equals(hash, md5.digest())) {
                    System.out.println("Invalid hash");
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            reader.seek(16);

            while (reader.getFilePointer() < reader.length())
            {
                int id = Integer.reverseBytes(reader.readInt());
                int size = Integer.reverseBytes(reader.readInt());
                long pos = reader.getFilePointer();
                boolean valid = true;

                switch (id) {
                    case Chunk.HEADER:
                        chunks.add(new HeaderChunk());
                        break;
                    case Chunk.UPDATE_KERNEL:
                        chunks.add(new KernelChunk(Chunk.UPDATE_KERNEL));
                        break;
                    case Chunk.UPDATE_RAMDISK:
                        chunks.add(new RamdiskChunk(Chunk.UPDATE_RAMDISK));
                        break;
                    case Chunk.UPDATE_INSTALLER_APP:
                        chunks.add(new AppChunk(Chunk.UPDATE_INSTALLER_APP));
                        break;
                    case Chunk.UPDATE_INSTALLER_APP_CONFIG:
                        chunks.add(new AppConfigChunk(Chunk.UPDATE_INSTALLER_APP_CONFIG));
                        break;
                    case Chunk.SERVICE_KERNEL:
                        chunks.add(new KernelChunk(Chunk.SERVICE_KERNEL));
                        break;
                    case Chunk.SERVICE_RAMDISK:
                        chunks.add(new RamdiskChunk(Chunk.SERVICE_RAMDISK));
                        break;
                    case Chunk.SERVICE_APP:
                        chunks.add(new AppChunk(Chunk.SERVICE_APP));
                        break;
                    case Chunk.SERVICE_APP_CONFIG:
                        chunks.add(new AppConfigChunk(Chunk.SERVICE_APP_CONFIG));
                        break;
                    case Chunk.UPDATE_LAUNCHER_APP:
                        chunks.add(new AppChunk(Chunk.UPDATE_LAUNCHER_APP));
                        break;
                    case Chunk.UPDATE_LAUNCHER_APP_CONFIG:
                        chunks.add(new AppConfigChunk(Chunk.UPDATE_LAUNCHER_APP_CONFIG));
                        break;
                    case Chunk.MLO:
                        chunks.add(new MLOChunk());
                        break;
                    case Chunk.UBOOT:
                        chunks.add(new UBootChunk());
                        break;
                    case Chunk.USER_KERNEL:
                        chunks.add(new KernelChunk(Chunk.USER_KERNEL));
                        break;
                    case Chunk.INSTALLER_SCRIPT:
                        chunks.add(new InstallerScriptChunk());
                        break;
                    case Chunk.DIRECTORY:
                        chunks.add(new DirectoryChunk());
                        break;
                    case Chunk.FILE:
                        chunks.add(new FileChunk());
                        break;
                    case Chunk.LINK:
                        chunks.add(new LinkChunk());
                        break;
                    case Chunk.ROOT_FS:
                        chunks.add(new RootFSChunk());
                        break;
                    default:
                        valid = false;
                }

                if (valid) {
                    Chunk lastChunk = chunks.get(chunks.size() - 1);
                    lastChunk.load(reader, size);
                }

                long offset = pos + size;
                long rem = offset % 4;
                if(rem != 0)
                    offset += 4 - rem;
                reader.seek(offset);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return chunks;
    }

}
