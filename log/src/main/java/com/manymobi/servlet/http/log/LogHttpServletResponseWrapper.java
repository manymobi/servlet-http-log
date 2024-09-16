package com.manymobi.servlet.http.log;

import com.manymobi.servlet.http.log.io.PartByteArrayOutputStream;
import com.manymobi.servlet.http.util.ContentTypeUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.function.Consumer;

/**
 * @author 梁建军
 * 创建日期： 2018/10/22
 * 创建时间： 14:14
 * @version 1.0
 * @since 1.0
 */
public class LogHttpServletResponseWrapper extends HttpServletResponseWrapper {
    public static final String DEFAULT_CHARACTER_ENCODING ="ISO-8859-1";
    /**
     * 用于记录请求体，打印完日志就释放
     */
    private final ByteArrayOutputStream byteArrayOutputStream;
    private ServletOutputStream servletOutputStream;

    private final LogStrategy logStrategy;
    private final Consumer<String> print;

    private boolean printIng = false;

    private PrintWriter writer;


    public LogHttpServletResponseWrapper(HttpServletResponse response, LogStrategy logStrategy,
                                         Consumer<String> print) {
        super(response);
        this.logStrategy = logStrategy;
        this.print = print;
        if (logStrategy.isResponseBody()) {
            if (logStrategy.getResponseBodyMaxSize() < 0) {
                byteArrayOutputStream = new ByteArrayOutputStream(logStrategy.getResponseBodyInitialSize());
            } else {
                byteArrayOutputStream = new PartByteArrayOutputStream(logStrategy.getResponseBodyInitialSize(),
                        logStrategy.getResponseBodyMaxSize());
            }
        } else {
            byteArrayOutputStream = null;
        }

    }

    @Override
    public void setCharacterEncoding(String charset) {
        super.setCharacterEncoding(charset);
    }


    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (servletOutputStream == null) {
            if (logStrategy.isResponseBody()) {
                servletOutputStream = new RetainServletOutputStream(this, super.getOutputStream(), byteArrayOutputStream);
            } else {
                return super.getOutputStream();
            }
        }
        return servletOutputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (this.writer == null) {
            String characterEncoding = getCharacterEncoding();
            this.writer = (characterEncoding != null ? new ResponsePrintWriter(getOutputStream(), characterEncoding) :
                    new ResponsePrintWriter(getOutputStream(), DEFAULT_CHARACTER_ENCODING));
        }
        return this.writer;
    }

    public String getBodyString(String charsetName) throws UnsupportedEncodingException {
        return byteArrayOutputStream.toString(charsetName);
    }

    public void print() throws UnsupportedEncodingException {
        if (printIng) {
            return;
        }
        print.accept(getBodyString(getCharacterEncoding()));
        printIng = true;
    }


    /**
     * 保留
     */
    private class RetainServletOutputStream extends ServletOutputStream {

        private final LogHttpServletResponseWrapper httpServletResponse;

        private final ServletOutputStream servletOutputStream;

        private final ByteArrayOutputStream byteArrayOutputStream;

        private Boolean readBody;

        public RetainServletOutputStream(LogHttpServletResponseWrapper httpServletResponse, ServletOutputStream servletOutputStream, ByteArrayOutputStream byteArrayOutputStream) {
            this.httpServletResponse = httpServletResponse;
            this.servletOutputStream = servletOutputStream;
            this.byteArrayOutputStream = byteArrayOutputStream;
        }


        @Override
        public boolean isReady() {
            return servletOutputStream.isReady();
        }

        @Override
        public void setWriteListener(WriteListener listener) {
            servletOutputStream.setWriteListener(listener);
        }

        @Override
        public void write(int b) throws IOException {
            if (readBody == null) {
                readBody = ContentTypeUtil.isCompatibleWith(logStrategy.getResponseContentType(), httpServletResponse.getContentType());
            }
            if (readBody) {
                byteArrayOutputStream.write(b);
            }
            servletOutputStream.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (readBody == null) {
                readBody = ContentTypeUtil.isCompatibleWith(logStrategy.getResponseContentType(), httpServletResponse.getContentType());
            }
            if (readBody) {
                byteArrayOutputStream.write(b, off, len);
            }
            servletOutputStream.write(b, off, len);
        }

        @Override
        public void write(byte[] b) throws IOException {
            this.write(b, 0, b.length);
        }

        @Override
        public void close() throws IOException {
            servletOutputStream.close();
            super.close();
        }

        @Override
        public void flush() throws IOException {
            servletOutputStream.flush();
            super.flush();
        }
    }

    private static class ResponsePrintWriter extends PrintWriter {

        public ResponsePrintWriter(ServletOutputStream outputStream, String characterEncoding) throws UnsupportedEncodingException {
            super(new OutputStreamWriter(outputStream, characterEncoding));
        }

        @Override
        public void write(char[] buf, int off, int len) {
            super.write(buf, off, len);
            super.flush();
        }

        @Override
        public void write(String s, int off, int len) {
            super.write(s, off, len);
            super.flush();
        }

        @Override
        public void write(int c) {
            super.write(c);
            super.flush();
        }
    }
}
