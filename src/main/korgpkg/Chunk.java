package korgpkg;

import java.io.*;

/**
 * Created by korgeaux on 08.05.2016.
 */
public abstract class Chunk {

    public static final int HEADER = 1;
    public static final int UPDATE_KERNEL = 2;
    public static final int UPDATE_RAMDISK = 3;
    public static final int UPDATE_INSTALLER_APP = 4;
    public static final int UPDATE_INSTALLER_APP_CONFIG = 5;
    public static final int SERVICE_KERNEL = 6;
    public static final int SERVICE_RAMDISK = 7;
    public static final int SERVICE_APP = 8;
    public static final int SERVICE_APP_CONFIG = 9;
    public static final int UPDATE_LAUNCHER_APP = 10;
    public static final int UPDATE_LAUNCHER_APP_CONFIG = 11;
    public static final int MLO = 12;
    public static final int UBOOT = 13;
    public static final int USER_KERNEL = 14;
    public static final int INSTALLER_SCRIPT = 15;
    public static final int DIRECTORY = 16;
    public static final int FILE = 17;
    public static final int LINK = 18;
    public static final int ROOT_FS = 19;

    public final static int ATTR_VFAT_ARCHIVE = 0x1000;
    public final static int ATTR_VFAT_READONLY = 0x2000;
    public final static int ATTR_VFAT_SYSTEM = 0x4000;
    public final static int ATTR_VFAT_HIDDEN = 0x8000;

    public final static int ATTR_EXT3_OWNER_R = (1 << 6);
    public final static int ATTR_EXT3_OWNER_W = (2 << 6);
    public final static int ATTR_EXT3_OWNER_X = (4 << 6);
    public final static int ATTR_EXT3_GROUP_R = (1 << 3);
    public final static int ATTR_EXT3_GROUP_W = (2 << 3);
    public final static int ATTR_EXT3_GROUP_X = (4 << 3);
    public final static int ATTR_EXT3_OTHER_R = (1 << 0);
    public final static int ATTR_EXT3_OTHER_W = (2 << 0);
    public final static int ATTR_EXT3_OTHER_X = (4 << 0);

    public final static int ATTR_EXT3_DONT_CHANGE = 0xFFFF;

    protected int id;

    public abstract void load(RandomAccessFile reader, int size) throws IOException;

    public abstract void save(RandomAccessFile writer) throws IOException;

    public abstract void export(String path) throws IOException;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    protected String readString(RandomAccessFile reader) throws IOException {
        byte tmp;
        StringBuilder stringBuilder = new StringBuilder();
        while ((tmp = reader.readByte()) != 0)
            stringBuilder.append((char) tmp);
        return stringBuilder.toString();
    }

    protected void writeString(RandomAccessFile writer, String string) throws IOException {
        writer.writeBytes(string);
        writer.write(0);
    }

    protected byte[] readData(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fileInputStream.read(data, 0, (int) file.length());
        fileInputStream.close();
        return data;
    }

    protected void writeData(File file, byte[] data) throws IOException {
        if (file.exists())
            file.delete();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(data);
        fileOutputStream.close();
    }

}
