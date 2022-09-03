package com.kyyee.framework.common.utils;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

public class FastStringWriter extends Writer {
    private char[] buf;
    private int count;

    public FastStringWriter() {
        this(64);
    }

    public FastStringWriter(int initialSize) {
        if (initialSize < 0) {
            throw new IllegalArgumentException("Negative initial size: " + initialSize);
        } else {
            this.buf = new char[initialSize];
        }
    }

    public void write(int c) {
        int newCount = this.count + 1;
        if (newCount > this.buf.length) {
            this.buf = Arrays.copyOf(this.buf, Math.max(this.buf.length << 1, newCount));
        }

        this.buf[this.count] = (char) c;
        this.count = newCount;
    }

    public void write(char[] c, int off, int len) {
        if (off >= 0 && off <= c.length && len >= 0 && off + len <= c.length && off + len >= 0) {
            if (len != 0) {
                int newCount = this.count + len;
                if (newCount > this.buf.length) {
                    this.buf = Arrays.copyOf(this.buf, Math.max(this.buf.length << 1, newCount));
                }

                System.arraycopy(c, off, this.buf, this.count, len);
                this.count = newCount;
            }
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public void write(String str, int off, int len) {
        int newCount = this.count + len;
        if (newCount > this.buf.length) {
            this.buf = Arrays.copyOf(this.buf, Math.max(this.buf.length << 1, newCount));
        }

        str.getChars(off, off + len, this.buf, this.count);
        this.count = newCount;
    }

    public void writeTo(Writer out) throws IOException {
        out.write(this.buf, 0, this.count);
    }

    public FastStringWriter append(CharSequence csq) {
        String s = csq == null ? "null" : csq.toString();
        this.write(s, 0, s.length());
        return this;
    }

    public FastStringWriter append(CharSequence csq, int start, int end) {
        String s = (csq == null ? "null" : csq).subSequence(start, end).toString();
        this.write(s, 0, s.length());
        return this;
    }

    public FastStringWriter append(char c) {
        this.write(c);
        return this;
    }

    public void reset() {
        this.count = 0;
    }

    public char[] toCharArray() {
        return Arrays.copyOf(this.buf, this.count);
    }

    public int size() {
        return this.count;
    }

    public String toString() {
        return new String(this.buf, 0, this.count);
    }

    public void flush() {
    }

    public void close() {
        this.buf = null;
    }
}
