package korgpackage;

import java.io.*;
import java.util.zip.DataFormatException;
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
        reader.skipBytes(16); //MD5
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
                if((compressedBlockSize % 4) != 0)
                    reader.skipBytes(4 - (compressedBlockSize % 4));
            }
        }
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
