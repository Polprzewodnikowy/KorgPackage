package com.polprzewodnikowy.korgpkg;

import java.io.IOException;
import java.io.RandomAccessFile;

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

    int id;

    public void load(RandomAccessFile reader, int size) throws IOException {

    }

    public void save(RandomAccessFile writer) throws IOException {

    }

    public void export(String path) throws IOException {

    }

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
            stringBuilder.append((char)tmp);
        return stringBuilder.toString();
    }

    protected void writeString(RandomAccessFile writer, String string) throws IOException {
        writer.writeBytes(string);
        writer.write(0);
    }

}
