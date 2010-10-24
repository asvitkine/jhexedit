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
import  java.awt.*;
import  java.awt.event.*;
import  javax.swing.*;
import  java.io.*;
import  java.util.*;

import jhexedit.bdoc.*;
import jhexedit.textgrid.*;

public class ApplicationFrame extends JFrame {

  // PRIVATE STATIC MEMBERS
  private static ApplicationFrame _instance;
        
  // PRIVATE MEMBERS
  private MainMenu      mainMenu;
  private JTabbedPane   documentTabs;
  private JPanel        statusPanel;
  private LinkedList    editors;
          
  private FileToolBar   fileToolBar;
  private BinaryOperationsToolBar binToolBar;

  /////////////////////////////////////////////////////
  // STATIC METHODS

  public static void main( String [] argv ) {
    System.setProperty("apple.laf.useScreenMenuBar", "true");

    ApplicationFrame frame = ApplicationFrame.instance();

    // Open all documents specified in the command line
    if (argv.length == 0) {
      frame.newDocument();
    }
    else {
      for(int i=0; i<argv.length; i++)
        frame.openDocument(new File(argv[i]));
    }

    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }  

  public static ApplicationFrame instance() {
    if (_instance == null) 
      _instance = new ApplicationFrame();
    return _instance;
  }  
  
  /////////////////////////////////////////////////////
  // INSTANCE METHODS

  
  protected ApplicationFrame() {
    super("JHexEdit");

    editors = new LinkedList();

    setSize(600,400);
    
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    getContentPane().setLayout( gridbag );
    
    mainMenu = new MainMenu();
    setJMenuBar( mainMenu );
    
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new AKDockLayout());

    fileToolBar = new FileToolBar();
    //mainPanel.add(fileToolBar, AKDockLayout.NORTH);

    binToolBar = new BinaryOperationsToolBar();
    //mainPanel.add(binToolBar, AKDockLayout.NORTH);

    documentTabs = new JTabbedPane();
    mainPanel.add(documentTabs, AKDockLayout.CENTER);

    statusPanel = new JPanel();
    statusPanel.setBorder(BorderFactory.createLoweredBevelBorder());
    statusPanel.add(new JLabel("Status..."));
    
    c.gridx=0;
    c.gridy=0;
    c.gridwidth = 1;
    c.gridheight = 1;
    c.weightx = 1.0;
    c.weighty = 1.0;
    c.fill = GridBagConstraints.BOTH;
    gridbag.setConstraints(mainPanel,c);
    getContentPane().add(mainPanel);
    
    c.gridx=0;
    c.gridy=1;
    c.gridwidth = 1;
    c.gridheight = 1;
    c.weightx = 0;
    c.weighty = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    gridbag.setConstraints(statusPanel,c);
    //getContentPane().add(statusPanel);

    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    addWindowListener(new LocalWindowListener());
  }

  public BinaryEditor openDocument() {
    JFileChooser chooser = new JFileChooser();
     
    chooser.setDialogType(JFileChooser.OPEN_DIALOG);
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    chooser.setMultiSelectionEnabled(true);

    int returnVal = chooser.showOpenDialog(this);
    if(returnVal == JFileChooser.APPROVE_OPTION) {
      File [] files = chooser.getSelectedFiles();
      if (files != null) {
        for (int i=0; i<files.length; i++) 
          openDocument(files[i]);
      }
    }
          
    return null;
  }
  
  public BinaryEditor openDocument(File file) {
    try {
      BinaryDocument doc = new AnnotatedBinaryDocument(file);
      HexEditor hexEditor = new HexEditor(doc);
      addEditor(hexEditor);
      return hexEditor;
    }
    catch(IOException e) {
      System.out.println(e);
      e.printStackTrace();
    }

    return null;
  }
  
  public BinaryEditor newDocument() {
    HexEditor e = new HexEditor(new AnnotatedBinaryDocument());
    addEditor(e);
    return e;
  }
	
  public void closeDocument() {
    Component component = documentTabs.getSelectedComponent();
    if (component instanceof JScrollPane) {
      Object view = ((JScrollPane) component).getViewport().getView();
      if (view instanceof BinaryEditor) {
        closeEditor((BinaryEditor) view);
      }
    }
  }

  public void addEditor(BinaryEditor e) {
    editors.add(e);
    documentTabs.insertTab(e.toString(), null, new JScrollPane((JComponent) e), null, documentTabs.getTabCount());
    documentTabs.setSelectedIndex(documentTabs.getTabCount()-1);    
  }

  public boolean closeEditor(BinaryEditor e) {
    BinaryDocument doc = e.getDocument();
    
    // Confirm the close if the document was modified
    if (doc != null && doc.isModified()) {
      int result = JOptionPane.showConfirmDialog(this, "Do you want to save changes to " + e.toString() + " before exiting?",
                                                 "The document has been modified.",
                                                 JOptionPane.YES_NO_CANCEL_OPTION);
      switch(result) {
        case JOptionPane.OK_OPTION:
          break;
        case JOptionPane.NO_OPTION:
          break;
        default:
          return false;
      }
    }
    
    // Remove the editor from the tabs      
    Component [] c = documentTabs.getComponents();
    if (c != null) {
      for (int i=0; i<c.length; i++) {
        if (c[i] instanceof JScrollPane && ((JScrollPane) c[i]).getViewport().getView() == e)
          documentTabs.remove(c[i]);
      } 
    }        

    editors.remove(e);
    return true;
  }

  /**
   * Quit the application.
   * Prompt the user to save modifications for all dirty documents.
   */
  public void quit() {
    while (editors.size() > 0) {
      if (!closeEditor((BinaryEditor) editors.getFirst())) return;
    }

    System.exit(0);
  }

  ////////////////////////////////////////////////////////////////////
  // INNER CLASSES

  private class LocalWindowListener extends WindowAdapter {
    public void windowClosing(WindowEvent e) {
      quit();
    }
  }

}
