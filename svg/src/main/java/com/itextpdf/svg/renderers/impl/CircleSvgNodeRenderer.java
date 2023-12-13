/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.svg.SvgConstants;

import com.itextpdf.svg.renderers.ISvgNodeRenderer;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;circle&gt; tag.
 */
public class CircleSvgNodeRenderer extends EllipseSvgNodeRenderer {


    @Override
    protected boolean setParameters(){
        cx = 0;
        cy = 0;
        if(getAttribute(SvgConstants.Attributes.CX) != null){
            cx = CssDimensionParsingUtils.parseAbsoluteLength(getAttribute(SvgConstants.Attributes.CX));
        }
        if(getAttribute(SvgConstants.Attributes.CY) != null){
            cy = CssDimensionParsingUtils.parseAbsoluteLength(getAttribute(SvgConstants.Attributes.CY));
        }

        if(getAttribute(SvgConstants.Attributes.R) != null
                && CssDimensionParsingUtils.parseAbsoluteLength(getAttribute(SvgConstants.Attributes.R)) >0){
            rx = CssDimensionParsingUtils.parseAbsoluteLength(getAttribute(SvgConstants.Attributes.R));
            ry=rx;
        }else{
            return false; //No drawing if rx is absent
        }
        return true;

    }

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        CircleSvgNodeRenderer copy = new CircleSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        return copy;
    }

}
