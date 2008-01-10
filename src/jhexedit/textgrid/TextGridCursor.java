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

package jhexedit.textgrid;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Iterator;
import javax.swing.Timer;

public class TextGridCursor {

  public static final int DEFAULT_BLINK_RATE = 500;      
  public static final Color DEFAULT_SELECTION_COLOR = new Color(200, 221, 241);
  public static final Color DEFAULT_SELECTED_TEXT_COLOR = Color.BLACK;
        
  private int row;
  private int column;
  private int markedRow;
  private int markedColumn;

  private int blinkRate = DEFAULT_BLINK_RATE;

  private boolean visible;
  private boolean selectionVisible;

  private javax.swing.Timer drawTimer;
  protected boolean draw; 
 
  private Color selectionColor = DEFAULT_SELECTION_COLOR;
  private Color selectedTextColor = DEFAULT_SELECTED_TEXT_COLOR;
  
  private TextGrid textGrid;
  private LocalMouseListener ml;
  private LocalKeyListener kl;
  
  private LinkedList listeners;
  
  public TextGridCursor() {
    uninstall();

    listeners = new LinkedList();
  }

  public void install(TextGrid textGrid) {
    if (textGrid != null) uninstall();
    
    this.textGrid = textGrid;
    row = 0;
    column = 0;
    markedRow = -1;
    markedColumn = -1;
    
    drawTimer = new javax.swing.Timer(blinkRate, new DrawToggle());
    drawTimer.setRepeats(true);
    drawTimer.start();

    visible = true;
    selectionVisible = true;

    ml = new LocalMouseListener();
    textGrid.addMouseListener(ml);
    textGrid.addMouseMotionListener(ml);

    kl = new LocalKeyListener();
    textGrid.addKeyListener(kl);
  }

  public void uninstall() {
    this.textGrid = null;
    row = -1;
    column = -1;
    markedRow = -1;
    markedColumn = -1;

    if (textGrid != null) {
      textGrid.removeMouseListener(ml);
      textGrid.removeMouseMotionListener(ml);   
      textGrid.removeKeyListener(kl);  
    }
  }

  public TextGrid getTextGrid() {
    return textGrid;
  }

  public void addTextGridCursorListener(TextGridCursorListener l) {
    listeners.add(l);
  }

  public void removeTextGridCursorListener(TextGridCursorListener l) {
    listeners.remove(l);
  }

  protected void fireTextGridCursorEvent(TextGridCursorEvent e) {
    Iterator i = listeners.iterator();
    while(i.hasNext()) {
      ((TextGridCursorListener) i.next()).cursorUpdated(e);
    }
  }
  
  public void left() {
    if (getCurrentRow() == 0 && getCurrentColumn() == 0)
      return;
    moveTo(getCurrentRow(), getCurrentColumn() - 1);
  }
  
  public void right() {
    moveTo(getCurrentRow(), getCurrentColumn() + 1);
  }

  public void up() {
    moveTo(getCurrentRow() - 1, getCurrentColumn());
  }

  public void down() {
    moveTo(getCurrentRow() + 1, getCurrentColumn());
  }

  public void moveTo(int row, int column) {

    if (column >= textGrid.getColumnCount()) {
      column = 0;
      row ++;
    }
    else if (column < 0) {
      column = textGrid.getColumnCount()-1;
      row --;
    }
    
    row = row < 0 ? 0 : row; 
    row = row >= textGrid.getRowCount() ? textGrid.getRowCount() - 1 : row;
   
    // Erase the old caret
    if (!isMarkSet()) {
      draw = false;
      textGrid.repaint(getCaretRect());
    }
    
    // Move the cursor
    this.row = row;
    this.column = column; 

    // Scroll if necessary
    textGrid.scrollRectToVisible(getCaretRect());

    // Draw the new caret
    if (!isMarkSet()) {
      draw = true;
      textGrid.repaint(getCaretRect());
    }
    else {
      textGrid.repaint();
    }
      
    // Continue flashing
    drawTimer.restart();

    // Notify listeners
    fireTextGridCursorEvent(new TextGridCursorEvent(this, getCurrentRow(), getCurrentColumn(), markedRow, markedColumn, 
                                TextGridCursorEvent.CURSOR_MOVED));
  }

  protected Rectangle getCaretRect() {
    Rectangle r = textGrid.modelToView(this.row, this.column);
    r.y--;
    return r;
  }

  public void setMark() {
    if (!isMarkSet()) { 
      markedRow = getCurrentRow();
      markedColumn = getCurrentColumn();
      textGrid.repaint();
      
      fireTextGridCursorEvent(new TextGridCursorEvent(this, getCurrentRow(), getCurrentColumn(), markedRow, markedColumn, 
                                  TextGridCursorEvent.MARK_SET));
    }
  }

