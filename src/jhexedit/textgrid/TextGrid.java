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

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.LinkedList;
import java.util.Iterator;

public class TextGrid extends JComponent implements TextGridModelListener, Scrollable {

  // CONSTANTS
  public static final Font PLAIN_FONT       = new Font("Monospaced", Font.PLAIN,  11);
  public static final Font BOLD_FONT        = new Font("Monospaced", Font.BOLD,   11);
  public static final Font ITALIC_FONT      = new Font("Monospaced", Font.ITALIC, 11);
  public static final Font BOLD_ITALIC_FONT = new Font("Monospaced", Font.BOLD|Font.ITALIC, 11);
  
  public static final int PLAIN                = 0;
  public static final int BOLD                 = 1;
  public static final int ITALIC               = 2;
  public static final int STRIKETHROUGH        = 4;
  public static final int UNDERLINE            = 8;
  public static final int UNDERLINE_LOW        = 16;
  public static final int UNDERLINE_LOW_DASHED = 32;
  public static final int UNDERLINE_LOW_DOTTED = 64;
  public static final int UNDERLINE_LOW_DOUBLE = 128;

  // MEMBERS  
  protected int charHeight;
  protected int charWidth;
  protected int charDescent;
  protected int leftMargin;
  protected int topMargin;
  
  protected TextGridModel model;
  protected TextGridCursor cursor;

  /**
   * Construct the editor with a document.
   */
  public TextGrid(TextGridModel model) {
    charHeight  = getFontMetrics(PLAIN_FONT).getHeight()-1;  // TODO: Fogure out why this works better. 
    charWidth   = getFontMetrics(PLAIN_FONT).charWidth('0'); // Assume fixed width!
    charDescent = getFontMetrics(PLAIN_FONT).getDescent();

    setModel(model);

    topMargin  = 2;
    leftMargin = 2;
    
    setOpaque(true);
    setFocusable(true);
    setAutoscrolls(true);
  }
  
  // MODEL STUFF
   
  public TextGridModel getModel() {
    return model;
  }

  public void setModel(TextGridModel model) {
    if (model != null)
      model.removeTextGridModelListener(this);
          
    this.model = model;

    if (model != null)
      model.addTextGridModelListener(this);
    
    /*
    Dimension d = new Dimension( leftMargin + model.getColumnCount()*charWidth,
                                 topMargin + model.getRowCount()*charHeight);

    setPreferredSize(d);
    setMinimumSize(d);
    */

    revalidate();
    repaint();
  }

  // model can change its column count, so this is now dynamic
  public Dimension getPreferredSize() {
    return new Dimension(leftMargin + model.getColumnCount()*charWidth,
                         topMargin + model.getRowCount()*charHeight);
  }
  
  // CURSOR STUFF

  public TextGridCursor getTextGridCursor(TextGridCursor cursor) {
    return this.cursor;
  }
  
  public void setTextGridCursor(TextGridCursor cursor) {
    this.cursor = cursor;
    cursor.install(this);
  }
  
  // DIMENSION STUFF  
  
  /**
   * Get the row count.
   */
  public int getRowCount() {
    return model.getRowCount();
  }

  /**
   * Get the column count.
   */
  public int getColumnCount() {
    return model.getColumnCount();
  }

  
  // VIEW - MODEL STUFF
  
  /**
   * Convert a screen point to row and column position.
   */
  public Point viewToModel(Point p) {
    int row = (p.y-topMargin) / charHeight;
    int col = (p.x-leftMargin) / charWidth;
    row = row < 0 ? 0 : row;
    row = row >= getRowCount() ? getRowCount() - 1 : row;
    
    col = col < 0 ? 0 : col;
    col = col >= getColumnCount() ? getColumnCount() - 1 : col;
    
    return new Point(col,row);
  }

  /**
   * Convert a row/column to a rectangle on the screen. 
   */
  public Rectangle modelToView(int row, int col) {
    return new Rectangle(col * charWidth +leftMargin, row * charHeight + topMargin, charWidth, charHeight);
  }
  
  /**
   * Get the character at a particular locaiton.
   */
  public char getCharAt(int row, int col) {
    return model.getCharAt(row, col);
  }

