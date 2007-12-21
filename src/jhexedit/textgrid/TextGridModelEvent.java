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

import java.util.EventObject;

public class TextGridModelEvent extends EventObject {

  public static final int FIRST_ROW    = -1;
  public static final int FIRST_COLUMN = -2;
  public static final int LAST_ROW     = -3;
  public static final int LAST_COLUMN  = -4;
  public static final int UPDATE       = 0;
  public static final int INSERT       = 1;
  public static final int DELETE       = 2;
  
  private int firstRow    = FIRST_ROW;
  private int lastRow     = LAST_ROW;
  private int firstColumn = FIRST_COLUMN;
  private int lastColumn  = LAST_COLUMN;
  private int type        = UPDATE;

  public TextGridModelEvent(TextGridModel source) {
    super(source);
  }

  public TextGridModelEvent(TextGridModel source, int firstRow, int firstColumn,
                            int lastRow, int lastColumn, int type ) {
    super(source);
    this.firstRow    = firstRow;
    this.firstColumn = firstColumn;
    this.lastRow     = lastRow;
    this.lastColumn  = lastColumn;
    this.type        = type; 
  }

  public int getFirstRow()    { return firstRow; }
  public int getFirstColumn() { return firstColumn; }
  public int getLastRow()     { return lastRow; }
  public int getLastColumn()  { return lastColumn; }
  public int getType()        { return type; }
  
}
