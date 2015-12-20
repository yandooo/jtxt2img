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

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class JTxt2Img {

    private static final Logger LOG = LoggerFactory.getLogger(JTxt2Img.class);

    private final ImgTextProperties imgTextProperties;
    private final ImgTextPropertiesAccessor imgTextPropertiesAccessor;
    private BufferedImage bufferedImage;

    private JTxt2Img(ImgTextProperties imgTextProperties) {
        this.imgTextProperties = imgTextProperties;
        imgTextPropertiesAccessor = new ImgTextPropertiesAccessor(imgTextProperties);
    }

    public static JTxt2Img withText(final String text) {
        Validate.notNull(text, "Image text must not be null");

        ImgTextProperties imgTextProperties = new ImgTextProperties();
        imgTextProperties.setText(text);
        return new JTxt2Img(imgTextProperties);
    }

    public static JTxt2Img withProperties(final ImgTextProperties imgTextProperties) {
        Validate.notNull(imgTextProperties, "Properties must not be null");
        ImgTextPropertiesAccessor imgTextPropertiesAccessor = new ImgTextPropertiesAccessor(imgTextProperties);
        Validate.isTrue(imgTextPropertiesAccessor.isValidText());
        return new JTxt2Img(imgTextProperties);
    }

    public JTxt2Img foregroundColor(final String colorCode) {
        imgTextProperties.setFgColor(colorCode);
        return this;
    }

    public JTxt2Img backgroundColor(final String colorCode) {
        imgTextProperties.setBgColor(colorCode);
        return this;
    }

    public JTxt2Img width(final int width) {
        imgTextProperties.setWidth(width);
        return this;
    }

    public JTxt2Img height(final int height) {
        imgTextProperties.setHeight(height);
        return this;
    }

    public JTxt2Img font(final Font font) {
        imgTextProperties.setFont(font);
        return this;
    }

    public JTxt2Img format(final ImgTextProperties.IMG_FORMAT imgFormat) {
        imgTextProperties.setFormat(imgFormat);
        return this;
    }

    public JTxt2Img generate() {
        bufferedImage = ImageProcessor.forProperties(imgTextProperties).createBufferedImage();
        return this;
    }

    public boolean write(File file) {
        Validate.isTrue(file != null && file.getParentFile().exists(), "File must not be null and exists");
        boolean result = false;
        try (OutputStream os = new FileOutputStream(file)) {
            result = write(os);
        } catch (Exception e) {
            String msg = "Can't write image placeholder to the file {}";
            if (LOG.isErrorEnabled())
                LOG.error(msg, file.getName());
            throw new JTxt2ImgIoRuntimeException(msg, e);
        }
        return result;
    }

    public boolean write(OutputStream outputStream) {
        Validate.notNull(outputStream, "OutputStream must not be null");
        Validate.notNull(bufferedImage, "Image must be generated before writing to output stream");
        boolean result = false;
        ImgTextPropertiesAccessor imgTextPropertiesAccessor = new ImgTextPropertiesAccessor(imgTextProperties);
        try {
            result = ImageIO.write(bufferedImage, imgTextPropertiesAccessor.getFormat().name().toLowerCase(), outputStream);
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            String msg = "Can't write image placeholder to the out stream";
            if (LOG.isErrorEnabled())
                LOG.error(msg);
            throw new JTxt2ImgIoRuntimeException(msg, e);
        }
        return result;
    }
}
