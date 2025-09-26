package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import log.Logger;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final List<JInternalFrame> internalFrames = new ArrayList<>();

    public MainApplicationFrame() {

        WindowConfig.WindowState savedState = WindowConfig.loadWindowState();

        if (savedState != null && !savedState.mainMaximized) {
            setBounds(savedState.mainX, savedState.mainY,
                    savedState.mainWidth, savedState.mainHeight);
        }

        else {
            int inset = 50;
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setBounds(inset, inset,
                    screenSize.width  - inset*2,
                    screenSize.height - inset*2);
        }

        setContentPane(desktopPane);
        
        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);

        if (savedState != null) {
            restoreInternalFramesState(savedState);
        }

        if (savedState != null && savedState.mainMaximized) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        setJMenuBar(generateMenuBar());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
        internalFrames.add(frame);
    }

    private void restoreInternalFramesState(WindowConfig.WindowState state) {
        if (state.internalFrames != null) {
            for (int i = 0; i < Math.min(state.internalFrames.size(), internalFrames.size()); i++) {
                WindowConfig.InternalFrameState frameState = state.internalFrames.get(i);
                JInternalFrame frame = internalFrames.get(i);

                frame.setLocation(frameState.x, frameState.y);
                frame.setSize(frameState.width, frameState.height);

                try {
                    if (frameState.icon) {
                        frame.setIcon(true);
                    }
                    if (frameState.maximized) {
                        frame.setMaximum(true);
                    }
                } catch (Exception e) {
                    Logger.debug("Ошибка при восстановлении состояния окна: " + e.getMessage());
                }
            }
        }
    }

    private void exitApplication() {
        if (confirmExit()) {
            WindowConfig.saveWindowState(this, internalFrames);
            System.exit(0);
        }
    }

    private boolean confirmExit() {
        Object[] options = {"Да", "Нет"};

        int result = JOptionPane.showOptionDialog(
                this,
                "Вы действительно хотите выйти?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]
        );

        return result == JOptionPane.YES_OPTION;
    }

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
            addTestMenuItem(menu, "Выход", () -> frame.exitApplication()); // Используем метод exitApplication

            menuBar.add(menu);
        }

        private void addTestMenuItem(JMenu parentMenu, String text, Runnable action) {
            JMenuItem item = new JMenuItem(text);
            item.addActionListener((event) -> {
                if (text.equals("Выход")) {
                    frame.exitApplication();
                } else {
                    action.run();
                }
            });
            parentMenu.add(item);
        }
    }

    private JMenuBar generateMenuBar() {
        return new MenuBuilder(this).build();
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // just ignore
        }
    }
}
