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

import com.embedler.moon.jtxt2img.JTxt2Img;
import com.embedler.moon.jtxt2img.TextProperties;
import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;

public class ImgsTest {

    private String outputDirectory;

    @Before
    public void before() {
        outputDirectory = System.getenv("IMG_OUTPUT");
    }

    @Test
    public void simpleTest() {
        TextProperties textProperties = new TextProperties();

        textProperties.setBgColor("876");
        textProperties.setFgColor("754");
        textProperties.setFormat("jpg");
        textProperties.setWidth(200);
        textProperties.setHeight(200);
        textProperties.setText("hello-txt");

        BufferedImage bufferedImage = JTxt2Img.createBufferedImage(textProperties);
        JTxt2Img.write(new File(outputDirectory, "hello-text.jpg"), bufferedImage, "jpg");

    }
}
