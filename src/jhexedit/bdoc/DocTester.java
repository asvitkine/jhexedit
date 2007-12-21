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

import java.io.*;
import java.util.*;

public class DocTester {

  private static BinaryDocument bDoc;      
  private static Cursor c;

  private static Position a;
  private static Position b;
  private static Position d;
  private static Position e;
  private static Position f;
  private static Position g;
  
  
  public static void main( String [] args ) {
    
    bDoc = new BinaryDocument();

    /*
    a = bDoc.getPosition(0);
    b = bDoc.getPosition(0);
    d = bDoc.getPosition(0);
    e = a;
    a = null;
    b = null;
    d = null;
    e = null;
   
    while(true) {
      System.gc();  
    }
    */
    
    if (args.length != 1) {
      System.out.println("SYNTAX: DocTester <filename>");
      System.exit(1);
    }
    
    try {
    bDoc = new BinaryDocument( new File(args[0]) );

    BufferedReader reader = new BufferedReader( new InputStreamReader(System.in) );
    System.out.println(":: d(elete), r(ead), w(rite), i(nsert)");    
    String line = reader.readLine();
    c = bDoc.createCursor(new Offset(bDoc,0));

    bDoc.addObserver( new LocalObserver() );
    
    a = bDoc.createPosition( 5 ); 
    b = bDoc.createPosition( 10 ); 
    d = bDoc.createPosition( 15 ); 
    e = bDoc.createPosition( 6 ); 
    f = bDoc.createPosition( 11 ); 
    g = bDoc.createPosition( 16 ); 
    
    while( line != null ) {
      
      if (line.startsWith("d")) {
        int num = 1;
        if (line.length() > 2) {
          try {
            num = Integer.parseInt(line.substring(2));
          } catch( Exception ex ) {
            System.out.println(ex);
            ex.printStackTrace();
          }
        }
        
        c.delete( num );
        System.out.println("DELETED [" + num + "]: ");        
        System.out.println("BUFFER: ");
        displayDocument();
        System.out.println(":: d(elete), r(ead), w(rite), i(nsert)");    
        System.out.println();
      }
      else if (line.startsWith("r")) {
        int num = 1;
        if (line.length() > 2) {
          try {
            num = Integer.parseInt(line.substring(2));
          } catch( Exception ex ) {
            System.out.println(ex);
            ex.printStackTrace();
          }
        }
        
        byte [] b = new byte[num];
        
        if (num == 1)
          b[0] = (byte) c.read();
        else
          c.read(b);
          
        System.out.println("READ [" + num + "]: ");
        System.out.println("  " + new String(b));
        System.out.println("  012345678901234567890123456789" );
        System.out.println("BUFFER: ");
        displayDocument();
        System.out.println(":: d(elete), r(ead), w(rite), i(nsert)");    
        System.out.println();
      } 
      else if (line.startsWith("w")) {
        if (line.length() < 3)
          return;

        String str = line.substring(2);
        if (str.length() == 1)
          c.write((int) str.charAt(0));
        else 
          c.write(str.getBytes());
        
        System.out.println("WROTE: " + str);
        System.out.println("BUFFER: ");
        displayDocument();
        System.out.println(":: d(elete), r(ead), w(rite), i(nsert)");    
        System.out.println();
      }
      else if (line.startsWith("i")) {
        if (line.length() < 3)
          return;

        String str = line.substring(2);
        if (str.length() == 1)
          c.insert((int) str.charAt(0));
        else 
          c.insert(str.getBytes());
        
        System.out.println("INSERTED: " + str);
        System.out.println("BUFFER: ");
        displayDocument();
        System.out.println(":: d(elete), r(ead), w(rite), i(nsert)");    
        System.out.println();
      }
      
      else {
        System.out.println("BUFFER: ");
        displayDocument();
        System.out.println(":: d(elete), r(ead), w(rite), i(nsert)");    
        System.out.println();
      }     
            
      line = reader.readLine();
    }
    
    bDoc.close();
    }
    catch( IOException e ) {
      System.out.println( e );
      e.printStackTrace();
    }
  }

  private static void displayDocument() {
    char [] ruler = new char[70];
    for (int i=0; i<ruler.length; i++)
      ruler[i] = ' ';
    ruler[ (int) a.getOffset() ] = 'a';
    ruler[ (int) b.getOffset() ] = 'b';
    ruler[ (int) d.getOffset() ] = 'd';
    ruler[ (int) e.getOffset() ] = 'e';
    ruler[ (int) f.getOffset() ] = 'f';
    ruler[ (int) g.getOffset() ] = 'g';
    ruler[ (int) c.getPosition().getOffset() ] = '*';
    ruler[ (int) c.getDocument().length() ] = '|';
    
    System.out.println(ruler);    
    
    Cursor c2 = bDoc.createCursor(new Offset(bDoc, 0));
    byte [] b = new byte [128];
    int     r = 0;
    
    try {
      r = c2.read(b);
      while( r != -1 ) {
        //for (int i=0; i<r; i++) {
        //  String hex = "0123456789ABCDEF";
        //  int high = (b[i] 0xF0) >> 4;
        //  int low  = b[i] & 0x0F;
        //}
        System.out.println(new String(b, 0, r)); 
        r = c2.read(b);
      }
    } 
    catch(IOException e) {
      System.out.println( e );
      e.printStackTrace();
    }
    
    //System.out.println();
    //bDoc.rawPrint();
    System.out.println("00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F");
  } 


  private static class LocalObserver implements Observer {
    public void update(Observable o, Object arg) {
      if (arg instanceof ContentChangedEvent ) {
        ContentChangedEvent e = (ContentChangedEvent) arg;
        if ( e.getType() == ContentChangedEvent.WRITTEN ) {
          System.out.println("WRITTEN: " + e.getSpan().getStartLocation().getOffset() + ", " +
                                           e.getSpan().getEndLocation().getOffset());
        }
        else if ( e.getType() == ContentChangedEvent.INSERTED ) {
          System.out.println("INSERTED: " + e.getSpan().getStartLocation().getOffset() + ", " +
                                            e.getSpan().getEndLocation().getOffset());
        }
        else if ( e.getType() == ContentChangedEvent.DELETED ) {
          System.out.println("DELETED: " + e.getSpan().getStartLocation().getOffset() + ", " +
                                          e.getSpan().getEndLocation().getOffset());
        }
      }
    }
  }

}
