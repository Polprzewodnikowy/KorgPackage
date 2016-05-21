package com.polprzewodnikowy.korgpkg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Created by korgeaux on 09.05.2016.
 */
public class FileChunk extends Chunk {

    int unknown1;
    short permissions;
    short unknown2;
    boolean isCompressed;
    String name;
    String date;
    String time;
    byte[] data;

    public FileChunk() {
        id = FILE;
    }

    @Override
    public void load(RandomAccessFile reader, int size) throws IOException {
        reader.skipBytes(16);
        unknown1 = Integer.reverseBytes(reader.readInt());
        permissions = Short.reverseBytes(reader.readShort());
        unknown2 = Short.reverseBytes(reader.readShort());
        int dataSize = Integer.reverseBytes(reader.readInt());
        isCompressed = reader.readBoolean();
        name = readString(reader);
        date = readString(reader);
        time = readString(reader);

        data = new byte[dataSize];

        if(!isCompressed) {
            reader.read(data, 0, dataSize);
        } else {
            int index = 0;
            while (true) {
                int blockType = Integer.reverseBytes(reader.readInt());
                if (blockType != 0x00000100)
                    break;
                int compressedBlockSize = Integer.reverseBytes(reader.readInt()) - 4;
                int uncompressedBlockSize = reader.readInt();
                byte[] compressed = new byte[compressedBlockSize];
                reader.read(compressed, 0, compressedBlockSize);

                try {
                    Inflater inflater = new Inflater();
                    inflater.setInput(compressed);
                    byte[] uncompressed = new byte[uncompressedBlockSize];
                    inflater.inflate(uncompressed);
                    System.arraycopy(uncompressed, 0, data, index, uncompressedBlockSize);
                } catch (DataFormatException e) {
                    e.printStackTrace();
                }

                index += uncompressedBlockSize;
                int rem = compressedBlockSize % 4;
                if(rem != 0)
                    reader.skipBytes(4 - rem);
            }
        }
    }

    @Override
    public void save(RandomAccessFile writer) throws IOException {
        byte[] hash = new byte[16];
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(data);
            hash = md5.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        writer.writeInt(Integer.reverseBytes(id));
        long offset = writer.getFilePointer();
        writer.write(new byte[4]);
        writer.write(hash);
        writer.writeInt(Integer.reverseBytes(unknown1));
        writer.writeShort(Short.reverseBytes(permissions));
        writer.writeShort(Short.reverseBytes(unknown2));
        writer.writeInt(Integer.reverseBytes(data.length));
        writer.writeBoolean(isCompressed);
        writeString(writer, name);
        writeString(writer, date);
        writeString(writer, time);
        if(isCompressed) {
            int index = 0;
            int remain = data.length;
            if(data.length > 0)
            {
                do {
                    int blockSize;
                    if(remain > 0x00100000) {
                        blockSize = 0x00100000;
                    } else {
                        blockSize = remain;
                    }
                    byte compressed[] = new byte[0x00100000];
                    Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION, false);
                    deflater.setInput(data, index, blockSize);
                    deflater.finish();
                    int compressedBlockSize = deflater.deflate(compressed);
                    deflater.end();
                    writer.writeInt(Integer.reverseBytes(0x00000100));
                    writer.writeInt(Integer.reverseBytes(compressedBlockSize + 4));
                    writer.writeInt(blockSize);
                    writer.write(compressed, 0, compressedBlockSize);

                    int rem = compressedBlockSize % 4;
                    if(rem != 0)
                        writer.write(new byte[4 - rem]);

                    index += 0x00100000;
                    remain -= 0x00100000;
                } while (index < data.length);
            }
            writer.writeInt(Integer.reverseBytes(0x00000101));
            writer.writeInt(Integer.reverseBytes(0x00000000));
        } else {
            writer.write(data);
        }

        int size = (int)(writer.getFilePointer() - offset - 4);
        writer.seek(offset);
        writer.writeInt(Integer.reverseBytes(size));
    }

    @Override
    public void export(String path) throws IOException {
        if(path.length() > 0)
            path = path + "/";
        String dirPath = path + name.substring(name.indexOf('/') + 1, name.lastIndexOf('/'));
        String filePath = path + name.substring(name.indexOf('/') + 1);
        new File(dirPath).mkdirs();
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        fileOutputStream.write(data);
        fileOutputStream.close();
    }

}
