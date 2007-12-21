/* ************************************************************************
 *                                                                        *
 *  JHexEdit -- The hex editor written in Java.                           *
 *  Online at http://www.madcomputerscientist.net                         *
 *                                                                        *
 *  Copyright (c) 2006, Adam Fourney <adam.fourney(NOSPAM)@gmail.com>     *
 *  All rights reserved.                                                  *
 *                                                                        *
 *  Redistribution and use in source and binary forms, with or without    *
 *  modification, are permitted provided that the following conditions    *
 *  are met:                                                              *
 *                                                                        *
 *      * Redistributions of source code must retain the above            *
 *        copyright notice, this list of conditions and the               *
 *        following disclaimer.                                           *
 *      * Redistributions in binary form must reproduce the above         *
 *        copyright notice, this list of conditions and the               *
 *        following disclaimer in the documentation and/or other          *
 *        materials provided with the distribution.                       *
 *      * The name of the author, Adam Fourney, may not be used to        *
 *        endorse or promote products derived from this software          *
 *        without specific prior written permission.                      *
 *                                                                        *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS   *
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT     *
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS     *
 *  FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE        *
 *  COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,   *
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  *
 *  BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;      *
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER      *
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT    *
 *  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN     *
 *  ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE       *
 *  POSSIBILITY OF SUCH DAMAGE.                                           *
 *                                                                        *
 ************************************************************************ */

package jhexedit;

import jhexedit.bdoc.*;

public interface BinaryDocumentEditor {

  // Constants
  public static final Object CURSOR_KEY = new Object();
  public static final Object SELECTION_KEY = new Object();
  public static final Object ANNOTATION_KEY = new Object();
  public static final Object ANNOTATION_STYLE_MAP_KEY = new Object();

  // Keys used to "synchronize" common objects between editors. 
  public Object getCursorKey();
  public void setCursorKey(Object key);
  
  public Object getSelectionKey();
  public void setSelectionKey(Object key);
 
  public Object getAnnotationKey();
  public void setAnnotationKey(Object key);
  
  public Object getAnnotationStyleMapKey();
  public void setAnnotationStyleMapKey(Object key);
  
  // General getters and setters
  public AnnotatedBinaryDocument getDocument();
  public void setDocument(AnnotatedBinaryDocument document);

  public Cursor getDocumentCursor();
  public void setDocumentCursor(Cursor cursor);

  public ByteSpan getSelectionSpan();
  public void setSelectionSpan(ByteSpan selection);
  public void selectAll();

  public Object getFocusedAnnotation();
  public void setFocusedAnnotation(Object annotation);

  // Oprtaions on selections
  public void cut();
  public void copy();
  public void paste();
  public void replaceSelection(byte [] b);
}
