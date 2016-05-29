package com.polprzewodnikowy.korgpkg;

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

    public final static int ATTR_VFAT_ARCHIVE = 0x1000;
    public final static int ATTR_VFAT_READONLY = 0x2000;
    public final static int ATTR_VFAT_SYSTEM = 0x4000;
    public final static int ATTR_VFAT_HIDDEN = 0x8000;

    public final static int ATTR_EXT3_DONT_CHANGE = 0xFFFF;

    File tmpFile;

    short group;
    short owner;
    short attributes;
    short order;
    byte compressionType;
    String name;
    String date;
    String time;

    public FileChunk() {
        id = FILE;
        group = 0;
        owner = 0;
        attributes = ATTR_VFAT_ARCHIVE | ATTR_VFAT_READONLY | ATTR_VFAT_SYSTEM;
        order = -1;
        compressionType = COMPRESSION_RAW;
        name = "";
        date = "";
        time = "";
        try {
            tmpFile = Files.createTempFile("", ".FileChunk").toFile();
            tmpFile.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public short getGroup() {
        return group;
    }

    public void setGroup(short group) {
        this.group = group;
    }

    public short getOwner() {
        return owner;
    }

    public void setOwner(short owner) {
        this.owner = owner;
    }

    public int getAttributes() {
        return attributes;
    }

    public void setAttributes(int attributes) {
        this.attributes = (short) attributes;
    }

    public short getOrder() {
        return order;
    }

    public void setOrder(short order) {
        this.order = order;
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
            e.printStackTrace();
        }
        return tmpDate;
    }

    public void setDateTime(Date date) {
        this.date = String.format("%tm/%<td/%<ty", date);
        this.time = String.format("%tH:%<tM", date);
    }

    public void setData(byte[] data) {
        try {
            if(tmpFile.exists())
                tmpFile.delete();
            FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);
            fileOutputStream.write(data);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getData() {
        byte[] tmpData = new byte[0];
        try {
            FileInputStream fileInputStream = new FileInputStream(tmpFile);
            tmpData = new byte[fileInputStream.available()];
            fileInputStream.read(tmpData, 0, fileInputStream.available());
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return tmpData;
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("[" + id + " FileChunk]: ");
        str.append(name + " | " + date + " " + time + " | ");
        str.append(group + ":" + owner + " | [");
        if ((attributes & ATTR_VFAT_ARCHIVE) == ATTR_VFAT_ARCHIVE)
            str.append("A");
        if ((attributes & ATTR_VFAT_READONLY) == ATTR_VFAT_READONLY)
            str.append("R");
        if ((attributes & ATTR_VFAT_SYSTEM) == ATTR_VFAT_SYSTEM)
            str.append("S");
        if ((attributes & ATTR_VFAT_HIDDEN) == ATTR_VFAT_HIDDEN)
            str.append("H");
        str.append("] | ");
        if (compressionType == COMPRESSION_RAW)
            str.append("RAW");
        else if (compressionType == COMPRESSION_ZLIB)
            str.append("ZLIB");
        str.append(" | " + order);
        return str.toString();
    }

    @Override
    public void load(RandomAccessFile reader, int size) throws IOException {
        if(tmpFile.exists())
            tmpFile.delete();
        FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);

        reader.skipBytes(16);
        group = Short.reverseBytes(reader.readShort());
        owner = Short.reverseBytes(reader.readShort());
        attributes = Short.reverseBytes(reader.readShort());
        order = Short.reverseBytes(reader.readShort());
        int dataSize = Integer.reverseBytes(reader.readInt());
        compressionType = reader.readByte();
        name = readString(reader);
        date = readString(reader);
        time = readString(reader);

        if (compressionType == COMPRESSION_RAW) {
            byte[] tmpData = new byte[dataSize];
            reader.read(tmpData, 0, dataSize);
            fileOutputStream.write(tmpData);
        } else if (compressionType == COMPRESSION_ZLIB) {
            Inflater inflater = new Inflater();
            while (true) {
                int blockType = Integer.reverseBytes(reader.readInt());
                if (blockType != 0x00000100)
                    break;
                int compressedBlockSize = Integer.reverseBytes(reader.readInt()) - 4;
                int uncompressedBlockSize = reader.readInt();
                byte[] compressed = new byte[compressedBlockSize];
                reader.read(compressed, 0, compressedBlockSize);
                try {
                    inflater.reset();
                    inflater.setInput(compressed);
                    byte[] uncompressed = new byte[uncompressedBlockSize];
                    inflater.inflate(uncompressed);
                    fileOutputStream.write(uncompressed);
                } catch (DataFormatException e) {
                    e.printStackTrace();
                }

                int rem = compressedBlockSize % 4;
                if (rem != 0)
                    reader.skipBytes(4 - rem);
            }
        }

        fileOutputStream.close();
    }

    @Override
    public void save(RandomAccessFile writer) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(tmpFile);

        byte[] hash = new byte[16];
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] tmpData = new byte[0x100000];
            while (fileInputStream.available() > 0) {
                int bytes = fileInputStream.read(tmpData, 0, 0x100000);
                md5.update(tmpData, 0, bytes);
            }
            hash = md5.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            fileInputStream.close();
            fileInputStream = new FileInputStream(tmpFile);
        }

        writer.writeInt(Integer.reverseBytes(id));
        long offset = writer.getFilePointer();
        writer.write(new byte[4]);
        writer.write(hash);
        writer.writeShort(Short.reverseBytes(group));
        writer.writeShort(Short.reverseBytes(owner));
        writer.writeShort(Short.reverseBytes(attributes));
        writer.writeShort(Short.reverseBytes(order));
        writer.writeInt(Integer.reverseBytes(fileInputStream.available()));
        writer.writeByte(compressionType);
        writeString(writer, name);
        writeString(writer, date);
        writeString(writer, time);

        if (compressionType == COMPRESSION_RAW) {
            byte[] tmpData = new byte[0x100000];
            while (fileInputStream.available() > 0) {
                int bytes = fileInputStream.read(tmpData, 0, 0x100000);
                writer.write(tmpData, 0, bytes);
            }
        } else if (compressionType == COMPRESSION_ZLIB) {
            Deflater deflater = new Deflater();
            int remain = fileInputStream.available();
            if (fileInputStream.available() > 0) {
                do {
                    int blockSize;
                    if (remain > 0x00100000) {
                        blockSize = 0x00100000;
                    } else {
                        blockSize = remain;
                    }
                    byte tmpData[] = new byte[blockSize];
                    fileInputStream.read(tmpData, 0, blockSize);
                    byte compressed[] = new byte[0x00100000];
                    deflater.reset();
                    deflater.setInput(tmpData, 0, blockSize);
                    deflater.finish();
                    int compressedBlockSize = deflater.deflate(compressed);
                    deflater.end();
                    writer.writeInt(Integer.reverseBytes(0x00000100));
                    writer.writeInt(Integer.reverseBytes(compressedBlockSize + 4));
                    writer.writeInt(blockSize);
                    writer.write(compressed, 0, compressedBlockSize);

                    int rem = compressedBlockSize % 4;
                    if (rem != 0)
                        writer.write(new byte[4 - rem]);

                    remain -= 0x00100000;
                } while (fileInputStream.available() > 0);
            }
            writer.writeInt(Integer.reverseBytes(0x00000101));
            writer.writeInt(Integer.reverseBytes(0x00000000));
        }

        int size = (int) (writer.getFilePointer() - offset - 4);
        writer.seek(offset);
        writer.writeInt(Integer.reverseBytes(size));
    }

    @Override
    public void export(String path) throws IOException {
        String tmpName = name.charAt(0) == '/' ? name.substring(1) : name;
        Path tmpPath = Paths.get(path, tmpName);
        tmpPath.getParent().toFile().mkdirs();
        Files.copy(tmpFile.toPath(), tmpPath, REPLACE_EXISTING);
    }

}
