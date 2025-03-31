package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import log.Logger;


public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);

        setContentPane(desktopPane);
        
        
        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }
    
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }
    
    /*protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        //Set up the lone menu.
        JMenu menu = new JMenu("Document");
        menu.setMnemonic(KeyEvent.VK_D);
        menuBar.add(menu);

        //Set up the first menu item.
        JMenuItem menuItem = new JMenuItem("New");
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("new");
//        menuItem.addActionListener(this);
        menu.add(menuItem);

        //Set up the second menu item.
        menuItem = new JMenuItem("Quit");
        menuItem.setMnemonic(KeyEvent.VK_Q);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("quit");
//        menuItem.addActionListener(this);
        menu.add(menuItem);

        return menuBar;
    }*/

    private static class MenuBuilder {
        private final JMenuBar menuBar;
        private final MainApplicationFrame frame;

        public MenuBuilder(MainApplicationFrame frame) {
            this.menuBar = new JMenuBar();
            this.frame = frame;
        }

        public JMenuBar build() {
            addLookAndFeelMenu();
            addTestMenu();
            return menuBar;
        }

        private void addLookAndFeelMenu() {
            JMenu menu = new JMenu("Режим отображения");
            menu.setMnemonic(KeyEvent.VK_V);
            menu.getAccessibleContext().setAccessibleDescription("Управление режимом отображения приложения");

            addLookAndFeelMenuItem(menu, "Системная схема", UIManager.getSystemLookAndFeelClassName());
            menu.addSeparator();
            addLookAndFeelMenuItem(menu, "Универсальная схема", UIManager.getCrossPlatformLookAndFeelClassName());
            menu.addSeparator();
            addLookAndFeelMenuItem(menu, "Крутая схема ;)", "javax.swing.plaf.nimbus.NimbusLookAndFeel");

            menuBar.add(menu);
        }

        private void addLookAndFeelMenuItem(JMenu parentMenu, String text, String lookAndFeel) {
            JMenuItem item = new JMenuItem(text, KeyEvent.VK_S);
            item.addActionListener((event) -> {
                frame.setLookAndFeel(lookAndFeel);
                frame.invalidate();
            });
            parentMenu.add(item);
        }

        private void addTestMenu() {
            JMenu menu = new JMenu("Тесты");
            menu.setMnemonic(KeyEvent.VK_T);
            menu.getAccessibleContext().setAccessibleDescription("Тестовые команды");

            addTestMenuItem(menu, "Сообщение в лог", () -> Logger.debug("Новая строка"));
            menu.addSeparator();
            addTestMenuItem(menu, "Выход", () -> System.exit(0));

            menuBar.add(menu);
        }

        private void addTestMenuItem(JMenu parentMenu, String text, Runnable action) {
            JMenuItem item = new JMenuItem(text, KeyEvent.VK_S);
            item.addActionListener((event) -> action.run());
            parentMenu.add(item);
        }
    }

    private JMenuBar generateMenuBar() {
        return new MenuBuilder(this).build();
    }
    
    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
}
