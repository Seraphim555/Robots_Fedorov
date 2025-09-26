package gui;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class GameWindow extends JInternalFrame {
    private final RobotModel model;
    private final GameVisualizer visualizer;

    public GameWindow() {
        super("Игровое поле", true, true, true, true);

        this.model = new RobotModel();
        this.visualizer = new GameVisualizer(model);

        JPanel panel = new JPanel();
        panel.add(visualizer);
        getContentPane().add(panel);
        pack();
    }


    public RobotModel getModel() {
        return model;
    }

    public GameVisualizer getVisualizer() {
        return visualizer;
    }
}