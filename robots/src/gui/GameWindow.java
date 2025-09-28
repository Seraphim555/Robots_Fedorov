package gui;

import javax.swing.JInternalFrame;

public class GameWindow extends JInternalFrame {
    private GameVisualizer m_visualizer;

    public GameWindow(RobotModel robotModel) {
        super("Крутой робот", true, true, true, true);
        m_visualizer = new GameVisualizer(robotModel);
        add(m_visualizer);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        setSize(400, 400);
        setLocation(50, 50);
    }
}