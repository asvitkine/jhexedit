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
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Method;

public class MainMenu extends JMenuBar {

  private static boolean isMacOSX = System.getProperty("os.name").toLowerCase().contains("mac os x");

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
    openMenuItem = new JMenuItem(new OpenAction());
    closeMenuItem = new JMenuItem(new CloseAction());
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

    if (!isMacOSX) {
      fileMenu.addSeparator();
      fileMenu.add( exitMenuItem );
    }

    editMenu = new JMenu("Edit");
    editMenu.setMnemonic(KeyEvent.VK_E);
    editMenu.add(new JMenuItem(new CutAction()));
    editMenu.add(new JMenuItem(new CopyAction()));
    editMenu.add(new JMenuItem(new PasteAction()));

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
      if (!isMacOSX)
        putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
      putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }
    public void actionPerformed(ActionEvent e) {
      ApplicationFrame.instance().newDocument();
    }
  }
  
  private class OpenAction extends AbstractAction {
    public OpenAction() {
      super("Open");
      if (!isMacOSX)
        putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
      putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }
    public void actionPerformed(ActionEvent e) {
      ApplicationFrame.instance().openDocument();
    }
  }

  private class CloseAction extends AbstractAction {
    public CloseAction() {
      super("Close");
      if (!isMacOSX)
        putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_W));
      putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
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

  private class CutAction extends DefaultEditorKit.CutAction {
    public CutAction() {
      putValue(NAME, "Cut");
      if (!isMacOSX)
        putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_T));
      putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    public void actionPerformed(ActionEvent event) {
      Component c = ApplicationFrame.instance().getFocusOwner();
      if (c != null) {
        try {
	  c.getClass().getMethod("cut").invoke(c);
        } catch (Exception e) { }
      }
    }
  }

  private class CopyAction extends DefaultEditorKit.CopyAction {
    public CopyAction() {
      putValue(NAME, "Copy");
      if (!isMacOSX)
        putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
      putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    public void actionPerformed(ActionEvent event) {
      Component c = ApplicationFrame.instance().getFocusOwner();
      if (c != null) {
        try {
	  c.getClass().getMethod("copy").invoke(c);
        } catch (Exception e) { }
      }
    }
  }

  private class PasteAction extends DefaultEditorKit.PasteAction {
    public PasteAction() {
      putValue(NAME, "Paste");
      if (!isMacOSX)
        putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_P));
      putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    public void actionPerformed(ActionEvent event) {
      Component c = ApplicationFrame.instance().getFocusOwner();
      if (c != null) {
        try {
	  c.getClass().getMethod("paste").invoke(c);
        } catch (Exception e) { }
      }
    }
  }

}
