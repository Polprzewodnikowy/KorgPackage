package korgpkg;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by korgeaux on 09.05.2016.
 */
public class FileChunk extends Chunk {

    public final static byte COMPRESSION_RAW = 0;
    public final static byte COMPRESSION_ZLIB = 1;
    public final static byte ENCRYPTED = 16;

    private File file;

    private short owner;
    private short group;
    private short attributes;
    private short condition;
    private byte compressionType;
    private String name;
    private String date;
    private String time;
    private int size;

    public FileChunk(int size) {
        id = FILE;
        this.size = size;
        owner = 0;
        group = 0;
        attributes = ATTR_VFAT_ARCHIVE | ATTR_VFAT_READONLY | ATTR_VFAT_SYSTEM;
        condition = -1;
        compressionType = COMPRESSION_ZLIB;
        name = "/omega_sys/file";
        date = "01/01/2016";
        time = "12:00";
        try {
            file = Files.createTempFile("", ".FileChunk").toFile();
            file.deleteOnExit();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public short getOwner() {
        return owner;
    }

    public void setOwner(short owner) {
        this.owner = owner;
    }

    public short getGroup() {
        return group;
    }

    public void setGroup(short group) {
        this.group = group;
    }

    public int getAttributes() {
        return attributes;
    }

    public void setAttributes(int attributes) {
        this.attributes = (short) attributes;
    }

    public short getCondition() {
        return condition;
    }

    public void setCondition(short condition) {
        this.condition = condition;
    }

    public byte getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(byte compressionType) {
        this.compressionType = compressionType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        Date tmpDate = new Date();
        try {
            tmpDate = simpleDateFormat.parse(date + " " + time);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
        return tmpDate;
    }

    public void setDateTime(Date date) {
        this.date = String.format("%tm/%<td/%<tY", date);
        this.time = String.format("%tH:%<tM", date);
    }

    public void setData(byte[] data) {
        try {
            writeData(file, data);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public byte[] getData() {
        byte[] tmpData = new byte[0];
        try {
            tmpData = readData(file);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            return tmpData;
        }
    }

    @Override
    public String toString() {
        return "File: " + name;
    }

    public void load(RandomAccessFile reader, int size) throws IOException {
        if (file.exists())
            file.delete();
        FileOutputStream fileOutputStream = new FileOutputStream(file);

        reader.skipBytes(16);
        owner = Short.reverseBytes(reader.readShort());
        group = Short.reverseBytes(reader.readShort());
        attributes = Short.reverseBytes(reader.readShort());
        condition = Short.reverseBytes(reader.readShort());
        int dataSize = Integer.reverseBytes(reader.readInt());
        compressionType = reader.readByte();
        name = readString(reader);
        date = readString(reader);
        time = readString(reader);

        if (compressionType == COMPRESSION_RAW) {
            byte[] tmpData = new byte[dataSize];
            reader.read(tmpData, 0, dataSize);
            fileOutputStream.write(tmpData);
        } else if (compressionType == ENCRYPTED) {
            int tmpSize = size - 29 - name.length() - date.length() - time.length() - 3;
            byte[] tmpData = new byte[tmpSize];
            reader.read(tmpData, 0, tmpSize);
            fileOutputStream.write(tmpData);
        } else if (compressionType == COMPRESSION_ZLIB) {
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
                    fileOutputStream.write(uncompressed);
                } catch (DataFormatException e) {
                    System.err.println(e.getMessage());
                }

                int rem = compressedBlockSize % 4;
                if (rem != 0)
                    reader.skipBytes(4 - rem);
            }
        }

        fileOutputStream.close();
    }

    public void save(RandomAccessFile writer) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);

        byte[] hash = new byte[16];
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] data = new byte[0x100000];
            while (fileInputStream.available() > 0) {
                int bytes = fileInputStream.read(data, 0, 0x100000);
                md5.update(data, 0, bytes);
            }
            hash = md5.digest();
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        } finally {
            fileInputStream.close();
        }

        fileInputStream = new FileInputStream(file);

        writer.writeInt(Integer.reverseBytes(id));
        long offset = writer.getFilePointer();
        writer.write(new byte[4]);
        writer.write(hash);
        writer.writeShort(Short.reverseBytes(owner));
        writer.writeShort(Short.reverseBytes(group));
        writer.writeShort(Short.reverseBytes(attributes));
        writer.writeShort(Short.reverseBytes(condition));
        writer.writeInt(Integer.reverseBytes((int) file.length()));
        writer.writeByte(compressionType);
        writeString(writer, name);
        writeString(writer, date);
        writeString(writer, time);

        if (compressionType == COMPRESSION_RAW || compressionType == ENCRYPTED) {
            byte[] data = new byte[0x100000];
            while (fileInputStream.available() > 0) {
                int bytes = fileInputStream.read(data, 0, 0x100000);
                writer.write(data, 0, bytes);
            }
        } else if (compressionType == COMPRESSION_ZLIB) {
            int remain = (int) file.length();
            if (remain > 0) {
                do {
                    int blockSize;
                    if (remain > 0x00100000) {
                        blockSize = 0x00100000;
                    } else {
                        blockSize = remain;
                    }
                    byte data[] = new byte[blockSize];
                    fileInputStream.read(data, 0, blockSize);
                    byte compressed[] = new byte[0x00100000];
                    Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
                    deflater.setInput(data, 0, blockSize);
                    deflater.finish();
                    int compressedBlockSize = deflater.deflate(compressed);
                    writer.writeInt(Integer.reverseBytes(0x00000100));
                    writer.writeInt(Integer.reverseBytes(compressedBlockSize + 4));
                    writer.writeInt(blockSize);
                    writer.write(compressed, 0, compressedBlockSize);

                    int rem = compressedBlockSize % 4;
                    if (rem != 0)
                        writer.write(new byte[4 - rem]);

                    remain -= 0x00100000;
                } while (remain > 0);
            }
            writer.writeInt(Integer.reverseBytes(0x00000101));
            writer.writeInt(Integer.reverseBytes(0x00000000));
        }

        int size = (int) (writer.getFilePointer() - offset - 4);
        writer.seek(offset);
        writer.writeInt(Integer.reverseBytes(size));
    }

    public void export(String path) throws IOException {
        String tmpName = name.charAt(0) == '/' ? name.substring(1) : name;
        Path tmpPath = Paths.get(path, tmpName);
        tmpPath.getParent().toFile().mkdirs();
        Files.copy(file.toPath(), tmpPath, REPLACE_EXISTING);
    }

}