  /**
   * Get a character's fg colour.
   */
  public Color getCharColor(int row, int col) {
    return model.getCharColor(row, col);
  }
  
  /**
   * Get a character's bg colour.
   */
  public Color getCharBackground(int row, int col) {
    return model.getCharBackground(row, col);
  }

  /**
   * Get a characters style.
   */
  public int getCharStyle(int row, int col) {
    return model.getCharStyle(row, col);
  }

  // TEXT GRID MODEL LISTENER INTERFACE
  public void textGridUpdated(TextGridModelEvent e) {
    Dimension d = new Dimension( leftMargin + model.getColumnCount()*charWidth,
                                 topMargin + model.getRowCount()*charHeight);
    setPreferredSize(d);

    revalidate();
    repaint();
  }

  // SCROLLABLE INTERFACE
  public Dimension getPreferredScrollableViewportSize() {
    return getPreferredSize();
  }
  
  public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
    int result = 0;
    if (orientation == SwingConstants.VERTICAL) {
      if (direction < 0)
         result = (visibleRect.y - topMargin)%charHeight;
      else 
         result = charHeight - (visibleRect.y - topMargin)%charHeight;   

      if (result <= 0) result = charHeight; 
    }
    else if (orientation == SwingConstants.HORIZONTAL) {
      if (direction < 0)
        result = (visibleRect.x - leftMargin)%charWidth;
      else 
        result = charWidth - (visibleRect.x - leftMargin)%charWidth;  

      if (result <= 0) result = charWidth; 
    }

