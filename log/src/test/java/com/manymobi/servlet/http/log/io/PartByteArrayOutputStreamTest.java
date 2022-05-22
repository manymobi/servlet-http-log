package com.manymobi.servlet.http.log.io;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 梁建军
 * 创建日期： 2022/4/30
 * 创建时间： 下午11:20
 * @version 1.0
 * @since 1.0
 */
class PartByteArrayOutputStreamTest {

    @Test
    void write() {

        PartByteArrayOutputStream partByteArrayOutputStream = new PartByteArrayOutputStream(1024, 2048);

        for (int i = 0; i < 1024; i++) {
            partByteArrayOutputStream.write(1);
        }
        assertEquals(partByteArrayOutputStream.size(), 1024);
        for (int i = 0; i < 512; i++) {
            partByteArrayOutputStream.write(1);
        }
        assertEquals(partByteArrayOutputStream.size(), 1024 + 512);
        for (int i = 0; i < 1024; i++) {
            partByteArrayOutputStream.write(1);
        }
        assertEquals(partByteArrayOutputStream.size(), 2048);
        for (int i = 0; i < 1024; i++) {
            partByteArrayOutputStream.write(1);
        }
        assertEquals(partByteArrayOutputStream.size(), 2048);

    }

    @Test
    void testWrite() {
        PartByteArrayOutputStream partByteArrayOutputStream = new PartByteArrayOutputStream(1024, 2048);
        partByteArrayOutputStream.write(new byte[1024]);
        assertEquals(partByteArrayOutputStream.size(), 1024);
        partByteArrayOutputStream.write(new byte[512]);
        assertEquals(partByteArrayOutputStream.size(), 1024 + 512);
        partByteArrayOutputStream.write(new byte[1024]);
        assertEquals(partByteArrayOutputStream.size(), 2048);
        partByteArrayOutputStream.write(new byte[1024]);
        assertEquals(partByteArrayOutputStream.size(), 2048);
    }

    @Test
    void testWrite1() {
        PartByteArrayOutputStream partByteArrayOutputStream = new PartByteArrayOutputStream(1024, 2048);
        partByteArrayOutputStream.write(new byte[2048], 0, 1024);
        assertEquals(partByteArrayOutputStream.size(), 1024);
        partByteArrayOutputStream.write(new byte[512], 0, 512);
        assertEquals(partByteArrayOutputStream.size(), 1024 + 512);
        partByteArrayOutputStream.write(new byte[1024], 0, 1024);
        assertEquals(partByteArrayOutputStream.size(), 2048);
        partByteArrayOutputStream.write(new byte[1024], 0, 1024);
        assertEquals(partByteArrayOutputStream.size(), 2048);
    }
}