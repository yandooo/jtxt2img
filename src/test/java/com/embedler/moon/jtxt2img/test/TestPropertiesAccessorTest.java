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

package com.embedler.moon.jtxt2img.test;

import com.embedler.moon.jtxt2img.CoreHelper;
import com.embedler.moon.jtxt2img.ImgTextProperties;
import com.embedler.moon.jtxt2img.ImgTextPropertiesAccessor;
import org.junit.Assert;
import org.junit.Test;

public class TestPropertiesAccessorTest {

    @Test
    public void checkAllNull(){
        ImgTextProperties imgTextProperties = new ImgTextProperties();
        ImgTextPropertiesAccessor imgTextPropertiesAccessor = new ImgTextPropertiesAccessor(imgTextProperties);

        Assert.assertFalse(imgTextPropertiesAccessor.isValid());
        Assert.assertFalse(imgTextPropertiesAccessor.isValidWidth());
        Assert.assertFalse(imgTextPropertiesAccessor.isValidHeight());
        Assert.assertFalse(imgTextPropertiesAccessor.isValidForegroundColor());
        Assert.assertFalse(imgTextPropertiesAccessor.isValidBackgroundColor());
        Assert.assertFalse(imgTextPropertiesAccessor.isValidFont());
        Assert.assertFalse(imgTextPropertiesAccessor.isValidFormat());
        Assert.assertNotNull(imgTextPropertiesAccessor.getDefaultText());
    }

    @Test
    public void checkWidth() {
        ImgTextProperties imgTextProperties = new ImgTextProperties();
        imgTextProperties.setWidth(-1);

        ImgTextPropertiesAccessor imgTextPropertiesAccessor = new ImgTextPropertiesAccessor(imgTextProperties);
        Assert.assertFalse(imgTextPropertiesAccessor.isValidWidth());

        imgTextProperties.setWidth(CoreHelper.DEF_PLACEHOLDER_MAX_WIDTH + 1);
        Assert.assertFalse(imgTextPropertiesAccessor.isValidWidth());
    }

    @Test
    public void checkHeight() {
        ImgTextProperties imgTextProperties = new ImgTextProperties();
        imgTextProperties.setHeight(-1);

        ImgTextPropertiesAccessor imgTextPropertiesAccessor = new ImgTextPropertiesAccessor(imgTextProperties);
        Assert.assertFalse(imgTextPropertiesAccessor.isValidHeight());

        imgTextProperties.setWidth(CoreHelper.DEF_PLACEHOLDER_MAX_HEIGHT + 1);
        Assert.assertFalse(imgTextPropertiesAccessor.isValidHeight());
    }

    @Test
    public void checkFgColor() {
        ImgTextProperties imgTextProperties = new ImgTextProperties();
        imgTextProperties.setFgColor("765");

        ImgTextPropertiesAccessor imgTextPropertiesAccessor = new ImgTextPropertiesAccessor(imgTextProperties);
        Assert.assertTrue(imgTextPropertiesAccessor.isValidForegroundColor());
        Assert.assertNotNull(imgTextPropertiesAccessor.getForegroundColor());

        imgTextProperties.setFgColor("-1");

        Assert.assertFalse(imgTextPropertiesAccessor.isValidForegroundColor());
    }

    @Test
    public void checkBgColor() {
        ImgTextProperties imgTextProperties = new ImgTextProperties();
        imgTextProperties.setBgColor("345");

        ImgTextPropertiesAccessor imgTextPropertiesAccessor = new ImgTextPropertiesAccessor(imgTextProperties);
        Assert.assertTrue(imgTextPropertiesAccessor.isValidBackgroundColor());
        Assert.assertNotNull(imgTextPropertiesAccessor.getBackgroundColor());

        imgTextProperties.setBgColor("-1");

        Assert.assertFalse(imgTextPropertiesAccessor.isValidBackgroundColor());
    }
}
