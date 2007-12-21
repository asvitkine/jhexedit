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

package jhexedit.bdoc;
import  java.io.*;
import  java.util.*;

public class AnnotatedBinaryDocument extends BinaryDocument {
   
  // PRIVATE MEMBERS
  protected TreeMap indexMap;
  protected HashMap keyMap;   
  protected HashMap properties;
        
  // CONSTRUCTORS
  public AnnotatedBinaryDocument() {
    super();
    indexMap = new TreeMap();
    keyMap = new HashMap();
    properties = new HashMap();
  }

  public AnnotatedBinaryDocument(File file) throws IOException {
    super(file);
    indexMap = new TreeMap();
    keyMap = new HashMap();
    properties = new HashMap();
  }
  
  public AnnotatedBinaryDocument(File file, boolean readOnly) throws IOException {
    super(file, readOnly);
    indexMap = new TreeMap();
    keyMap = new HashMap();
    properties = new HashMap();
  }
  
  public AnnotatedBinaryDocument(File file, boolean readOnly, boolean loadAnnotations) throws IOException {
    super();
    // TODO: Set the file and length. Clear isNew
  }
  
  // SIMPLE PROPERTY STUFF
  public void putProperty(Object key, Object value) {
    Object oldValue = properties.get(key);
    
    if (value == null)
      properties.remove(key);
    else
      properties.put(key,value);

    firePropertyChanged(new PropertyChangedEvent(this, key, oldValue, value));
  }

  public Object getProperty(Object key) {
    return properties.get(key);
  }
  
  protected void firePropertyChanged(PropertyChangedEvent e) {
    setChanged();
    notifyObservers(e);
  }

  public Object [] getProperties() {
    return properties.keySet().toArray();
  }
 
 /* 
  // ANNOTATED BINARY DOCUMENT STUFF
  public void addAnnotation(Object key, ByteSpan span, Object value) {
    setChanged();
    notifyObservers( new AnnotationChangedEvent(this, annotation, AnnotationChangedEvent.ADDED) );
  }
  
  public void removeAnnotation(Object value) {
    setChanged();
    notifyObservers( new AnnotationChangedEvent(this, annotation, AnnotationChangedEvent.DELETED) );
  }

  public int countAnnotations() {
    return hashMap.size();
  }
 
  public AnnotatedByteSpan [] getAnnotations(ByteSpan span, boolean matchPartials) {
    return null;
  }

  public AnnotatedByteSpan [] getAnnotations(ByteSpan span) {
    return getAnnotations(span, true);
  }  
  */
}

