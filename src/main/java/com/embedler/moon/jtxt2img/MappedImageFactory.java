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

import javax.imageio.ImageTypeSpecifier;
import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * A factory for creating {@link BufferedImage}s backed by memory mapped files.
 * The data buffers will be allocated outside the normal JVM heap, allowing more efficient
 * memory usage for large images
 */
public final class MappedImageFactory {

    private static final Logger LOG = LoggerFactory.getLogger(MappedImageFactory.class);

    static final RasterFactory RASTER_FACTORY = createRasterFactory();

    private MappedImageFactory() {
    }

    public static BufferedImage createCompatibleMappedImage(int width, int height, int type) {
        BufferedImage temp = new BufferedImage(1, 1, type);
        return createCompatibleMappedImage(width, height, temp.getSampleModel().createCompatibleSampleModel(width, height), temp.getColorModel());
    }

    public static BufferedImage createCompatibleMappedImage(int width, int height, int type, IndexColorModel colorModel)  {
        BufferedImage temp = new BufferedImage(1, 1, type, colorModel);
        return createCompatibleMappedImage(width, height, temp.getSampleModel().createCompatibleSampleModel(width, height), temp.getColorModel());
    }

    public static BufferedImage createCompatibleMappedImage(int width, int height, GraphicsConfiguration configuration, int transparency)  {
        return createCompatibleMappedImage(width, height, configuration.getColorModel(transparency));
    }

    public static BufferedImage createCompatibleMappedImage(int width, int height, ImageTypeSpecifier type)  {
        return createCompatibleMappedImage(width, height, type.getSampleModel(width, height), type.getColorModel());
    }

    static BufferedImage createCompatibleMappedImage(int width, int height, ColorModel cm) {
        return createCompatibleMappedImage(width, height, cm.createCompatibleSampleModel(width, height), cm);
    }

    public static BufferedImage createCompatibleMappedImage(int width, int height, SampleModel sm, ColorModel cm) {
        DataBuffer buffer = MappedFileBuffer.create(sm.getTransferType(), width * height * sm.getNumDataElements(), 1);

        return new BufferedImage(cm, RASTER_FACTORY.createRaster(sm, buffer, new Point()), cm.isAlphaPremultiplied(), null);
    }

    private static RasterFactory createRasterFactory() {
        try {
            return new SunRasterFactory();
        } catch (LinkageError e) {
            String msg = "Could not instantiate SunWritableRaster, falling back to GenericWritableRaster.";
            if (LOG.isDebugEnabled()) {
                LOG.error(msg, e);
            }
        }

        return new GenericRasterFactory();
    }

    static interface RasterFactory {
        WritableRaster createRaster(SampleModel model, DataBuffer buffer, Point origin);
    }

    static final class GenericRasterFactory implements RasterFactory {
        public WritableRaster createRaster(final SampleModel model, final DataBuffer buffer, final Point origin) {
            return new GenericWritableRaster(model, buffer, origin);
        }
    }

    static final class SunRasterFactory implements RasterFactory {
        final private Constructor<WritableRaster> factoryMethod = getFactoryMethod();

        @SuppressWarnings("unchecked")
        private static Constructor<WritableRaster> getFactoryMethod() {
            try {
                Class<?> cls = Class.forName("sun.awt.image.SunWritableRaster");

                if (Modifier.isAbstract(cls.getModifiers())) {
                    throw new IncompatibleClassChangeError("sun.awt.image.SunWritableRaster has become abstract and can't be instantiated");
                }

                return (Constructor<WritableRaster>) cls.getConstructor(SampleModel.class, DataBuffer.class, Point.class);
            } catch (ClassNotFoundException e) {
                throw new NoClassDefFoundError(e.getMessage());
            } catch (NoSuchMethodException e) {
                throw new NoSuchMethodError(e.getMessage());
            }
        }

        public WritableRaster createRaster(final SampleModel model, final DataBuffer buffer, final Point origin) {
            try {
                return factoryMethod.newInstance(model, buffer, origin);
            } catch (InstantiationException e) {
                throw new Error("Could not create SunWritableRaster: ", e);
            } catch (IllegalAccessException e) {
                throw new Error("Could not create SunWritableRaster: ", e);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();

                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                } else if (cause instanceof Error) {
                    throw (Error) cause;
                }

                throw new UndeclaredThrowableException(cause);
            }
        }
    }
}