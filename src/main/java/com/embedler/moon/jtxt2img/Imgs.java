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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class Imgs {

    private static final Logger LOG = LoggerFactory.getLogger(Imgs.class);

    public static ImageTextSettings calculateImageTextSettings(String text, int w, int h, Font font, int imageType) {
        ImageTextSettings imageTextSettings = new ImageTextSettings();

        BufferedImage bufferedImage = new BufferedImage(1, 1, imageType);
        Graphics2D g = bufferedImage.createGraphics();
        Font currentFont = font == null ? CoreHelper.DEF_PLACEHOLDER_FONT : font;

        Rectangle2D rect = null;

        int fontSize = CoreHelper.DEF_PLACEHOLDER_FONT_SIZE;
        final String fontName = currentFont.getFontName(CoreHelper.DEFAULT_LOCALE);
        final int fontStyle = currentFont.getStyle();

        Font fontIterator = null;
        do {
            fontSize--;
            fontIterator = new Font(fontName, fontStyle, fontSize);
            rect = getStringBoundsRectangle2D(g, text, fontIterator);
        } while ((rect.getWidth() >= w || rect.getHeight() >= h) && (fontSize > 1));
        g.dispose();
        bufferedImage = null;

        imageTextSettings.setFont(fontIterator);
        imageTextSettings.setFontSize(fontSize);
        imageTextSettings.setTextBounds(rect);

        return imageTextSettings;
    }

    private static Rectangle2D getStringBoundsRectangle2D(Graphics2D g, String title, Font font) {
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds(title, g);
        return rect;
    }

    public static BufferedImage read1x1BufferedImage(InputStream inputStream) {
        BufferedImage image = null;
        try (ImageInputStream stream = ImageIO.createImageInputStream(inputStream)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);

            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                reader.setInput(stream);
                ImageReadParam param = reader.getDefaultReadParam();
                param.setSourceRegion(new Rectangle(1, 1));
                image = reader.read(0, param);
            }

        } catch (Exception e) {
            String msg = "Can't read image probe [1x1] into mapped buffered image";
            if (LOG.isErrorEnabled())
                LOG.error(msg);
            throw new JTxt2ImgIoRuntimeException(msg, e);
        }

        return image;
    }

    private BufferedImage generateMappedImagePlaceholder(TextProperties textProperties) throws IOException {
        final TextPropertiesAccessor textPropertiesAccessor = new TextPropertiesAccessor(textProperties);

        final String textToDraw = textPropertiesAccessor.getText();
        final int h = textPropertiesAccessor.getHeight();
        final int w = textPropertiesAccessor.getWidth();
        final Color bgColor = textPropertiesAccessor.getBackgroundColor();
        final Color fgColor = textPropertiesAccessor.getForegroundColor();

        final int imageType = BufferedImage.TYPE_BYTE_BINARY;
        Font font = null;

        ImageTextSettings imageTextSettings = calculateImageTextSettings(textToDraw, w, h, font, imageType);

        font = imageTextSettings.getFont();
        Rectangle2D textBounds = imageTextSettings.getTextBounds();

        byte[] rmap = {(byte) bgColor.getRed(), (byte) fgColor.getRed()};
        byte[] gmap = {(byte) bgColor.getGreen(), (byte) fgColor.getGreen()};
        byte[] bmap = {(byte) bgColor.getBlue(), (byte) fgColor.getBlue()};

        IndexColorModel indexColorModel = new IndexColorModel(1, 2, rmap, gmap, bmap);
        BufferedImage targetMappedBufferedImage = MappedImageFactory.createCompatibleMappedImage(w, h, imageType, indexColorModel);

        Graphics2D g = targetMappedBufferedImage.createGraphics();
        g.setFont(font);
        g.setBackground(bgColor);
        g.setColor(fgColor);
        g.drawString(textToDraw, (w - (int) Math.ceil(textBounds.getWidth())) / 2 - (int) textBounds.getX(), (h - (int) Math.ceil(textBounds.getHeight())) / 2 - (int) textBounds.getY());
        g.dispose();

        return targetMappedBufferedImage;
    }
}
