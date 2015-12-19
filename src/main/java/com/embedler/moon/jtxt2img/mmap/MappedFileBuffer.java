/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Oembedler Inc. and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 *  documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 *  persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.embedler.moon.jtxt2img.mmap;

import com.embedler.moon.jtxt2img.JTxt2ImgIoRuntimeException;
import org.apache.commons.lang3.Validate;

import java.awt.image.DataBuffer;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.*;
import java.nio.channels.FileChannel;

/**
 * A {@code DataBuffer} implementation that is backed by a memory mapped file.
 * Memory will be allocated outside the normal JVM heap, allowing more efficient
 * memory usage for large buffers
 */
public abstract class MappedFileBuffer extends DataBuffer {
    private final Buffer buffer;
    private final File tempFile;
    private final MappedByteBuffer byteBuffer;

    private MappedFileBuffer(final int type, final int size, final int numBanks) {
        super(type, size, numBanks);

        Validate.isTrue(size >= 0, "Integer overflow for size: %d", size);
        Validate.isTrue(numBanks >= 0, "Number of banks must be positive", numBanks);

        int componentSize = DataBuffer.getDataTypeSize(type) / 8;

        try {
            tempFile = File.createTempFile(String.format("%s-", getClass().getSimpleName().toLowerCase()), ".tmp");
            try (RandomAccessFile raf = new RandomAccessFile(tempFile, "rw");
                 FileChannel channel = raf.getChannel()) {

                long length = ((long) size) * componentSize * numBanks;
                raf.setLength(length);

                byteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, length);

                switch (type) {
                    case DataBuffer.TYPE_BYTE:
                        buffer = byteBuffer;
                        break;
                    case DataBuffer.TYPE_USHORT:
                        buffer = byteBuffer.asShortBuffer();
                        break;
                    case DataBuffer.TYPE_INT:
                        buffer = byteBuffer.asIntBuffer();
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported data type: " + type);
                }
            } finally {
                if (!tempFile.delete()) {
                    tempFile.deleteOnExit();
                }
            }
        } catch (Exception e) {
            throw new JTxt2ImgIoRuntimeException(e);
        }
    }

    public File getTempFile() {
        return tempFile;
    }

    @Override
    public String toString() {
        return String.format("MappedFileBuffer: %s", buffer);
    }

    public static DataBuffer create(final int type, final int size, final int numBanks) {
        switch (type) {
            case DataBuffer.TYPE_BYTE:
                return new DataBufferByte(size, numBanks);
            case DataBuffer.TYPE_USHORT:
                return new DataBufferUShort(size, numBanks);
            case DataBuffer.TYPE_INT:
                return new DataBufferInt(size, numBanks);
            default:
                throw new JTxt2ImgIoRuntimeException("Unsupported data type: " + type);
        }
    }

    final static class DataBufferByte extends MappedFileBuffer {
        private final ByteBuffer buffer;

        public DataBufferByte(int size, int numBanks) {
            super(DataBuffer.TYPE_BYTE, size, numBanks);
            buffer = (ByteBuffer) super.buffer;
        }

        @Override
        public int getElem(int bank, int i) {
            return buffer.get(bank * size + i) & 0xff;
        }

        @Override
        public void setElem(int bank, int i, int val) {
            buffer.put(bank * size + i, (byte) val);
        }
    }

    final static class DataBufferUShort extends MappedFileBuffer {
        private final ShortBuffer buffer;

        public DataBufferUShort(int size, int numBanks) {
            super(DataBuffer.TYPE_USHORT, size, numBanks);
            buffer = (ShortBuffer) super.buffer;
        }

        @Override
        public int getElem(int bank, int i) {
            return buffer.get(bank * size + i) & 0xffff;
        }

        @Override
        public void setElem(int bank, int i, int val) {
            buffer.put(bank * size + i, (short) val);
        }
    }

    final static class DataBufferInt extends MappedFileBuffer {
        private final IntBuffer buffer;

        public DataBufferInt(int size, int numBanks) {
            super(DataBuffer.TYPE_INT, size, numBanks);
            buffer = (IntBuffer) super.buffer;
        }

        @Override
        public int getElem(int bank, int i) {
            return buffer.get(bank * size + i);
        }

        @Override
        public void setElem(int bank, int i, int val) {
            buffer.put(bank * size + i, val);
        }
    }
}
