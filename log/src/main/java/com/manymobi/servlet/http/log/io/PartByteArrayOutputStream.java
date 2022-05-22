package com.manymobi.servlet.http.log.io;

import java.io.ByteArrayOutputStream;

/**
 * @author 梁建军
 * 创建日期： 2022/4/30
 * 创建时间： 下午10:59
 * @version 1.0
 * @since 1.0
 * 只保留部分,超过将忽略
 */
public class PartByteArrayOutputStream extends ByteArrayOutputStream {

    private final int maxSize;

    /**
     * 截断
     */
    private boolean truncation = false;

    /**
     * @param size    初始化大小
     * @param maxSize 最大大小
     */
    public PartByteArrayOutputStream(int size, int maxSize) {
        super(size);
        this.maxSize = maxSize;
    }

    @Override
    public void write(int b) {
        if (truncation) {
            return;
        }
        if (size() >= maxSize) {
            truncation = true;
            return;
        }
        super.write(b);
    }


    @Override
    public void write(byte[] b) {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) {
        if (truncation) {
            return;
        }
        if (size() >= maxSize) {
            truncation = true;
            return;
        }
        int min = Math.min(maxSize - size(), len);
        if (min != len) {
            truncation = true;
        }
        super.write(b, off, min);
    }

    /**
     * 允许的最大缓存的数量
     *
     * @return 最大缓存数量
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * 是否后面数据已经被截断
     *
     * @return 是否截断
     */
    public boolean isTruncation() {
        return truncation;
    }
}
