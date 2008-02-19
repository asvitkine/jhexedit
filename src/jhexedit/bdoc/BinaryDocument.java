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
import  java.util.*;
import  java.io.*;

/**
 * The BinaryDocument class essentially adds document style operations to
 * a RandomAccessFile. Some noteable features include the ability to insert or
 * delete bytes from any location in the document. Additionally, changes to the
 * document are not commited to disk until the save() or saveAs() methods are
 * invoked.
 * <p>
 * This class was developped independantly from the Swing Document model. 
 * However, like Swing Documents, there are Positions and Offsets. Positions
 * are designed to "float" as the document is modified (by insertions or
 * deletions). Offsets, of course, are fixed and are start from 0. Additionally,
 * the concept of a Location was added. Locations are abstractions of Positions
 * and Offsets, which include a reference to the Document for which they are
 * associated. Most methods use Locations for addressing, allowing bytes to be
 * addressed by either offset or position.
 * <p> 
 * Unlike arrays and other list-like structures, Locations refere the location
 * between bytes in the document. (Again, this is similar to Swing Documents).
 * Usually it is not necessary to make this distinction; reading 1 byte from
 * offset 2 behaves like reading index 2 of an array: (or byte addressable file).
 * <br>
 * <pre>
 * Document:
 *  'A' 'B' 'C' 'D'  &lt;- Bytes
 * 0   1   2   3   4 &lt;- Positions
 * 
 * Array:
 *  ['A', 'B', 'C', 'D']
 *
 * ** In both cases 'C' is read.
 * </pre>
 * It is only when describing ranges of bytes, or when describing the location of an 
 * insert, that the distinction becomes important. For example, the range 0-3 contains
 * three bytes not four. Similarily, inserting 'XYZ' at location 2 of 'ABCDEFG' yields
 * 'ABXYZCDEFG'.
 * <p>
 * Also, all non-negative locations are valid regardless of the document length. If
 * an attempt is made to read bytes from a location beyond the length of the document, 
 * then no bytes are returned as a result. However, inserts and writes will extend the
 * document as necessary. (By inserting 0 valued bytes). 
 * <p>
 * Another interesting point about BinaryDocuments, is that read-only does not imply
 * that changes are prohibitted. Instead, it simply means that changes can not be
 * saved back to the orriginal file. (By invoking the save() method). To save changes
 * made to a readOnly document, use saveAs().
 * <p>
 * BinaryDocuments can also create Cursors. Cursors esentially behave like
 * the implicit cursors used to read from streams or RandomAccessFiles. Cursors
 * should be used whenever a large portion of the document will be read sequentially.
 * However, cursors use positions for addressing, not offsets. So, they are 
 * guaranteed not to miss bytes, even if bytes are inserted or removed from earlier
 * in the document. 
 * <p>
 * Last, but not least, the current implementation of BinaryDocuments is not thread-safe.
 * Even though multiple cursors can be created, please resist the temptation to modify
 * the documents from several concurrently running threads.
 *
 * @author Adam Fourney
 */
public class BinaryDocument extends Observable {

  // PRIVATE MEMBERS
  private File file;
  private boolean readOnly;
  private boolean modified;
  
  private byte [] data;
  private int occupied;

  // Used for O(1) access to positions.
  private HashMap anchor2Offset;
  
  // CONSTRUCTORS
  /**
   * Construct an empty binary document.
   * Documents created in this way are not readOnly, but are considered
   * new, (as defined by the isNew() method). New documents must be saved
   * using the saveAs() method.
   */
  public BinaryDocument() {
    file     = null;
    readOnly = false;  
    modified = false;
    
    data = new byte[256];
    occupied = 0;    

    anchor2Offset = new HashMap();
  }
  
  /**
   * Construct a binary document from a file.
   * The document is opened in read/write mode.
   *
   * @param file The file to open.
   * @throws IOException if an exception occurs while reading the file.
   */
  public BinaryDocument( File file ) throws IOException  {
    this( file, false );
  }
  
  /**
   * Construct a binary document from a file.
   * The document is opened read-only mode if readOnly is true.
   * Otherwise the document is opened in in read/write mode.
   *
   * @param file The file to open.
   * @param readOnly True if the document should be opened in read-only mode.
   * @throws IOException if an exception occurs while reading the file.
   */
  public BinaryDocument( File file, boolean readOnly ) throws IOException {
    this.file     = file;
    this.readOnly = readOnly;
    this.modified = false;

    anchor2Offset = new HashMap();
    
    RandomAccessFile ioFile = new RandomAccessFile( file, "r" );
    occupied = (int) ioFile.length();
    data = new byte[occupied + 256];
    ioFile.read(data);
    ioFile.close();
  }
  
