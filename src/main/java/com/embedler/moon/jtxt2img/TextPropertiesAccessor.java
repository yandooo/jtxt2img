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

public class TextPropertiesAccessor {

    private TextProperties textProperties;

    public TextPropertiesAccessor(TextProperties textProperties) {
        Validate.notNull(textProperties, "Text properties can not be null");
        this.textProperties = textProperties;
    }

    public boolean isValid() {
        return isValidSize() &&
                isValidText() &&
                isValidBackgroundColor() &&
                isValidForegroundColor() &&
                isValidFormat();
    }

    public boolean isValidFormat() {
        return StringUtils.isNotBlank(textProperties.getFormat());
    }

    public String getFormat() {
        return isValidFormat() ? textProperties.getFormat() : CoreHelper.DEF_PLACEHOLDER_FORMAT;
    }

    public boolean isValidText() {
        return StringUtils.isNotBlank(textProperties.getText());
    }

    public String getDefaultText() {
        return StringUtils.join(textProperties.getWidth(), "x", textProperties.getHeight());
    }

    public String getText() {
        return isValidText() ? textProperties.getText() : getDefaultText();
    }

    public boolean isValidForegroundColor() {
        boolean result = false;
        if (StringUtils.isNotBlank(textProperties.getFgColor())) {
            Matcher m = CoreHelper.COLOR_REGEXP.matcher(textProperties.getFgColor());
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
            Matcher m = CoreHelper.COLOR_REGEXP.matcher(textProperties.getFgColor());
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
        if (StringUtils.isNotBlank(textProperties.getBgColor())) {
            Matcher m = CoreHelper.COLOR_REGEXP.matcher(textProperties.getBgColor());
            result = m.matches();
        }
        return result;
    }

    public Color getBackgroundColor() {
        Color bgColor = getDefaultBackgroundColor();
        if (isValidForegroundColor()) {
            Matcher m = CoreHelper.COLOR_REGEXP.matcher(textProperties.getBgColor());
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
        return CoreHelper.DEF_PLACEHOLDER_MAX_HEIGHT;
    }

    public boolean isValidWidth() {
        int width = textProperties.getWidth();
        return width <= CoreHelper.DEF_PLACEHOLDER_MAX_WIDTH && width >= CoreHelper.DEF_PLACEHOLDER_MIN_WIDTH;
    }

    public int getHeight(){
        return isValidHeight() ? textProperties.getHeight() : getDefaultHeight();
    }

    public int getWidth(){
        return isValidWidth() ? textProperties.getWidth() : getDefaultWidth();
    }

    public boolean isValidHeight() {
        int height = textProperties.getHeight();
        return height <= CoreHelper.DEF_PLACEHOLDER_MAX_HEIGHT && height >= CoreHelper.DEF_PLACEHOLDER_MIN_HEIGHT;
    }

    public boolean isValidSize() {
        return isValidWidth() && isValidHeight();
    }

}