  public void clearMark() {
    if (isMarkSet()) { 
      markedRow    = -1;
      markedColumn = -1;
      textGrid.repaint();
     
      fireTextGridCursorEvent(new TextGridCursorEvent(this, getCurrentRow(), getCurrentColumn(), markedRow, markedColumn, 
                                  TextGridCursorEvent.MARK_CLEARED));
    }
  }

  public boolean isMarkSet() {
    return markedRow >= 0 && markedColumn >=0;
  }

  public int getCurrentRow() {
    return row;
  }

  public int getCurrentColumn() {
    return column;
  }

  public int getMarkedRow() {
    return markedRow;
  }

  public int getMarkedColumn() {
    return markedColumn;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible; 
  }

  public boolean isSelectionVisible() {
    return selectionVisible;
  }

  public void setSelectionVisible(boolean selectionVisible) {
    this.selectionVisible = selectionVisible; 
  }

  public Color getSelectionColor() {
    return selectionColor;
  }

  public Color getSelectedTextColor() {
    return selectedTextColor;
  }

  public boolean isSelected(int row, int column) {
    if (!isMarkSet())
      return false;
    
    int start   = (getCurrentRow()*textGrid.getColumnCount())+getCurrentColumn();
    int end     = (markedRow*textGrid.getColumnCount())+markedColumn;
    int current = (getCurrentRow()*textGrid.getColumnCount())+getCurrentColumn();

    if (start > end) 
      return end <= current && current <= start;
    else        
      return start <= current && current <= end;
  }

  public int getBlinkRate() {
    return blinkRate;
  }

  public void setBlinkRate(int blinkRate) {
    this.blinkRate = blinkRate;
    if (drawTimer != null) drawTimer.setDelay(blinkRate);
  }

  public void paint(Graphics g) {
    if (draw) {
      Rectangle rect = getCaretRect();
      g.setXORMode(Color.BLACK);
      g.fillRect(rect.x, rect.y, rect.width, rect.height);
      g.setPaintMode();
    }
  }

  //////////////////////////
  // PROTECTED METHODS

  protected void processComponentMouseEvent(MouseEvent e) {     
    if (e.getID() == MouseEvent.MOUSE_PRESSED) {
      textGrid.requestFocusInWindow(); 
            
      if (e.isShiftDown() || e.isControlDown()) {
        if (!isMarkSet()) setMark();
      }
      else
        clearMark();
      
      Point p = textGrid.viewToModel(e.getPoint());
      moveTo(p.y,p.x);
    }
  }

  protected void processComponentMouseMotionEvent(MouseEvent e) {
    if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
      Point p = textGrid.viewToModel(e.getPoint());
      if (!isMarkSet()) setMark();
      moveTo(p.y,p.x);
    }
  }

  protected void processComponentKeyEvent(KeyEvent e) {
    if (e.getID() == KeyEvent.KEY_PRESSED) {
      switch(e.getKeyCode()) {
        case KeyEvent.VK_LEFT:
          
          if (e.isShiftDown() || e.isControlDown()) {
            if (!isMarkSet()) setMark();
          }
          else
            clearMark();
          
          left();
          e.consume();
          break;
        case KeyEvent.VK_RIGHT:
          if (e.isShiftDown() || e.isControlDown()) {
            if (!isMarkSet()) setMark();
          }
          else
            clearMark();
          
          right();
          e.consume();
          break;
        case KeyEvent.VK_UP:
          if (e.isShiftDown() || e.isControlDown()) {
            if (!isMarkSet()) setMark();
          }
          else
            clearMark();
          
          up();
          e.consume();
          break;
        case KeyEvent.VK_DOWN:
          if (e.isShiftDown() || e.isControlDown()) {
            if (!isMarkSet()) setMark();
          }
          else
            clearMark();
          
          down();
          e.consume();
          break;
      }
    }
  } 

  //////////////////////////
  // INNER CLASSES

  private class DrawToggle implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      draw = !draw;
      textGrid.repaint(getCaretRect());
    }
  }

  private class LocalMouseListener implements MouseListener, MouseMotionListener  {
    public void mouseClicked(MouseEvent e)  { processComponentMouseEvent(e); }
    public void mouseDragged(MouseEvent e)  { processComponentMouseMotionEvent(e); }
    public void mouseEntered(MouseEvent e)  { processComponentMouseEvent(e); }
    public void mouseExited(MouseEvent e)   { processComponentMouseEvent(e); }
    public void mouseMoved(MouseEvent e)    { processComponentMouseMotionEvent(e); }
    public void mousePressed(MouseEvent e)  { processComponentMouseEvent(e); }
    public void mouseReleased(MouseEvent e) { processComponentMouseEvent(e); }
  }

  private class LocalKeyListener implements KeyListener {
    public void keyPressed(KeyEvent e)  { processComponentKeyEvent(e); }
    public void keyReleased(KeyEvent e) { processComponentKeyEvent(e); }
    public void keyTyped(KeyEvent e)    { processComponentKeyEvent(e); }
  }
}
