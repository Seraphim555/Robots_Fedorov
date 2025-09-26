package gui;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class WindowConfig {
    private static final String CONFIG_DIR = System.getProperty("user.home") +
            File.separator + ".robots_app";
    private static final String CONFIG_FILE = CONFIG_DIR +
            File.separator + "window_config.properties";

    public static void saveWindowState(JFrame frame, List<JInternalFrame> internalFrames) { // Изменили на List<>
        try {

            File configDir = new File(CONFIG_DIR);
            if (!configDir.exists()) {
                configDir.mkdirs();
            }

            Properties props = new Properties();

            props.setProperty("main.x", String.valueOf(frame.getX()));
            props.setProperty("main.y", String.valueOf(frame.getY()));
            props.setProperty("main.width", String.valueOf(frame.getWidth()));
            props.setProperty("main.height", String.valueOf(frame.getHeight()));
            props.setProperty("main.maximized",
                    String.valueOf((frame.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0));

            for (int i = 0; i < internalFrames.size(); i++) {
                JInternalFrame internalFrame = internalFrames.get(i);
                props.setProperty("internal." + i + ".x", String.valueOf(internalFrame.getX()));
                props.setProperty("internal." + i + ".y", String.valueOf(internalFrame.getY()));
                props.setProperty("internal." + i + ".width", String.valueOf(internalFrame.getWidth()));
                props.setProperty("internal." + i + ".height", String.valueOf(internalFrame.getHeight()));
                props.setProperty("internal." + i + ".icon", String.valueOf(internalFrame.isIcon()));
                props.setProperty("internal." + i + ".maximized", String.valueOf(internalFrame.isMaximum()));
            }

            try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
                props.store(fos, "Window configuration for Robots application");
            }

        } catch (IOException e) {
            System.err.println("Ошибка при сохранении конфигурации окон: " + e.getMessage()); // Заменили Logger
        }
    }

    public static WindowState loadWindowState() {
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            return null;
        }

        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            Properties props = new Properties();
            props.load(fis);

            WindowState state = new WindowState();

            state.mainX = Integer.parseInt(props.getProperty("main.x", "50"));
            state.mainY = Integer.parseInt(props.getProperty("main.y", "50"));
            state.mainWidth = Integer.parseInt(props.getProperty("main.width", "800"));
            state.mainHeight = Integer.parseInt(props.getProperty("main.height", "600"));
            state.mainMaximized = Boolean.parseBoolean(props.getProperty("main.maximized", "false"));

            state.internalFrames = new ArrayList<InternalFrameState>();
            int i = 0;
            while (props.containsKey("internal." + i + ".x")) {
                InternalFrameState ifs = new InternalFrameState();
                ifs.x = Integer.parseInt(props.getProperty("internal." + i + ".x"));
                ifs.y = Integer.parseInt(props.getProperty("internal." + i + ".y"));
                ifs.width = Integer.parseInt(props.getProperty("internal." + i + ".width"));
                ifs.height = Integer.parseInt(props.getProperty("internal." + i + ".height"));
                ifs.icon = Boolean.parseBoolean(props.getProperty("internal." + i + ".icon"));
                ifs.maximized = Boolean.parseBoolean(props.getProperty("internal." + i + ".maximized"));
                state.internalFrames.add(ifs);
                i++;
            }

            return state;

        } catch (IOException | NumberFormatException e) {
            System.err.println("Ошибка при загрузке конфигурации окон: " + e.getMessage()); // Заменили Logger
            return null;
        }
    }

    public static class WindowState {
        public int mainX, mainY, mainWidth, mainHeight;
        public boolean mainMaximized;
        public List<InternalFrameState> internalFrames; // Убрали параметризацию
    }

    public static class InternalFrameState {
        public int x, y, width, height;
        public boolean icon, maximized;
    }
}