  // SAVE / CLOSE

  /**
   * Save the document back to the source file. 
   * This method saves the document back to the file from which it was
   * created. This method can not be called if the document is new or
   * read-only.
   *
   * @throws IOException if an exception occured while writing the file.
   * @throws DocumentSaveException if the document is read-only or if the document is new.
   */ 
  public void save() throws IOException { 
    if (isReadOnly())
      throw new DocumentSaveException(this,
        "Cannot call save() on a read-only document. Try saveAs(File).");
    
    if (isNew())
      throw new DocumentSaveException(this,
        "Cannot call save() on a new document. Try saveAs(File).");
    
    RandomAccessFile ioFile = new RandomAccessFile( file, "rw" );
    ioFile.write(data,0, (int) length());
    ioFile.close();
   
    modified = false;
  }

  /**
   * Save the document back to a new file. 
   * This method saves the document to a new file. This new file becomes the source
   * of the document, and subsequent calls to save() will save to this newly specified
   * file.
   *
   * @throws IOException if an exception occured while writing the file.
   */ 
  public void saveAs( File file ) throws IOException {
    this.file = file;
          
    RandomAccessFile ioFile = new RandomAccessFile( file, "rw" );
    ioFile.write(data,0,(int) length());
    ioFile.close();    
          
    modified = false;
  }

  /**
   * Close a document, releasing all resources.
   * Once a document is closed, it can not be re-opened and this instance
   * becomes invalid. Create a new BinaryDocument to re-open the file.
   * 
   * @throws IOException if an exception occured while closing the source file.
   */
  public void close() throws IOException {
    data     = null;
    occupied = 0;
    modified = false;
  }
  
  // GETTERS

  /**
   * Returns the length of document.
   */
  public long length() {
    return occupied;
  }

  /**
   * Returns true if the document is read-only.
   */
  public boolean isReadOnly() {
    return readOnly;
  }

  /**
   * Returns true if the document is new, and has not yet been saved.
   */
  public boolean isNew() {
    return (file == null);
  }

  /**
   * Returns true if the document has been modified since it was last 
   * opened, or last saved.
   */
  public boolean isModified() {
    return modified;
  }

  /**
   * Returns the source file of the document, or null if the document is new.
   */
  public File getFile() {
    return file;
  }

  // POSITIONS, OFFSETS, and CURSORS -- Oh my! 
  
  /**
   * Create a Position at the specified offset.
   * Positions track changes as the document is modified.
   * NOTE: Positions are bound to this document instance.
   * 
   * @return a new postion that begins at the specified location.
   */ 
  public Position createPosition(long offset) {
    Long _offset = new Long(offset);
    PositionAnchor anchor = null; 
    
    Set entries = anchor2Offset.entrySet();
    Iterator i = entries.iterator();

    while(i.hasNext()) {
      Map.Entry entry = (Map.Entry) i.next();
      if ( entry.getValue().equals(_offset) ) {
        anchor = (PositionAnchor) entry.getKey();
        break;
      }
    }
  
    if (anchor == null) {
      anchor = new PositionAnchor( this );
      anchor2Offset.put( anchor, _offset );
    }
    
    return new Position(this, anchor);  
  }

  /**
   * Create an Offset instance representing the specified offset.
   * NOTE: Offset instances are bound to this document instance.
   * 
   * @return a new Offset instance.
   */
  public Offset createOffset(long offset) {
    return new Offset(this, offset);
  }

  /**
   * Create a cursor that can be used to sequentially (or randomly)
   * access this document. Cursors are position based, and thus "float"
   * as the document is modified. (directly or from other cursors).
   *
   * @return a new cursor who's position begins at the specified location.
   */
  public Cursor createCursor(Location loc) {
    return new Cursor(createPosition(loc.getOffset()));
  }
   
  // READ OPERATIONS
  
  public int read(Location loc) {
    byte [] b = new byte[1];
    int ret = read( loc, b, 0, b.length );
    if (ret == -1) 
      return -1;
    else 
      return 0xFF & (int) b[0];
  }
  
  public int read(Location loc, byte [] b) {
    return read( loc, b, 0, b.length );
  }
  
  public int read(Location loc, byte [] b, int off, int len) {
    long offset = loc.getOffset();
    int bytesRemaining = (int) (length() - offset);
    
    if (len > bytesRemaining)
      len = bytesRemaining;
    
    if (len < 1)
      return -1;
    
    for (int i=0; i<len; i++)
      b[off+i] = data[(int) (offset+i)];

    return len;    
  }

  // WRITE OPERATIONS  
  public void write(Location loc, int b) {
    byte [] bt = new byte[1];
    bt[0] = (byte) b;
    write( loc, bt, 0, bt.length );
  }
  
