package korgpkg;

import javax.xml.bind.DatatypeConverter;
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

    private File file;
    private List<Chunk> chunks;

    public PackageReader(String path) {
        file = new File(path);
        chunks = new ArrayList<>();
    }

    public PackageReader(File file) {
        this.file = file;
        chunks = new ArrayList<>();
    }

    public List<Chunk> getChunks() {
        return chunks;
    }

    public List<Chunk> load() {
        chunks = new ArrayList<>();
        RandomAccessFile reader = null;
        System.out.println("Processing pkg: " + file.getName());
        System.out.println("Calculating hash...");

        try {
            reader = new RandomAccessFile(file, "r");
            byte[] calcHash = new byte[16];
            byte[] pkgHash = new byte[16];
            reader.read(pkgHash, 0, 16);
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                byte[] tmp = new byte[0xFFFFFF];
                while (reader.getFilePointer() < reader.length()) {
                    int bytes = reader.read(tmp);
                    md5.update(tmp, 0, bytes);
                }
                calcHash = md5.digest();
                if (!Arrays.equals(pkgHash, calcHash)) {
                    System.out.println("Invalid hash! Package may be corrupted.");
                }
            } catch (NoSuchAlgorithmException e) {
                System.err.println(e.getMessage());
            }

            System.out.println("Calculated hash: 0x" + DatatypeConverter.printHexBinary(calcHash));
            System.out.println("Expected hash:   0x" + DatatypeConverter.printHexBinary(pkgHash));

            reader.seek(16);

            while (reader.getFilePointer() < reader.length()) {
                int id = Integer.reverseBytes(reader.readInt());
                int size = Integer.reverseBytes(reader.readInt());
                long pos = reader.getFilePointer();

                switch (id) {
                    case Chunk.HEADER:
                        chunks.add(new HeaderChunk());
                        break;
                    case Chunk.UPDATE_KERNEL:
                    case Chunk.UPDATE_RAMDISK:
                    case Chunk.UPDATE_INSTALLER_APP:
                    case Chunk.UPDATE_INSTALLER_APP_CONFIG:
                    case Chunk.SERVICE_KERNEL:
                    case Chunk.SERVICE_RAMDISK:
                    case Chunk.SERVICE_APP:
                    case Chunk.SERVICE_APP_CONFIG:
                    case Chunk.UPDATE_LAUNCHER_APP:
                    case Chunk.UPDATE_LAUNCHER_APP_CONFIG:
                    case Chunk.MLO:
                    case Chunk.UBOOT:
                    case Chunk.USER_KERNEL:
                        chunks.add(new SystemFileChunk(id));
                        break;
                    case Chunk.INSTALLER_SCRIPT:
                        chunks.add(new InstallerScriptChunk());
                        break;
                    case Chunk.DIRECTORY:
                        chunks.add(new DirectoryChunk());
                        break;
                    case Chunk.FILE:
                        chunks.add(new FileChunk(size));
                        break;
                    case Chunk.LINK:
                        chunks.add(new LinkChunk());
                        break;
                    case Chunk.ROOT_FS:
                        chunks.add(new RootFSChunk());
                        break;
                    default:
                        chunks.add(new UnknownChunk(id, size, pos));
                }

                Chunk lastChunk = chunks.get(chunks.size() - 1);
                lastChunk.load(reader, size);
                System.out.println(lastChunk);

                long offset = pos + size;
                long rem = offset % 4;
                if (rem != 0)
                    offset += 4 - rem;
                reader.seek(offset);
            }

            System.out.println("Done! Processed " + chunks.size() + " chunks");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

        return chunks;
    }

}
