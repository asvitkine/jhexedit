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

import java.io.IOException;
import java.util.Observable;

public class Cursor extends Observable {

  // PRIVATE MEMBERS
  private BinaryDocument bDoc;
  private Position pos;
  
  // CONSTRUCTORS
  Cursor( Position pos ) {
    this.pos = pos;
  }

  // GETTERS
  public BinaryDocument getDocument() {
    return pos.getDocument();
  }
  
  public Position getPosition() {
    return pos;
  }

  public Offset getOffset() {
    return pos.getDocument().createOffset(pos.getOffset());
  }

  // CURSOR METHODS
  public void seek( Location loc ) {
    pos = pos.getDocument().createPosition(loc.getOffset());
    setChanged();
    notifyObservers(getOffset());
  }

  public int read() throws IOException {
    int b = getDocument().read( pos );
    if (b > 0)
      moveCursor(1); 
    return b;
  }

  public int read( byte [] b ) throws IOException {
    int bytesRead = getDocument().read( pos, b );
    if (bytesRead > 0) 
      moveCursor( bytesRead );
    return bytesRead;
  }

  public int read( byte [] b, int off, int len ) {
    int bytesRead = getDocument().read( pos, b, off, len );
    if (bytesRead > 0) 
      moveCursor( bytesRead );
    return bytesRead;
  }

  public BinaryDocument read(int len) {
    BinaryDocument result = getDocument().read( pos, len );
    if (result != null)
      moveCursor( (int) result.length() );
    return result;
  }

  public void write( int b ) {
    getDocument().write( pos, b );
    moveCursor(1);
  }

  public void write( byte [] b ) {
    getDocument().write( pos, b );
    moveCursor(b.length);
  }

  public void write( byte [] b, int off, int len ) {
    getDocument().write( pos, b, off, len );
    moveCursor(len);
  }

  public void insert( int b ) {
    getDocument().insert( pos, b );
    moveCursor(1);
  }

  public void insert( byte [] b ) {
    getDocument().insert( pos, b );
    moveCursor(b.length);
  }

  public void insert( byte [] b, int off, int len ) {
    getDocument().insert( pos, b, off, len );
    moveCursor(len);
  }
  
  public int delete( int n ) {
    return getDocument().delete( pos, n );
  }
  
  public int skip( int n ) {
    if (pos.getOffset() + n >= getDocument().length())
      n = (int) (getDocument().length() - pos.getOffset() - n - 1);
    if (n>0) 
      moveCursor(n);
    
    return n;    
  }

  ////
  private void moveCursor(int n) {
    Position oldPos = pos;      
    pos = (Position) pos.addOffset(n);
    if (pos != oldPos)
      oldPos.dispose();

    setChanged();
    notifyObservers(getOffset());
  }
}