  public void write(Location loc, byte [] b) {
    write( loc, b, 0, b.length ); 
  }
  
  public void write(Location loc, byte [] b, int off, int len) {
    modified = true;
    
    long offset = loc.getOffset();
    int bytesRemaining = (int) (length() - offset);

    if (len > bytesRemaining) {
      if ((int) offset + len >= data.length)
        expandBuffer(((int) offset + len) - data.length);
      occupied += len - bytesRemaining; 
    }

    byte[] oldContent = new byte[len];

    for (int i = 0; i < len; i++)
      oldContent[i] = data[(int) (offset+i)];
    
    for (int i=0; i<len; i++)
      data[(int) offset + i] = b[off+i];

    setChanged();
    notifyObservers( new ContentChangedEvent( this, new ByteSpan( loc, loc.addOffset(len-1) ),
                                              ContentChangedEvent.WRITTEN, oldContent ) );
    clearChanged();
  }
  
  // INSERT OPERATIONS
  public void insert(Location loc, int b) {
    byte [] bt = new byte[1];
    bt[0] = (byte) b;
    insert( loc, bt, 0, bt.length );
  }
  
  public void insert(Location loc, byte [] b) {
    insert( loc, b, 0, b.length );
  }
  
  public void insert(Location loc, byte [] b, int off, int len) {
    modified = true;
    
    long offset = loc.getOffset();
    int spaceRemaining = (int) (data.length - length());

    if (spaceRemaining < len)
      expandBuffer( len - spaceRemaining );

    for (int i=(int) length()-1; i>=(int) offset; i--)
      data[i+len] = data[i];
    occupied += len;

    for (int i=0; i<len; i++)
      data[(int) offset + i] = b[off + i];
    
    Vector anchors = new Vector( anchor2Offset.keySet() );
    HashMap anchor2Offset = new HashMap(2*this.anchor2Offset.size() +1);
    
    for (int i=0; i<anchors.size(); i++) {
      PositionAnchor anchor = (PositionAnchor) anchors.get(i);
      Long _offset = new Long(anchor.getOffset());
      if (offset < _offset.longValue())
        _offset = new Long(_offset.longValue() + len);
      anchor2Offset.put(anchor,_offset);
    }   

    this.anchor2Offset = anchor2Offset;

    setChanged();
    notifyObservers( new ContentChangedEvent( this, new ByteSpan( loc, loc.addOffset(len-1) ),
                                              ContentChangedEvent.INSERTED, null ) );
    clearChanged();
  }

  // DELETE
  public int delete(Location loc, int len) {
    modified = true;

    long offset = loc.getOffset();
    int bytesRemaining = (int) (length() - offset);

    if (len > bytesRemaining)
      len = bytesRemaining;

    byte[] oldContent = new byte[len];

    for (int i = 0; i < len; i++)
      oldContent[i] = data[(int) (offset + i)];

    occupied -= len;

    for (int i=(int) offset; i<(int) length(); i++)
      data[i] = data[i + len];
    
    Vector anchors = new Vector( anchor2Offset.keySet() );
    HashMap anchor2Offset = new HashMap(2*this.anchor2Offset.size() +1);
    
    for (int i=0; i<anchors.size(); i++) {
      PositionAnchor anchor = (PositionAnchor) anchors.get(i);
      Long _offset = new Long(anchor.getOffset());
      if (offset < _offset.longValue()) {
        if (len < _offset.longValue() - offset )        
          _offset = new Long(_offset.longValue() - len);
        else
          _offset = new Long(offset);
      }
      anchor2Offset.put(anchor,_offset);
    }   

    this.anchor2Offset = anchor2Offset;

    setChanged();
    notifyObservers( new ContentChangedEvent( this, new ByteSpan( loc, loc.addOffset(len-1) ),
                                              ContentChangedEvent.DELETED, oldContent ) );
    clearChanged();
    
    return len;
  }

  ////// PACKAGE PROTECTED
  void removeAnchor( PositionAnchor anchor ) {
    Long offset = (Long) anchor2Offset.get(anchor);
    anchor2Offset.remove(anchor);
  }

  long getAnchorOffset( PositionAnchor p ) {
    Long offset = (Long) anchor2Offset.get(p);
    if (offset == null) return -1;
    return offset.longValue();    
  }

  void expandBuffer(int minimum) {
    int expandBy = (512 < minimum ? minimum : 512);
    byte [] data = new byte[this.data.length+expandBy];
    for (int i=0; i<this.data.length; i++)
      data[i] = this.data[i];
    this.data = data;
  }

  void rawPrint() {
    System.out.println(new String(data));
  }
}
