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

public class BinaryDocumentEditorAdapter implements BinaryDocumentEditor {

  private AnnotatedBinaryDocument document;      
  private Object cursorKey = BinaryDocumentEditor.CURSOR_KEY;
  private Object selectionKey = BinaryDocumentEditor.SELECTION_KEY;
  private Object annotationKey = BinaryDocumentEditor.ANNOTATION_KEY;
  private Object annotationStyleMapKey = BinaryDocumentEditor.ANNOTATION_STYLE_MAP_KEY;  
        
  /**
   * Default constructor.
   */
  public BinaryDocumentEditorAdapter() {
  }
  
  // DOCUMENT STUFF
   
  public AnnotatedBinaryDocument getDocument() {
    return document;
  }

  public void setDocument( AnnotatedBinaryDocument document ) {
    this.document = document;
    setSelectionSpan(null);
  }
  
  // "SYNCHRONIZATION" KEY STUFF
  
  public Object getCursorKey() {
    return cursorKey;
  }

  public void setCursorKey(Object key) {
    cursorKey = key; 
  }
  
  public Object getSelectionKey() {
    return selectionKey;
  }

  public void setSelectionKey(Object key) {
    selectionKey = key; 
  }
  
  public Object getAnnotationKey() {
    return annotationKey;
  }

  public void setAnnotationKey(Object key) {
    annotationKey = key; 
  }
  
  public Object getAnnotationStyleMapKey() {
    return annotationStyleMapKey;
  }

  public void setAnnotationStyleMapKey(Object key) {
    annotationStyleMapKey = key; 
  }
  
  // CURSOR STUFF

  public Cursor getDocumentCursor() {
    return (Cursor) document.getProperty(cursorKey);
  }

  public void setDocumentCursor(Cursor cursor) {
    document.putProperty(cursorKey, cursor);
  }
  
  // SELECTION STUFF  
  
  public ByteSpan getSelectionSpan() {
    return (ByteSpan) document.getProperty(selectionKey);
  }

  public void setSelectionSpan( ByteSpan selection ) {
    document.putProperty(selectionKey, selection);
  }

  public void selectAll() {
    setSelectionSpan( new ByteSpan(document.createOffset(0), 
                      document.createOffset(document.length())) );
  }

  // ANNOTAION STUFF
  
  public Object getFocusedAnnotation() {
    return document.getProperty(annotationKey);
  }

  public void setFocusedAnnotation( Object annotation ) {
    document.putProperty(annotationKey, annotation);
  } 
  
  // ANNOTAION STYLE MAP STUFF
  
  public Object getAnnotationStyleMap() {
    return document.getProperty(annotationStyleMapKey);
  }

  public void setAnnotationStyleMap( Object annotationStyleMap ) {
    document.putProperty(annotationStyleMapKey, annotationStyleMap);
  }

  // OPERATIONS ON SELECTIONS
  
  public void cut() {
  }

  public void copy() {
  }

  public void paste() {
  }

  public void replaceSelection(byte [] b) {
  }
}
