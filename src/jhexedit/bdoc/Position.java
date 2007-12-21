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

/**
 * Positions represent a location between bytes, rather than an
 * offset from the start of the document. As such, positions will
 * "float" (move forwards or backwards) as the document is modified
 * through insertions and deletions. For this reason, positions are
 * maintained by their associated documents. This implies that
 * the number of operations required to insert or delete bytes from
 * the document may be effected by the number of positions that occur
 * after the location of the modification. (The order of this relationship
 * is implementation specific, but can be presumed to be no worse than O(<i>n </i>),
 * where <i>n </i>is the number of positions after the modification) For this
 * reason, positions should be used as marks, but not as indices or cursors.
 * <p>
 * No special procedures are required to destroy a Position. Like all simple
 * objects, the JVM's garbage collector will reclaim the resouces at some point
 * after the last reference to the position is lost. However, the garbace 
 * collector may run infrequently. Because of the complexity relationship
 * described above, it may be desirable to remove the position from the 
 * document as soon as it is known that the position is nolonger required. 
 * (Possibly well before the garbage collector figures this out for itself).
 * For this reason, Positions offer the dispose() method. Calling this method is 
 * optional, but may increase the effiency of some later operations. Do not, 
 * however, dispose of a position too soon; if there exists other references
 * to the position, then these references will be broken.
 * <p>
 * Currently, positions are implemented as follows:
 * <br>
 * <pre>
 *       Your Position Reference  
 *                   |
 *                   V
 *          +----------------+
 *          |    Position    |
 *          +----------------+
 *                   |
 *                   V
 *          +----------------+
 *          |    Position    | /_____ Document's Position Reference
 *          |     Anchor     | \ 
 *          +----------------+
 * </pre>
 * When the "Position" instance is nolonger referenced, and its finalize() method
 * is invoked by the garbage collector, then the Document detroys the "Position
 * Anchor". Calling Position.dispose() destroys the Position Anchor early.         
 *          
 * @author Adam Fourney
 */
public class Position extends Location {

  private PositionAnchor anchor;
  
  Position(BinaryDocument bDoc, PositionAnchor anchor) {
    super(bDoc);      
    this.anchor = anchor;
    anchor.referenceAdded();
  }

  protected void finalize() throws Throwable {
    try {
      dispose();
    }
    finally {
      super.finalize();
    }
  }
  
  public long getOffset() {
    return anchor.getOffset();
  }
          
  public Location addOffset(long offset) {
    return anchor.addOffset(offset);
  }

  public int compareTo(Object o) {
    return anchor.compareTo(o);
  }

  public void dispose() {
    if (anchor != null) {
      anchor.referenceLost();
      anchor = null;
    }
  }
}
