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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import jhexedit.bdoc.*;
import jhexedit.textgrid.*;

public class CharEditor extends TextGrid implements BinaryEditor {
        
  // CONSTANTS
  public static final int DEFAULT_BYTES_PER_ROW = 16;
  
  // MEMBERS
  protected AnnotatedBinaryDocument document;
  protected Location location;
  protected ByteSpan selection;
  protected LinkedList listeners;
  
  protected int bytesPerRow = DEFAULT_BYTES_PER_ROW;

  protected LocalTextGridModel localTextGridModel;
  protected LocalTextGridCursor localTextGridCursor;
  protected LocalDocumentObserver localDocumentObserver;

  /**
   * Construct the editor with a document.
   */
  public CharEditor(AnnotatedBinaryDocument document) {
    super(new TestTextGridModel());
    listeners = new LinkedList();
    
    localTextGridModel = new LocalTextGridModel(this);
    localTextGridCursor = new LocalTextGridCursor(this);
    localDocumentObserver = new LocalDocumentObserver();

    setDocument(document);
    setModel(localTextGridModel);
    setTextGridCursor(localTextGridCursor);
   
    setSelectionSpan(null);
    setCurrentLocation(document.createOffset(0));

    setBackground(Color.WHITE);
    setForeground(Color.BLACK);
  }

  public int getBytesPerRow() {
    return bytesPerRow;
  }  
  
  public AnnotatedBinaryDocument getDocument() {
    return document;
  }

  public void setDocument( AnnotatedBinaryDocument document ) {
    if (this.document != null)
      document.deleteObserver(localDocumentObserver);
    
    this.document = document;
    
    if (this.document != null)
      document.addObserver(localDocumentObserver);
  }

  public Location getCurrentLocation() {
    return localTextGridModel.gridToLocation(localTextGridCursor.getCurrentRow(),
                                             localTextGridCursor.getCurrentColumn());
  }
  
  public void setCurrentLocation(Location location) {
    localTextGridCursor.moveTo(location);
  }

  public ByteSpan getSelectionSpan() {
    return selection;
  }
  
  public void setSelectionSpan(ByteSpan selection) {
    this.selection = selection;
    fireBinaryEditorEvent( new BinaryEditorEvent(this, document, getCurrentLocation(), selection, null,
                                                    BinaryEditorEvent.SELECTION_CHANGED) );
    repaint();
  }

  public void addBinaryEditorListener(BinaryEditorListener l) {
    listeners.add(l);
  }

  public void removeBinaryEditorListener(BinaryEditorListener l) {
    listeners.remove(l);
  }

  public void fireBinaryEditorEvent(BinaryEditorEvent e) {
    Iterator i = listeners.iterator();
    while(i.hasNext()) {
      BinaryEditorListener l = (BinaryEditorListener) i.next();
      l.editorUpdated(e);
    }
  }
  
  ////////////////////////////////
  // GRID MODEL
  private class LocalTextGridModel implements TextGridModel {
    private int lastRowIndex = 0;
    private String lastRowText = null;
    private LinkedList listeners;
    private CharEditor parent;
    private Color whiteColor = new Color(254, 254, 254);
    private Color alternateColor = new Color(237, 243, 254);
    
    public LocalTextGridModel(CharEditor parent) {
      listeners = new LinkedList();
      this.parent = parent;
    }
    
    public int getColumnCount() {
      return getRowText(0).length();
    }
    
    public int getRowCount() {
      return (int) document.length()/bytesPerRow + 1;
    }
  
    public char getCharAt(int row, int col) {
      if (lastRowText == null || lastRowIndex != row ) {
        lastRowIndex = row;
        lastRowText  = getRowText(row);
      }
      return lastRowText.charAt(col);
    }
    
    public Color getCharColor(int row, int col) {
      return Color.BLACK;
    }
    
    public Color getCharBackground(int row, int col) {
      return (row % 2 == 0 ? whiteColor : alternateColor);
    }
    
    public int getCharStyle(int row, int col) {
      return 0;
    }

    public void addTextGridModelListener(TextGridModelListener l) {
      listeners.add(l);
    }
    
    public void removeTextGridModelListener(TextGridModelListener l) {
      listeners.remove(l);
    }

    public void fireTextGridModelEvent(TextGridModelEvent e) {
      Iterator i = listeners.iterator();
      while(i.hasNext()) {
        TextGridModelListener l = (TextGridModelListener) i.next();
        l.textGridUpdated(e);
        lastRowIndex = 0;
        lastRowText = null;
      }
    }

    public String getRowText(int row) {
      String result = "";
      int bytesRead = 0;
      byte [] b = new byte[bytesPerRow];
      int i;
      
      try {
        bytesRead = document.read(document.createOffset(row*bytesPerRow), b);
      }
      catch(Exception ignore) {}
      
      for (i=0; i<bytesRead; i++) {
        if (b[i] >= 32 && b[i] <= 126) {
          result += (char) b[i];
        } else {
          result += '.';
        }
      }

      for (; i<bytesPerRow; i++) {
        result = result + " "; 
      }
      
      return result;
    }

    public Location gridToLocation(int row, int col) {
      return document.createOffset((row*bytesPerRow)+col);
    }

