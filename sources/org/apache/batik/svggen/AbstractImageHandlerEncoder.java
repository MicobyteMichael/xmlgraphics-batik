/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.*;
import java.awt.image.*;
import java.awt.image.renderable.RenderableImage;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.net.*;

import org.w3c.dom.Element;

import org.apache.batik.ext.awt.RenderingHintsKeyExt;

/**
 * This abstract implementation of the ImageHandler interface
 * is intended to be the base class for ImageHandlers that generate
 * image files for all the images they handle. This class stores
 * images in an configurable directory. The xlink:href value the
 * class generates is made of a configurable url root and the name
 * of the file created by this handler.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see             org.apache.batik.svggen.SVGGraphics2D
 * @see             org.apache.batik.svggen.ImageHandlerJPEGEncoder
 * @see             org.apache.batik.svggen.ImageHandlerPNGEncoder
 */
public abstract class AbstractImageHandlerEncoder extends DefaultImageHandler {
    private static final AffineTransform IDENTITY = new AffineTransform();

    /**
     * Directory where all images are placed
     */
    private String imageDir = "";

    /**
     * Value for the url root corresponding to the directory
     */
    private String urlRoot = "";

    /**
     * @param generatorContext the context in which the handler will work.
     * @param imageDir directory where this handler should generate images.
     *        If null, an SVGGraphics2DRuntimeException is thrown.
     * @param urlRoot root for the urls that point to images created by this
     *        image handler. If null, then the url corresponding to imageDir
     *        is used.
     */
    public AbstractImageHandlerEncoder(String imageDir, String urlRoot)
        throws SVGGraphics2DIOException {
        if (imageDir == null)
            throw new SVGGraphics2DRuntimeException(ERR_IMAGE_DIR_NULL);

        File imageDirFile = new File(imageDir);
        if (!imageDirFile.exists())
            throw new SVGGraphics2DRuntimeException(ERR_IMAGE_DIR_DOES_NOT_EXIST);

        this.imageDir = imageDir;
        if (urlRoot != null)
            this.urlRoot = urlRoot;
        else {
            try{
                this.urlRoot = imageDirFile.toURL().toString();
            } catch (MalformedURLException e) {
                throw new SVGGraphics2DIOException(ERR_CANNOT_USE_IMAGE_DIR+
                                                   e.getMessage(),
                                                   e);
            }
        }
    }

    /**
     * This template method should set the xlink:href attribute on the input
     * Element parameter
     */
    protected void handleHREF(Image image, Element imageElement,
                              SVGGeneratorContext generatorContext)
        throws SVGGraphics2DIOException {
        // Create an buffered image where the image will be drawn
        Dimension size = new Dimension(image.getWidth(null),
                                       image.getHeight(null));
        BufferedImage buf = buildBufferedImage(size);

        java.awt.Graphics2D g = buf.createGraphics();
        g.setRenderingHint(RenderingHintsKeyExt.KEY_BUFFERED_IMAGE, buf);
        g.clip(new Rectangle(0, 0, buf.getWidth(), buf.getHeight()));

        g.drawImage(image, 0, 0, null);
        g.dispose();

        // Save image into file
        saveBufferedImageToFile(imageElement, buf, generatorContext);
    }

    /**
     * This template method should set the xlink:href attribute on the input
     * Element parameter
     */
    protected void handleHREF(RenderedImage image, Element imageElement,
                              SVGGeneratorContext generatorContext)
        throws SVGGraphics2DIOException {
        // Create an buffered image where the image will be drawn
        Dimension size = new Dimension(image.getWidth(), image.getHeight());
        BufferedImage buf = buildBufferedImage(size);

        java.awt.Graphics2D g = buf.createGraphics();

        // The following help to keep the GVT renderer happy.
        g.setRenderingHint(RenderingHintsKeyExt.KEY_BUFFERED_IMAGE, buf);
        g.clip(new Rectangle(0, 0, buf.getWidth(), buf.getHeight()));

        g.drawRenderedImage(image, IDENTITY);
        g.dispose();

        // Save image into file
        saveBufferedImageToFile(imageElement, buf, generatorContext);
    }

    /**
     * This template method should set the xlink:href attribute on the input
     * Element parameter
     */
    protected void handleHREF(RenderableImage image, Element imageElement,
                              SVGGeneratorContext generatorContext)
        throws SVGGraphics2DIOException {
        // Create an buffered image where the image will be drawn
        Dimension size = new Dimension((int)Math.ceil(image.getWidth()),
                                       (int)Math.ceil(image.getHeight()));
        BufferedImage buf = buildBufferedImage(size);

        java.awt.Graphics2D g = buf.createGraphics();

        // The following help to keep the GVT renderer happy.
        g.setRenderingHint(RenderingHintsKeyExt.KEY_BUFFERED_IMAGE, buf);
        g.clip(new Rectangle(0, 0, buf.getWidth(), buf.getHeight()));

        g.drawRenderableImage(image, IDENTITY);
        g.dispose();

        // Save image into file
        saveBufferedImageToFile(imageElement, buf, generatorContext);
    }

    private void saveBufferedImageToFile(Element imageElement,
                                         BufferedImage buf,
                                         SVGGeneratorContext generatorContext)
        throws SVGGraphics2DIOException {
        if (generatorContext == null)
            throw new SVGGraphics2DRuntimeException(ERR_CONTEXT_NULL);

        // Create a new file in image directory
        File imageFile = null;

        // While the files we are generating exist, try to create another
        // id that is unique.
        while (imageFile == null) {
            String fileId = generatorContext.idGenerator.generateID(getPrefix());
            imageFile = new File(imageDir, fileId + getSuffix());
            if (imageFile.exists())
                imageFile = null;
        }

        // Encode image here
        encodeImage(buf, imageFile);

        // Update HREF
        imageElement.setAttributeNS(XLINK_NAMESPACE_URI,
                                    ATTR_XLINK_HREF, urlRoot + "/" +
                                    imageFile.getName());
    }

    /**
     * @return the suffix used by this encoder. E.g., ".jpg" for
     * ImageHandlerJPEGEncoder
     */
    public abstract String getSuffix();

    /**
     * @return the prefix used by this encoder. E.g., "jpegImage" for
     * ImageHandlerJPEGEncoder
     */
    public abstract String getPrefix();

    /**
     * Derived classes should implement this method and encode the input
     * BufferedImage as needed
     */
    public abstract void encodeImage(BufferedImage buf, File imageFile)
        throws SVGGraphics2DIOException;

    /**
     * This method creates a BufferedImage of the right size and type
     * for the derived class.
     */
    public abstract BufferedImage buildBufferedImage(Dimension size);
}
