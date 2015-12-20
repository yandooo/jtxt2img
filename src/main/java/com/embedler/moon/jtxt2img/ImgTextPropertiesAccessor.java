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
import org.apache.commons.lang3.math.NumberUtils;

import java.awt.*;
import java.util.regex.Matcher;

public class ImgTextPropertiesAccessor {

    private ImgTextProperties imgTextProperties;

    public ImgTextPropertiesAccessor(ImgTextProperties imgTextProperties) {
        Validate.notNull(imgTextProperties, "Text properties can not be null");
        this.imgTextProperties = imgTextProperties;
    }

    public boolean isValid() {
        return isValidSize() &&
                isValidText() &&
                isValidBackgroundColor() &&
                isValidForegroundColor() &&
                isValidFormat();
    }

    public boolean isValidFont() {
        return imgTextProperties.getFont() != null;
    }

    public Font getDefaultFont() {
        return CoreHelper.DEF_PLACEHOLDER_FONT;
    }

    public Font getFont() {
        return isValidFont() ? imgTextProperties.getFont() : getDefaultFont();
    }

    public boolean isValidFormat() {
        return imgTextProperties.getFormat() != null;
    }

    public ImgTextProperties.IMG_FORMAT getFormat() {
        return isValidFormat() ? imgTextProperties.getFormat() : ImgTextProperties.IMG_FORMAT.JPG;
    }

    public boolean isValidText() {
        return StringUtils.isNotBlank(imgTextProperties.getText());
    }

    public String getDefaultText() {
        return StringUtils.join(getWidth(), "x", getHeight());
    }

    public String getText() {
        return isValidText() ? imgTextProperties.getText() : getDefaultText();
    }

    public boolean isValidForegroundColor() {
        boolean result = false;
        if (StringUtils.isNotBlank(imgTextProperties.getFgColor())) {
            Matcher m = CoreHelper.COLOR_REGEXP.matcher(imgTextProperties.getFgColor());
            result = m.matches();
        }
        return result;
    }

    public Color getDefaultForegroundColor() {
        return CoreHelper.DEF_PLACEHOLDER_FGCOLOR_PARSED;
    }

    public Color getForegroundColor() {
        Color fgColor = getDefaultForegroundColor();
        if (isValidForegroundColor()) {
            Matcher m = CoreHelper.COLOR_REGEXP.matcher(imgTextProperties.getFgColor());
            if (m.matches()) {
                String temp = m.group(1);
                if (NumberUtils.isNumber("0x" + temp)) {
                    fgColor = CoreHelper.hex2Rgb(temp);
                }
            }
        }
        return fgColor;
    }

    public Color getDefaultBackgroundColor() {
        return CoreHelper.DEF_PLACEHOLDER_BGCOLOR_PARSED;
    }

    public boolean isValidBackgroundColor() {
        boolean result = false;
        if (StringUtils.isNotBlank(imgTextProperties.getBgColor())) {
            Matcher m = CoreHelper.COLOR_REGEXP.matcher(imgTextProperties.getBgColor());
            result = m.matches();
        }
        return result;
    }

    public Color getBackgroundColor() {
        Color bgColor = getDefaultBackgroundColor();
        if (isValidForegroundColor()) {
            Matcher m = CoreHelper.COLOR_REGEXP.matcher(imgTextProperties.getBgColor());
            if (m.matches()) {
                String temp = m.group(1);
                if (NumberUtils.isNumber("0x" + temp)) {
                    bgColor = CoreHelper.hex2Rgb(temp);
                }
            }
        }
        return bgColor;
    }

    public int getDefaultWidth() {
        return CoreHelper.DEF_PLACEHOLDER_WIDTH;
    }

    public int getDefaultHeight() {
        return CoreHelper.DEF_PLACEHOLDER_HEIGHT;
    }

    public boolean isValidWidth() {
        int width = imgTextProperties.getWidth();
        return width <= CoreHelper.DEF_PLACEHOLDER_MAX_WIDTH && width >= CoreHelper.DEF_PLACEHOLDER_MIN_WIDTH;
    }

    public int getHeight() {
        return isValidHeight() ? imgTextProperties.getHeight() : getDefaultHeight();
    }

    public int getWidth() {
        return isValidWidth() ? imgTextProperties.getWidth() : getDefaultWidth();
    }

    public boolean isValidHeight() {
        int height = imgTextProperties.getHeight();
        return height <= CoreHelper.DEF_PLACEHOLDER_MAX_HEIGHT && height >= CoreHelper.DEF_PLACEHOLDER_MIN_HEIGHT;
    }

    public boolean isValidSize() {
        return isValidWidth() && isValidHeight();
    }

}