    public Point locationToGrid(Location loc) {
      long offset = loc.getOffset();
      Point p = new Point();
      p.y = (int) (offset/bytesPerRow);
      p.x = (int) (offset%bytesPerRow);
      return p;
    }
  }

  ////////////////////////////////
  // GRID CURSOR
  private class LocalTextGridCursor extends TextGridCursor {
    private CharEditor parent;
    private Color insertColor = Color.BLACK;

    public LocalTextGridCursor(CharEditor parent) {
      this.parent = parent;
    }

    public void moveTo(int row, int column) {
      super.moveTo(row,column);    

      Location cLoc = localTextGridModel.gridToLocation(getCurrentRow(),getCurrentColumn());
      
      if (isMarkSet()) {
        Location mLoc = localTextGridModel.gridToLocation(getMarkedRow(),getMarkedColumn());
        if (mLoc.compareTo(cLoc) <= 0)
          setSelectionSpan(new ByteSpan(mLoc,cLoc));
        else
          setSelectionSpan(new ByteSpan(cLoc,mLoc));
      }
      else {
        setSelectionSpan(null);
      }

      fireBinaryEditorEvent( new BinaryEditorEvent(parent, document, cLoc, getSelectionSpan(), null,
                                                   BinaryEditorEvent.LOCATION_CHANGED) );        
    }

    public void moveTo(Location loc) {
      Location cLoc = localTextGridModel.gridToLocation(getCurrentRow(),getCurrentColumn());
      if (cLoc.compareTo(loc) != 0) {
        Point p = localTextGridModel.locationToGrid(loc);
        moveTo(p.y,p.x+1);
      }
    }
    
    public boolean isSelected(int row, int column) {
       ByteSpan span = getSelectionSpan();
       if (span != null) {
         Point p = localTextGridModel.locationToGrid(span.getStartLocation());
         if (p.x == column && p.y == row)
           return false;
         else       
           return span.contains(localTextGridModel.gridToLocation(row,column));
       }
       return false;
    }

    public void paint(Graphics g) {
      if (draw) {
        Rectangle rect = getCaretRect();
        g.setXORMode(insertColor);
        g.drawLine(rect.x, rect.y, rect.x, rect.y + rect.height - 1);
        g.setPaintMode();
      }
    }

    protected void processComponentKeyEvent(KeyEvent e) {
      super.processComponentKeyEvent(e);
      
      ByteSpan selection = getSelectionSpan();
      
      if (e.getID() == KeyEvent.KEY_PRESSED) {
        switch(e.getKeyCode()) {
          case KeyEvent.VK_BACK_SPACE:
            // FIXME: don't do this if there's a selection
            if (getCurrentColumn() > 0) {
              Location loc = localTextGridModel.gridToLocation(getCurrentRow(),getCurrentColumn()-1);
              getDocument().delete(loc, 1);
              left();
            } else if (getCurrentRow() > 0) {
              Location loc = localTextGridModel.gridToLocation(getCurrentRow()-1,getColumnCount()-1);
              getDocument().delete(loc, 1);
              left();
            }
            break;

          case KeyEvent.VK_DELETE:
            // FIXME: can't delete if end
            Location loc = localTextGridModel.gridToLocation(getCurrentRow(),getCurrentColumn());
            getDocument().delete(loc, 1);
            break;

          // User types a character
          default:
            char keyChar = e.getKeyChar();
            
            try {        
              // There is a selection
              if (selection != null && 
                  selection.contains(localTextGridModel.gridToLocation(getCurrentRow(),getCurrentColumn()))) {
                  /*
                char [] byteChars = new char[byteWidth];
                byteChars[0] = keyChar;
                for (int i=1; i<byteWidth; i++)
                   byteChars[i] = Integer.toString(0, radix).charAt(0);
                int byteValue = Integer.parseInt(new String(byteChars), radix);
                if (byteValue >=0 && byteValue <= 0xFF) {
                  moveTo(selection.getStartLocation());
                  getDocument().delete(selection.getStartLocation(), (int) selection.length());
                  getDocument().insert(selection.getStartLocation(), byteValue);
                  right();
                  right();
                  clearMark();
                  setSelectionSpan(null);
                } */
              }
              // No selection - Insert
              else if (keyChar != KeyEvent.CHAR_UNDEFINED) {
                int byteValue = (byte) keyChar;
                if (byteValue >=0 && byteValue <= 0xFF) {
                  getDocument().insert(localTextGridModel.gridToLocation(getCurrentRow(),getCurrentColumn()),byteValue);
                  right();
                }
              }
            }
            catch(NumberFormatException exception) {
            }
            e.consume();
            break;
        }
      }
    }
  }

  ////////////////////////////////
  // DOCUMENT OBSERVER
  private class LocalDocumentObserver implements Observer {
    public void update(Observable o, Object arg) {

      // The document has changed
      if (arg instanceof ContentChangedEvent) {
        ContentChangedEvent e = (ContentChangedEvent) arg;
        localTextGridModel.fireTextGridModelEvent(
          new TextGridModelEvent(localTextGridModel,
                                 TextGridModelEvent.FIRST_ROW,
                                 TextGridModelEvent.FIRST_COLUMN,
                                 TextGridModelEvent.LAST_ROW,
                                 TextGridModelEvent.LAST_COLUMN,
                                 TextGridModelEvent.UPDATE));
      }
    }
  }
}
