/**
 * Copyright (c) 2005, www.pdfbox.org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of pdfbox; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://www.pdfbox.org
 *
 */
package at.ac.tuwien.dbai.pdfwrap.operator;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.util.PDFOperator;

import at.ac.tuwien.dbai.pdfwrap.pdfread.PDFObjectExtractor;

/**
 * Implementation of content stream operator for PDFObjectExtractor.
 * 
 * Adapted from PDFBox code
 * @author Ben Litchfield, ben@benlitchfield.com
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 */
public class SetNonStrokingRGBColor extends org.apache.pdfbox.util.operator.SetNonStrokingRGBColor
{
    /**
     * rg Set color space for non stroking operations.
     * @param operator The operator that is being executed.
     * @param arguments List
     * @throws IOException If an error occurs while processing the font.
     */
    public void process(PDFOperator operator, List arguments) throws IOException
    {
        super.process( operator, arguments );
        PDFObjectExtractor drawer = (PDFObjectExtractor)context;
        COSNumber r = (COSNumber)arguments.get( 0 );
        COSNumber g = (COSNumber)arguments.get( 1 );
        COSNumber b = (COSNumber)arguments.get( 2 );
        drawer.setNonStrokingColor( new Color( r.floatValue(), g.floatValue(), b.floatValue() ) );
    }
}
