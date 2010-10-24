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
package jhexedit;
import  javax.swing.*;
import  java.awt.Toolkit;
import  java.awt.event.*;

public class MainMenu extends JMenuBar {

  // PRIVATE MEMBER VARIABLES
  private JMenu fileMenu;
  private JMenu editMenu;
  private JMenu viewMenu;
  private JMenu helpMenu;

  private JMenuItem newMenuItem;
  private JMenuItem openMenuItem;
  private JMenuItem closeMenuItem;
  private JMenuItem saveMenuItem;
  private JMenuItem saveAsMenuItem;
  private JMenuItem exitMenuItem;

  private JMenuItem helpMenuItem;
  private JMenuItem aboutMenuItem;
  
  /**
   * Construct the default Main menu and install all the
   * default listeners.
   */
  public MainMenu() {
    int shortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

    // File Menu
    fileMenu = new JMenu("File");
    fileMenu.setMnemonic(KeyEvent.VK_F);

    newMenuItem = new JMenuItem(new NewAction());
    newMenuItem.setMnemonic(KeyEvent.VK_N);
    newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, shortcutKeyMask)); 
    
    openMenuItem = new JMenuItem(new OpenAction());
    openMenuItem.setMnemonic(KeyEvent.VK_O);
    openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, shortcutKeyMask)); 

    closeMenuItem = new JMenuItem(new CloseAction());
    closeMenuItem.setMnemonic(KeyEvent.VK_W);
    closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, shortcutKeyMask)); 

    saveMenuItem = new JMenuItem("Save");
    saveMenuItem.setMnemonic(KeyEvent.VK_S);
    saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, shortcutKeyMask)); 
    
    saveAsMenuItem = new JMenuItem("Save As");
    
    exitMenuItem = new JMenuItem(new ExitAction());
    exitMenuItem.setMnemonic(KeyEvent.VK_X);
    exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, shortcutKeyMask)); 
    
    fileMenu.add( newMenuItem );
    fileMenu.add( openMenuItem );
    fileMenu.addSeparator();
    fileMenu.add( closeMenuItem );
    fileMenu.add( saveMenuItem );
    fileMenu.add( saveAsMenuItem );

    boolean isMacOSX = System.getProperty("os.name").toLowerCase().contains("mac os x");
    if (!isMacOSX) {
        fileMenu.addSeparator();
        fileMenu.add( exitMenuItem );
    }

    editMenu = new JMenu("Edit");
    editMenu.setMnemonic(KeyEvent.VK_E);

    viewMenu = new JMenu("View");
    viewMenu.setMnemonic(KeyEvent.VK_V);

    helpMenu = new JMenu("Help");
    helpMenu.setMnemonic(KeyEvent.VK_H);

    aboutMenuItem = new JMenuItem("About", KeyEvent.VK_A);
    helpMenuItem = new JMenuItem("Help", KeyEvent.VK_H);
    helpMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));

    helpMenu.add(helpMenuItem);
    helpMenu.addSeparator();
    helpMenu.add(aboutMenuItem);

    add( fileMenu );
    add( editMenu );
    add( viewMenu );
    add( helpMenu );
  }

  //////////////////////////////////////////////////////////////
  // INNER CLASSES

  private class NewAction extends AbstractAction {
    public NewAction() {
      super("New");
    }
    public void actionPerformed(ActionEvent e) {
      ApplicationFrame.instance().newDocument();
    }
  }
  
  private class OpenAction extends AbstractAction {
    public OpenAction() {
      super("Open");
    }
    public void actionPerformed(ActionEvent e) {
      ApplicationFrame.instance().openDocument();
    }
  }

  private class CloseAction extends AbstractAction {
    public CloseAction() {
      super("Close");
    }
    public void actionPerformed(ActionEvent e) {
      ApplicationFrame.instance().closeDocument();
    }
  }

  private class ExitAction extends AbstractAction {
    public ExitAction() {
      super("Exit");
    }
    public void actionPerformed(ActionEvent e) {
      ApplicationFrame.instance().quit();
    }
  }

}
