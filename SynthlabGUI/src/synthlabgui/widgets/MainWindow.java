package synthlabgui.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import synthlab.internal.Audio;
import synthlabgui.widgets.configPanel.ModuleConfigWindow;

public class MainWindow extends JFrame implements WindowListener
{
  private static final long   serialVersionUID = 1910008960712226631L;

  private JMenuBar            menuBar_;

  private ModuleRegistryPanel moduleRegistryPanel_;

  private ModulePoolPanel     modulePoolPanel_;

  private InformationPanel    informationPanel_;

  public MainWindow()
  {
    super();
    setupMenuBar();
    setupPanels();
    setupGeneral();
  }

  private void setupPanels()
  {
    moduleRegistryPanel_ = new ModuleRegistryPanel();
    modulePoolPanel_ = new ModulePoolPanel(this);
    informationPanel_ = new InformationPanel(new Oscilloscope(), new Spectrometer());
    JSplitPane splitter2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, informationPanel_, modulePoolPanel_);
    splitter2.setDividerSize(22);
    splitter2.setUI(new BasicSplitPaneUI() {
      @Override
      public BasicSplitPaneDivider createDefaultDivider()
      {
        return new VerticalDivider(this);
      }
    });
    JScrollPane scroll_ = new JScrollPane(moduleRegistryPanel_);
    scroll_.getVerticalScrollBar().setUnitIncrement(50);
    scroll_.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scroll_.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    scroll_.setBorder(null);
    JPanel temp = new JPanel();
    temp.setLayout(new BorderLayout());
    temp.add(scroll_.getVerticalScrollBar(), BorderLayout.WEST);
    temp.add(scroll_, BorderLayout.EAST);
    temp.setBackground(new Color(237, 237, 237));
    temp.setMaximumSize(new Dimension(20, Integer.MAX_VALUE));
    JSplitPane splitter1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, temp, splitter2);
    splitter1.setDividerLocation(250);
    splitter1.setEnabled(false);
    splitter1.setDividerSize(22);
    splitter1.setUI(new BasicSplitPaneUI() {
      @Override
      public BasicSplitPaneDivider createDefaultDivider()
      {
        return new HorizontalDivider(this);
      }
    });
    splitter2.validate();
    getContentPane().add(splitter1);
  }

  private void setupGeneral()
  {
    setTitle("Synthlab GUI");
    setSize(1200, 900);
    addWindowListener(this);
    setVisible(true);
  }

  private void setupMenuBar()
  {
    // --- Create menu bar
    menuBar_ = new JMenuBar();
    // --- Create menus
    JMenu menuFile = new JMenu("File");
    JMenu menuEdit = new JMenu("Edit");
    JMenu menuSynth = new JMenu("Synthetizer");
    JMenu menuHelp = new JMenu("Help");
    // --- Create menu items
    JMenuItem itemFileNew = new JMenuItem("New");
    JMenuItem itemFileQuit = new JMenuItem("Quit");
    JMenuItem itemEditCopy = new JMenuItem("Copy");
    JMenuItem itemEditCut = new JMenuItem("Cut");
    JMenuItem itemEditPaste = new JMenuItem("Paste");
    JMenuItem itemSynthAdd = new JMenuItem("Add");
    JMenuItem itemSynthRemove = new JMenuItem("Remove");
    JMenuItem itemHelpAbout = new JMenuItem("About");
    // --- Bind actions to menu items
    itemFileQuit.addActionListener(new QuitActionListener());
    itemHelpAbout.addActionListener(new AboutActionListener());
    itemFileNew.addActionListener(new NewActionListener());
    // --- Add menu items
    menuFile.add(itemFileNew);
    menuFile.add(itemFileQuit);
    menuEdit.add(itemEditCopy);
    menuEdit.add(itemEditCut);
    menuEdit.add(itemEditPaste);
    menuSynth.add(itemSynthAdd);
    menuSynth.add(itemSynthRemove);
    menuHelp.add(itemHelpAbout);
    // --- Add menus
    menuBar_.add(menuFile);
    menuBar_.add(menuHelp);
    // --- Add menu bar
    setJMenuBar(menuBar_);
  }

  public void closeWindow()
  {
    Audio.stopLine();
    Audio.closeLine();
    System.exit(0);
  }

  public InformationPanel getInformationPanel()
  {
    return informationPanel_;
  }

  public ModuleRegistryPanel getRegistryPanel()
  {
    return moduleRegistryPanel_;
  }

  public ModulePoolPanel getPoolPanel()
  {
    return modulePoolPanel_;
  }

  // ====================================================================
  // ACTION LISTENERS
  // --------------------------------------------------------------------
  // These listeners will use the public MainWindow interface to handle
  // the incoming events. Same for window events & all. This allow us to
  // share actions between events.
  // ====================================================================
  private class QuitActionListener implements ActionListener
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      closeWindow();
    }
  }

  private class AboutActionListener implements ActionListener
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      new AboutView();
    }
  }

  private class NewActionListener implements ActionListener
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      setupPanels();
    }
  }

  private void hideWindows()
  {
    for (Component com : modulePoolPanel_.getComponents())
    {
      ModuleConfigWindow window = ((Module) com).getConfigWindow();
      System.out.println("config:" + window.isActive());
      window.unshow(false);
    }
  }

  private void recoverWindows()
  {
    for (Component com : modulePoolPanel_.getComponents())
    {
      ModuleConfigWindow window = ((Module) com).getConfigWindow();
      // window.setAlwaysOnTop(true);
      if (window.isShowing())
        window.show(window.getLocation());
    }
  }

  // ====================================================================
  // WINDOW LISTENERS
  // --------------------------------------------------------------------
  // These listeners will use the public MainWindow interface to handle
  // the incoming events. Same for menu items & all. This allow us to
  // share actions between events.
  // ====================================================================
  @Override
  public void windowClosed(WindowEvent e)
  {
  }

  @Override
  public void windowClosing(WindowEvent e)
  {
    closeWindow();
  }

  @Override
  public void windowDeactivated(WindowEvent e)
  {
  }

  @Override
  public void windowActivated(WindowEvent e)
  {
  }

  @Override
  public void windowDeiconified(WindowEvent e)
  {
    recoverWindows();
  }

  @Override
  public void windowIconified(WindowEvent e)
  {
    hideWindows();
  }

  @Override
  public void windowOpened(WindowEvent e)
  {
  }
}
