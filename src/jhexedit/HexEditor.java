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

public class HexEditor extends JPanel implements BinaryEditor, Scrollable {
        
  // CONSTANTS
  public static final int SPACER_WIDTH = 8;

  // MEMBERS
  protected TextGrid   addressComponent;
  protected ByteEditor hexEditor;
  protected CharEditor asciiEditor;
  
  private HexEditorListener hexEditorListener;
  private ASCIIEditorListener asciiEditorListener;
  
  protected JComponent spacer1;
  protected JComponent spacer2;
  
  /**
   * Construct the editor with a document.
   */
  public HexEditor(AnnotatedBinaryDocument document) {
    setBackground(Color.WHITE);
    setForeground(Color.BLACK);

    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    setLayout( gridbag );

    hexEditor     = new ByteEditor(document);
    asciiEditor   = new CharEditor(document);
    addressComponent = new TextGrid(new AddressTextGridModel());
    
    hexEditorListener = new HexEditorListener();
    asciiEditorListener = new ASCIIEditorListener();
    
    hexEditor.addBinaryEditorListener(hexEditorListener);
    asciiEditor.addBinaryEditorListener(asciiEditorListener);
    
    spacer1 = new JPanel();
    spacer2 = new JPanel();
    
    addressComponent.setBackground(getBackground());
    hexEditor.setBackground(getBackground());
    asciiEditor.setBackground(getBackground());
    
    spacer1.setBackground(getBackground());
    spacer1.setPreferredSize(new Dimension(SPACER_WIDTH, 1));
    spacer1.setMaximumSize(new Dimension(SPACER_WIDTH, 1));
    spacer1.setMinimumSize(new Dimension(SPACER_WIDTH, 1));
    spacer2.setBackground(getBackground());
    spacer2.setPreferredSize(new Dimension(SPACER_WIDTH, 1));
    spacer2.setMaximumSize(new Dimension(SPACER_WIDTH, 1));
    spacer2.setMinimumSize(new Dimension(SPACER_WIDTH, 1));

    gbc.gridx=0;
    gbc.gridy=0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    gridbag.setConstraints(addressComponent,gbc);
    add(addressComponent);

    gbc.gridx=1;
    gridbag.setConstraints(spacer1,gbc);
    add(spacer1);    
   
    gbc.gridx=2;
    gridbag.setConstraints(hexEditor,gbc);
    add(hexEditor);

    gbc.gridx=3;
    gridbag.setConstraints(spacer2,gbc);
    add(spacer2); 
    
    gbc.gridx=4;
    gbc.weightx = 1.0;
    gridbag.setConstraints(asciiEditor,gbc);
    add(asciiEditor);
  }
        
  public AnnotatedBinaryDocument getDocument() {
    return hexEditor.getDocument();
  }

  public void setDocument(AnnotatedBinaryDocument document) {
    hexEditor.setDocument(document);
    asciiEditor.setDocument(document);
  }
 
  public Location getCurrentLocation() {
    return hexEditor.getCurrentLocation();
  }

  public void setCurrentLocation(Location location) {
    hexEditor.setCurrentLocation(location);
    asciiEditor.setCurrentLocation(location);
  }  
  
  public ByteSpan getSelectionSpan() {
    return hexEditor.getSelectionSpan();
  }

  public void setSelectionSpan( ByteSpan selection ) {
    hexEditor.setSelectionSpan(selection);
    asciiEditor.setSelectionSpan(selection);
  }

  public void addBinaryEditorListener(BinaryEditorListener l) {
  }
  
  public void removeBinaryEditorListener(BinaryEditorListener l) {
  }

  public String toString() {
    AnnotatedBinaryDocument doc = getDocument();
    if (doc == null)
      return "No Document.";
    else if (doc.isNew()) 
      return "New Document";
    else 
      return doc.getFile().getName();
  }
 
  /////////////////////////
  // SCROLLABLE INTERFACE
  
  public Dimension getPreferredScrollableViewportSize() {
    return getPreferredSize();
  }
  
  public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
    return hexEditor.getScrollableUnitIncrement(visibleRect, orientation, direction);
  }
  
  public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
    return hexEditor.getScrollableBlockIncrement(visibleRect, orientation, direction);
  }
          
  public boolean getScrollableTracksViewportHeight() {
    return false;
  }
  
  public boolean getScrollableTracksViewportWidth() {
    return true;
  }

  ////////////////////////////////
  // ADDRESS COMPONENT GRID MODEL
  private class AddressTextGridModel implements TextGridModel {
    private int lastRowIndex = 0;
    private String lastRowText = null;
    private LinkedList listeners;
    
    public AddressTextGridModel() {
      listeners = new LinkedList();
    }
    
    public int getColumnCount() {
      return getRowText(0).length();
    }
    
    public int getRowCount() {
      return (int) hexEditor.getModel().getRowCount();
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
      return Color.WHITE;
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
      String maxAddress = Integer.toString(Integer.MAX_VALUE, 16);
      String result = Integer.toString(row * hexEditor.getBytesPerRow(),16);
      while (result.length() < maxAddress.length())
        result = Integer.toString(0,16) + result;
      return result + " ";
    }
  }

  //////////////////////////
  // Hex Editor Listener
  private class HexEditorListener implements BinaryEditorListener {
    private boolean enabled = true;
    public void enable()  { enabled = true; }
    public void disable() { enabled = false; }
    
    public void editorUpdated(BinaryEditorEvent e) {
      // Forward the event

      // Expand the addresses component if needed
      BinaryDocumentEvent bDocEvent = e.getDocumentEvent();
      if (bDocEvent != null && bDocEvent instanceof ContentChangedEvent) {
        ContentChangedEvent ccEvent = (ContentChangedEvent) bDocEvent;
        TextGridModelEvent gme = new TextGridModelEvent(addressComponent.getModel(),
                                     TextGridModelEvent.FIRST_ROW,
                                     TextGridModelEvent.FIRST_COLUMN,
                                     TextGridModelEvent.LAST_ROW,
                                     TextGridModelEvent.LAST_COLUMN,
                                     TextGridModelEvent.UPDATE);
        ((AddressTextGridModel) addressComponent.getModel()).fireTextGridModelEvent(gme);
      }
      
      // Synchronize the ascii editor
      if (!enabled) return;
      asciiEditorListener.disable();
      asciiEditor.setCurrentLocation(e.getCurrentLocation());
      asciiEditor.setSelectionSpan(e.getSelectionSpan());
      asciiEditorListener.enable();
    }
  }
  
  //////////////////////////
  // ASCII Editor Listener
  private class ASCIIEditorListener implements BinaryEditorListener {
    private boolean enabled = true;
    public void enable()  { enabled = true; }
    public void disable() { enabled = false; }
    
    public void editorUpdated(BinaryEditorEvent e) {
      if (!enabled) return;
      hexEditorListener.disable();
      hexEditor.setCurrentLocation(e.getCurrentLocation());
      hexEditor.setSelectionSpan(e.getSelectionSpan());
      hexEditorListener.enable();
    }
  }
}
