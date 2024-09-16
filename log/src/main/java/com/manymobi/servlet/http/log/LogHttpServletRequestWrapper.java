package com.manymobi.servlet.http.log;

import com.manymobi.servlet.http.log.io.PartByteArrayOutputStream;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.function.Consumer;

/**
 * @author 梁建军
 * 创建日期： 2018/10/22
 * 创建时间： 14:14
 * @version 1.0
 * @since 1.0
 */
public class LogHttpServletRequestWrapper extends HttpServletRequestWrapper {


    private ServletInputStream servletInputStream;

    private final LogStrategy logStrategy;
    private final Consumer<String> print;

    public LogHttpServletRequestWrapper(HttpServletRequest request, LogStrategy logStrategy, Consumer<String> print) {
        super(request);
        this.logStrategy = logStrategy;
        this.print = print;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (servletInputStream == null) {
            servletInputStream = new RetainServletInputStream(super.getInputStream(), this);
        }
        return servletInputStream;
    }

    public void print() throws UnsupportedEncodingException {
        if (servletInputStream instanceof RetainServletInputStream) {
            ((RetainServletInputStream) servletInputStream).print(true);
        }
    }


    /**
     * 保留
     */
    private class RetainServletInputStream extends ServletInputStream {

        private final ServletInputStream servletInputStream;

        private final LogHttpServletRequestWrapper logHttpServletRequestWrapper;
        /**
         * 用于记录请求体，打印完日志就释放
         */
        private ByteArrayOutputStream byteArrayOutputStream;

        private final int readContentLength;

        public RetainServletInputStream(ServletInputStream servletInputStream, LogHttpServletRequestWrapper logHttpServletRequestWrapper) {

            this.servletInputStream = servletInputStream;
            this.logHttpServletRequestWrapper = logHttpServletRequestWrapper;
            int contentLength = logHttpServletRequestWrapper.getContentLength();
            //解决当请求内容大小未知的时候报错
            if (contentLength < 0) {
                contentLength = 0;
            }
            int requestBodyMaxSize = logStrategy.getRequestBodyMaxSize();
            int requestBodyInitialSize = logStrategy.getRequestBodyInitialSize();
            if (requestBodyInitialSize <= 0) {
                if (requestBodyMaxSize <= 0) {
                    byteArrayOutputStream = new ByteArrayOutputStream(contentLength);
                    readContentLength = contentLength;
                } else {
                    int min = Math.min(contentLength, requestBodyMaxSize);
                    byteArrayOutputStream = new PartByteArrayOutputStream(min, min);
                    readContentLength = min;
                }
            } else {
                if (contentLength < requestBodyInitialSize) {
                    byteArrayOutputStream = new PartByteArrayOutputStream(contentLength, requestBodyMaxSize);
                    readContentLength = contentLength;
                } else {
                    byteArrayOutputStream = new PartByteArrayOutputStream(requestBodyInitialSize, requestBodyMaxSize);
                    readContentLength = Math.min(requestBodyMaxSize, contentLength);
                }
            }
        }

        @Override
        public boolean isFinished() {
            return servletInputStream.isFinished();
        }

        @Override
        public boolean isReady() {
            return servletInputStream.isReady();
        }

        @Override
        public void setReadListener(ReadListener listener) {
            servletInputStream.setReadListener(listener);
        }

        @Override
        public int read() throws IOException {
            int b = servletInputStream.read();
            if (b != -1 && byteArrayOutputStream != null) {
                byteArrayOutputStream.write(b);
            } else {
                print(true);
            }
            print(false);
            return b;
        }


        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int read = servletInputStream.read(b, off, len);
            if (read != -1 && byteArrayOutputStream != null) {
                byteArrayOutputStream.write(b, off, read);
            } else {
                print(true);
            }
            print(false);
            return read;
        }

        /**
         * @param force 强制
         */
        private void print(boolean force) throws UnsupportedEncodingException {
            if (byteArrayOutputStream != null) {
                if (force || byteArrayOutputStream.size() >= readContentLength) {
                    print.accept(byteArrayOutputStream.toString(logHttpServletRequestWrapper.getCharacterEncoding()));
                    byteArrayOutputStream = null;
                }
            }
        }
    }
}
