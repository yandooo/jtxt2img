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
package com.embedler.moon.jtxt2img;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import sun.nio.ch.FileChannelImpl;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

public enum CoreHelper {
    ;
    public static final String DEFAULT_CHARSET_NAME = "UTF-8";
    public static final Charset DEFAULT_CHARSET = Charset.forName(DEFAULT_CHARSET_NAME);
    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    public static final TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone("GMT");

    public static final String SIZE_REGEXP_STR = "(\\d{1,5})(x)?(\\d{1,5})?(\\.(png|jpg|jpeg|gif))?";
    public static final Pattern SIZE_REGEXP = Pattern.compile(SIZE_REGEXP_STR);
    public static final String COLOR_REGEXP_STR = "([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})(\\.(png|jpg|jpeg|gif))?";
    public static final Pattern COLOR_REGEXP = Pattern.compile(COLOR_REGEXP_STR);

    public static final String DEF_PLACEHOLDER_FORMAT = "png";
    public static final String DEF_PLACEHOLDER_BGCOLOR = "ffffff";
    public static final String DEF_PLACEHOLDER_FGCOLOR = "000000";
    public static final Color DEF_PLACEHOLDER_BGCOLOR_PARSED = hex2Rgb(DEF_PLACEHOLDER_BGCOLOR);
    public static final Color DEF_PLACEHOLDER_FGCOLOR_PARSED = hex2Rgb(DEF_PLACEHOLDER_FGCOLOR);
    public static final Integer DEF_PLACEHOLDER_WIDTH = 300;
    public static final Integer DEF_PLACEHOLDER_HEIGHT = 250;
    public static final Integer DEF_PLACEHOLDER_MAX_WIDTH = 3500;
    public static final Integer DEF_PLACEHOLDER_MAX_HEIGHT = 3500;
    public static final Integer DEF_PLACEHOLDER_MIN_WIDTH = 1;
    public static final Integer DEF_PLACEHOLDER_MIN_HEIGHT = 1;

    public static final int DEF_PLACEHOLDER_FONT_SIZE = 700;
    public static final Font DEF_PLACEHOLDER_FONT = new Font("Courier New Bold", Font.BOLD, DEF_PLACEHOLDER_FONT_SIZE);

    public static Color hex2Rgb(String colorStr) {
        Validate.notBlank(colorStr, "Color string must not be null");
        String _color = StringUtils.rightPad(StringUtils.removeStart(colorStr, "#"), 6, colorStr.charAt(colorStr.length() - 1));
        return new Color(
                Integer.valueOf(_color.substring(0, 2), 16),
                Integer.valueOf(_color.substring(2, 4), 16),
                Integer.valueOf(_color.substring(4, 6), 16));
    }

    public static String rgb2hex(Color color) {
        Validate.notNull(color, "Color must not be null");
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    public static void fastChannelCopy(final InputStream src, final OutputStream dest) throws IOException {
        try (ReadableByteChannel inputChannel = Channels.newChannel(src);
             WritableByteChannel outputChannel = Channels.newChannel(dest)) {
            fastChannelCopy(inputChannel, outputChannel);
        }
    }

    public static void fastChannelCopy(final ReadableByteChannel src, final WritableByteChannel dest) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
        while (src.read(buffer) != -1) {
            // prepare the buffer to be drained
            buffer.flip();
            // write to the channel, may block
            dest.write(buffer);
            // If partial transfer, shift remainder down
            // If buffer is empty, same as doing clear()
            buffer.compact();
        }
        // EOF will leave buffer in fill state
        buffer.flip();
        // make sure the buffer is fully drained.
        while (buffer.hasRemaining()) {
            dest.write(buffer);
        }
    }

    public static void unmap(final MappedByteBuffer buffer) {
        if (null == buffer) {
            return;
        }
        try {
            final Method method = FileChannelImpl.class.getDeclaredMethod("unmap", MappedByteBuffer.class);
            method.setAccessible(true);
            method.invoke(null, buffer);
        } catch (final Exception ex) {
            //fails silently
        }
    }

    public static Calendar getCalendar() {
        return Calendar.getInstance(DEFAULT_TIMEZONE);
    }

    public static String getCurrentMonth() {
        return new SimpleDateFormat("MMM").format(getCalendar().getTime());
    }

    public static Date getCurrentDate() {
        return getCalendar().getTime();
    }

    public static long getCurrentTime() {
        return getCalendar().getTimeInMillis();
    }

}
