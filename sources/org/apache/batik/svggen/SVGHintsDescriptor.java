/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.util.Map;
import java.util.Hashtable;
import java.util.Set;
import java.util.HashSet;

import org.w3c.dom.*;

/**
 * Describes a set of SVG hints
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see             org.apache.batik.svggen.SVGRenderingHints
 */
public class SVGHintsDescriptor implements SVGDescriptor, SVGSyntax {
    public static final String ERROR_NULL_INPUT = "none of the input arguments should be null";

    private String colorInterpolation;
    private String colorRendering;
    private String textRendering;
    private String shapeRendering;
    private String imageRendering;

    /**
     * Constructor
     */
    public SVGHintsDescriptor(String colorInterpolation,
                              String colorRendering,
                              String textRendering,
                              String shapeRendering,
                              String imageRendering){
        if(colorInterpolation == null ||
           colorRendering == null ||
           textRendering == null ||
           shapeRendering == null ||
           imageRendering == null)
            throw new SVGGraphics2DRuntimeException(ErrorConstants.ERR_HINT_NULL);

        this.colorInterpolation = colorInterpolation;
        this.colorRendering = colorRendering;
        this.textRendering = textRendering;
        this.shapeRendering = shapeRendering;
        this.imageRendering = imageRendering;
    }

    public Map getAttributeMap(Map attrMap){
        if(attrMap == null)
            attrMap = new Hashtable();

        attrMap.put(SVG_COLOR_INTERPOLATION_ATTRIBUTE, colorInterpolation);
        attrMap.put(SVG_COLOR_RENDERING_ATTRIBUTE, colorRendering);
        attrMap.put(SVG_TEXT_RENDERING_ATTRIBUTE, textRendering);
        attrMap.put(SVG_SHAPE_RENDERING_ATTRIBUTE, shapeRendering);
        attrMap.put(SVG_IMAGE_RENDERING_ATTRIBUTE, imageRendering);

        return attrMap;
    }

    public Set getDefinitionSet(Set defSet){
        if(defSet == null)
            defSet = new HashSet();

        return defSet;
    }
}
