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

import java.awt.*;
import java.util.regex.Pattern;

public enum CoreHelper {
    ;

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
}
