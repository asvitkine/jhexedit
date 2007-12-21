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

/**
 * TODO
 */
package jhexedit.bdoc;

public class ByteSpan {

  // PRIVATE MEMBERS
  private Location startLocation;
  private Location endLocation;

  // CONSTRUCTORS
  public ByteSpan( Location startLocation, Location endLocation ) {
  
    if ( startLocation.getDocument() != endLocation.getDocument() )
      throw new DocumentMismatchException(startLocation.getDocument(),
        "Bytespan start and end positions must belong to the same binary document.");
              
    this.startLocation = startLocation;
    this.endLocation   = endLocation;
  }

  // GETTERS
  public Location getStartLocation() {
    return startLocation;
  }

  public Location getEndLocation() {
    return endLocation;
  }

  public BinaryDocument getDocument() {
    return startLocation.getDocument();
  }

  public long length() {
    return endLocation.getOffset() -
           startLocation.getOffset() + 1;
  }

  public boolean contains( Location loc ) {
    return loc.compareTo(startLocation) >= 0 &&
           loc.compareTo(endLocation) <= 0;
  }

  public boolean contains( ByteSpan span ) {
    return startLocation.compareTo( span.getStartLocation() ) <= 0 &&
           endLocation.compareTo( span.getEndLocation() ) >= 0;
  }

  public ByteSpan [] union( ByteSpan span ) {
    ByteSpan [] result = null;
    
    if (span == null) {
      result = new ByteSpan[1]; 
      result[0] = this;
      return result;
    }

    Location outerStart, innerStart, innerEnd, outerEnd;

    if (startLocation.compareTo(span.getStartLocation()) < 0) {
      outerStart = startLocation;
      innerStart = span.getStartLocation();
    }
    else {
      outerStart = span.getStartLocation();
      innerStart = startLocation;
    }

    if (endLocation.compareTo(span.getEndLocation()) < 0) {
      innerEnd = endLocation;
      outerEnd = span.getEndLocation();
    }
    else {
      innerEnd = span.getEndLocation();
      outerEnd = endLocation;
    } 

    if ( innerEnd.compareTo(innerStart) < 0 ) {
      result = new ByteSpan[2];
      result[0] = new ByteSpan( outerStart, innerEnd );
      result[1] = new ByteSpan( innerStart, outerEnd );
    }
    else {
      result = new ByteSpan[1];
      result[0] = new ByteSpan( outerStart, outerEnd );
    }
    
    return result;
  }

  public ByteSpan intersection( ByteSpan span ) {
    if (span == null)
      return null;
    
    Location s,e;
    
    if ( startLocation.compareTo( span.getStartLocation() ) < 0 )
      s = span.getStartLocation();
    else
      s = startLocation;
    
    if ( endLocation.compareTo( span.getEndLocation() ) < 0 )
      e = endLocation;
    else
      e = getEndLocation();
    
    return new ByteSpan( s, e );
  }

  public ByteSpan [] difference( ByteSpan span ) {
    ByteSpan [] result;

    if (span == null) {
      result = new ByteSpan[1];
      result[0] = this;
      return result;
    }
      
    if (contains(span)) {
      result = new ByteSpan[2];
      result[0] = new ByteSpan( startLocation, span.getStartLocation().addOffset(-1) );
      result[1] = new ByteSpan( span.getEndLocation().addOffset(1), endLocation );
    }
    else {
      result = new ByteSpan[1];
      if (startLocation.compareTo(span.getStartLocation()) < 0)
        result[0] = new ByteSpan(startLocation, span.getStartLocation().addOffset(-1));
      else
        result[0] = new ByteSpan(span.getEndLocation().addOffset(1), endLocation);
    }

    return result;
  }
}
