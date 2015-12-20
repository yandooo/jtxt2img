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

import com.embedler.moon.jtxt2img.mmap.MappedImageFactory;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;

public class ImageProcessor {

    private static int BI_IMAGE_TYPE = BufferedImage.TYPE_BYTE_BINARY;

    static class ImageTextSettings {

        private Rectangle2D textBounds;
        private Font font;
        private int fontSize;

        public Rectangle2D getTextBounds() {
            return textBounds;
        }

        public void setTextBounds(Rectangle2D textBounds) {
            this.textBounds = textBounds;
        }

        public Font getFont() {
            return font;
        }

        public void setFont(Font font) {
            this.font = font;
        }

        public int getFontSize() {
            return fontSize;
        }

        public void setFontSize(int fontSize) {
            this.fontSize = fontSize;
        }
    }

    private final ImgTextProperties imgTextProperties;

    private ImageProcessor(ImgTextProperties imgTextProperties) {
        this.imgTextProperties = imgTextProperties;
    }

    public static ImageProcessor forProperties(final ImgTextProperties imgTextProperties) {
        return new ImageProcessor(imgTextProperties);
    }

    private ImageTextSettings calculateImageTextSettings() {

        final ImgTextPropertiesAccessor imgTextPropertiesAccessor = new ImgTextPropertiesAccessor(imgTextProperties);

        final String text = imgTextPropertiesAccessor.getText();
        final int h = imgTextPropertiesAccessor.getHeight();
        final int w = imgTextPropertiesAccessor.getWidth();
        Font currentFont = imgTextPropertiesAccessor.getFont();

        ImageTextSettings imageTextSettings = new ImageTextSettings();

        BufferedImage bufferedImage = new BufferedImage(1, 1, BI_IMAGE_TYPE);
        Graphics2D g = bufferedImage.createGraphics();

        Rectangle2D rect = null;

        int fontSize = CoreHelper.DEF_PLACEHOLDER_FONT_SIZE;
        final String fontName = currentFont.getFontName();
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

    private Rectangle2D getStringBoundsRectangle2D(Graphics2D g, String title, Font font) {
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds(title, g);
        return rect;
    }

    public BufferedImage createBufferedImage() {
        final ImgTextPropertiesAccessor imgTextPropertiesAccessor = new ImgTextPropertiesAccessor(imgTextProperties);

        final String textToDraw = imgTextPropertiesAccessor.getText();
        final int h = imgTextPropertiesAccessor.getHeight();
        final int w = imgTextPropertiesAccessor.getWidth();
        final Color bgColor = imgTextPropertiesAccessor.getBackgroundColor();
        final Color fgColor = imgTextPropertiesAccessor.getForegroundColor();

        ImageTextSettings imageTextSettings = calculateImageTextSettings();
        Rectangle2D textBounds = imageTextSettings.getTextBounds();

        byte[] rmap = {(byte) bgColor.getRed(), (byte) fgColor.getRed()};
        byte[] gmap = {(byte) bgColor.getGreen(), (byte) fgColor.getGreen()};
        byte[] bmap = {(byte) bgColor.getBlue(), (byte) fgColor.getBlue()};

        IndexColorModel indexColorModel = new IndexColorModel(1, 2, rmap, gmap, bmap);
        BufferedImage mappedBufferedImage = MappedImageFactory.createCompatibleMappedImage(w, h, BI_IMAGE_TYPE, indexColorModel);

        Graphics2D g = mappedBufferedImage.createGraphics();
        g.setFont(imageTextSettings.getFont());
        g.setBackground(bgColor);
        g.setColor(fgColor);
        g.drawString(textToDraw, (w - (int) Math.ceil(textBounds.getWidth())) / 2 - (int) textBounds.getX(), (h - (int) Math.ceil(textBounds.getHeight())) / 2 - (int) textBounds.getY());
        g.dispose();

        return mappedBufferedImage;
    }
}
