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

import java.util.EventObject;
import jhexedit.bdoc.*;

public class BinaryEditorEvent extends EventObject {
  
  // CONSTANTS
  public static final int DOCUMENT_CHANGED  = 0;
  public static final int SELECTION_CHANGED = 1;
  public static final int LOCATION_CHANGED  = 2;
  public static final int DOCUMENT_EVENT    = 3;
  
  // MEMBERS
  private BinaryDocument document;
  private Location location;
  private ByteSpan selection;
  private BinaryDocumentEvent docEvent;
  private int type;
  
  // CONSTRUCTOR
  public BinaryEditorEvent(BinaryEditor source, BinaryDocument document,
                           Location location, ByteSpan selection,
                           BinaryDocumentEvent docEvent, int type) {
    super(source);
    this.document = document;
    this.location = location;
    this.selection = selection;
    this.docEvent = docEvent;
    this.type = type;
  }

  // GETTERS
  public BinaryDocument getDocument() { return document; }
  public Location getCurrentLocation() { return location; }
  public ByteSpan getSelectionSpan() { return selection; }
  public BinaryDocumentEvent getDocumentEvent() { return docEvent; }
  public int getType() { return type; }
}