    return result;
  }
  
  public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
    int result = 0;

    if (orientation == SwingConstants.VERTICAL) {
      result = visibleRect.height - charHeight;  
      result += getScrollableUnitIncrement(new Rectangle(visibleRect.x, 
                                                         result + visibleRect.y,
                                                         visibleRect.width,
                                                         visibleRect.height), orientation, direction);
    }
    else if (orientation == SwingConstants.HORIZONTAL) {
      result = visibleRect.width - charWidth;  
      result += getScrollableUnitIncrement(new Rectangle(result + visibleRect.x, 
                                                         visibleRect.y,
                                                         visibleRect.width,
                                                         visibleRect.height), orientation, direction);
    }

    return result;
  }
          
  public boolean getScrollableTracksViewportHeight() {
    return false;
  }
  
  public boolean getScrollableTracksViewportWidth() {
    return false;
  }
   
  /**
   * Paint the component.
   */
  protected void paintComponent(Graphics g)  {
    Graphics2D g2d = (Graphics2D) g;

    if (isOpaque()) {
      g2d.setColor(getBackground());
      g2d.fill(g2d.getClip());
    }

    Rectangle bounds = g2d.getClipBounds();

    //g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
    //                     RenderingHints.VALUE_ANTIALIAS_ON);
  
    Point minRowCol = viewToModel(new Point(bounds.x,bounds.y));
    Point maxRowCol = viewToModel(new Point(bounds.x+bounds.width,bounds.y+bounds.height));
    
    String line;
    
    Rectangle rect;    
    Color bgColor;
    Color fgColor;
    int style;
    int i,j;

    // Draw the text layer
    for (i=minRowCol.y; i<=maxRowCol.y; i++) {
      int lastStyle = 0;
      int lastStyleIdx = 0;
      
      Color lastBg = getBackground(); 
      int lastBgIdx = 0;

      Color lastFg = getForeground();
      int lastFgIdx = 0;
      
      rect = modelToView(i,0);
      int baseLine = rect.y + rect.height - charDescent;
      
      AttributedString as = new AttributedString(getRowText(i));
      as.addAttribute(TextAttribute.FONT, PLAIN_FONT);
      
      for (j=minRowCol.x; j<=maxRowCol.x; j++) {
        if (cursor != null && cursor.isSelectionVisible() && cursor.isSelected(i,j)) {
          bgColor = cursor.getSelectionColor();
          fgColor = cursor.getSelectedTextColor();
        }
        else {
          bgColor = model.getCharBackground(i,j);
          fgColor = model.getCharColor(i,j);
        }
        
        if (!lastFg.equals(fgColor)) {         
          if (j>0)
            as.addAttribute(TextAttribute.FOREGROUND, lastFg, lastFgIdx, j);
          lastFg = fgColor;
          lastFgIdx = j;
        }

        if (!lastBg.equals(bgColor)) { 
          if (j>0)        
            as.addAttribute(TextAttribute.BACKGROUND, lastBg, lastBgIdx, j);
          lastBg = bgColor;
          lastBgIdx = j;
        }

        style = model.getCharStyle(i,j);
        
        if ( lastStyle != style ) {   
          if (lastStyle > 0) {    
            if ( (lastStyle & BOLD) > 0 && (lastStyle & ITALIC) > 0 )
              as.addAttribute(TextAttribute.FONT, BOLD_ITALIC_FONT, lastStyleIdx, j); 
            else if ( (lastStyle & ITALIC) > 0 )
              as.addAttribute(TextAttribute.FONT, ITALIC_FONT, lastStyleIdx, j); 
            else if ( (lastStyle & BOLD) > 0 )
              as.addAttribute(TextAttribute.FONT, BOLD_FONT, lastStyleIdx, j); 
        
            if ( (lastStyle & STRIKETHROUGH) > 0 )
              as.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON, lastStyleIdx, j); 
        
            if ( (lastStyle & UNDERLINE) > 0 )
              as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, lastStyleIdx, j);        
            else if ( (lastStyle & UNDERLINE_LOW) > 0 )
              as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL, lastStyleIdx, j);  
            else if ( (lastStyle & UNDERLINE_LOW_DASHED) > 0 )
              as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_DASHED, lastStyleIdx, j); 
            else if ( (lastStyle & UNDERLINE_LOW_DOTTED) > 0 )
              as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_DOTTED, lastStyleIdx, j); 
            else if ( (lastStyle & UNDERLINE_LOW_DOUBLE) > 0 )
              as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_TWO_PIXEL, lastStyleIdx, j);
          }
          
          lastStyle = style;
          lastStyleIdx = j;
        } 
      }

      // Apply remaining color changes
      as.addAttribute(TextAttribute.FOREGROUND, lastFg, lastFgIdx, j);
      as.addAttribute(TextAttribute.BACKGROUND, lastBg, lastBgIdx, j);
      
      // Apply remaining style change 
      if (lastStyle > 0) {                  
        if ( (lastStyle & BOLD) > 0 && (lastStyle & ITALIC) > 0 )
          as.addAttribute(TextAttribute.FONT, BOLD_ITALIC_FONT, lastStyleIdx, j); 
        else if ( (lastStyle & ITALIC) > 0 )
          as.addAttribute(TextAttribute.FONT, ITALIC_FONT, lastStyleIdx, j); 
        else if ( (lastStyle & BOLD) > 0 )
          as.addAttribute(TextAttribute.FONT, BOLD_FONT, lastStyleIdx, j); 
        
        if ( (lastStyle & STRIKETHROUGH) > 0 )
          as.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON, lastStyleIdx, j); 
        
        if ( (lastStyle & UNDERLINE) > 0 )
          as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, lastStyleIdx, j);        
        else if ( (lastStyle & UNDERLINE_LOW) > 0 )
          as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL, lastStyleIdx, j);  
        else if ( (lastStyle & UNDERLINE_LOW_DASHED) > 0 )
          as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_DASHED, lastStyleIdx, j); 
        else if ( (lastStyle & UNDERLINE_LOW_DOTTED) > 0 )
          as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_DOTTED, lastStyleIdx, j); 
        else if ( (lastStyle & UNDERLINE_LOW_DOUBLE) > 0 )
          as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_TWO_PIXEL, lastStyleIdx, j);
      }
        
      g2d.drawString(as.getIterator(), leftMargin, baseLine);    
    }
    
    // Draw the caret
    if (cursor != null && shouldDrawCursor())
      cursor.paint(g2d);
  }

  protected boolean shouldDrawCursor() {
    return hasFocus();
  }

  //////////////////////////
  // PROTECTED METHODS
  
  private String getRowText(int row) {
    char [] chars = new char[getColumnCount()];
    for (int j=0; j<chars.length; j++)
      chars[j] = getCharAt(row,j);
    return new String(chars);
  }
}
