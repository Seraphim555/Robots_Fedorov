package gui;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class CoordinatesWindow extends JInternalFrame implements Observer, RobotModel.RobotModelListener {
    private final RobotModel model;
    private final JLabel xLabel;
    private final JLabel yLabel;
    private final JLabel directionLabel;
    private final JLabel targetXLabel;
    private final JLabel targetYLabel;

    public CoordinatesWindow(RobotModel model) {
        super("Координаты робота", true, true, true, true);
        this.model = model;
        this.model.addObserver(this);
        this.model.addListener(this);

        // Создаем компоненты для отображения координат
        xLabel = new JLabel("X: " + model.getRobotPositionX());
        yLabel = new JLabel("Y: " + model.getRobotPositionY());
        directionLabel = new JLabel("Направление: " + String.format("%.2f", Math.toDegrees(model.getRobotDirection())) + "°");
        targetXLabel = new JLabel("Цель X: " + model.getTargetPositionX());
        targetYLabel = new JLabel("Цель Y: " + model.getTargetPositionY());

        // Настраиваем layout
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Добавляем компоненты
        panel.add(xLabel);
        panel.add(yLabel);
        panel.add(directionLabel);
        panel.add(targetXLabel);
        panel.add(targetYLabel);

        // Настраиваем шрифт для лучшей читаемости
        Font labelFont = new Font("Monospaced", Font.PLAIN, 12);
        xLabel.setFont(labelFont);
        yLabel.setFont(labelFont);
        directionLabel.setFont(labelFont);
        targetXLabel.setFont(labelFont);
        targetYLabel.setFont(labelFont);

        getContentPane().add(panel);
        setSize(250, 200);
        setLocation(470, 10); // Позиция рядом с окном логов
    }

    @Override
    public void update(Observable o, Object arg) {
        // Обновляем через Observer (если используется)
        updateCoordinates();
    }

    @Override
    public void onRobotUpdated(double x, double y, double direction) {
        // Обновляем координаты робота
        SwingUtilities.invokeLater(() -> {
            xLabel.setText(String.format("X: %.2f", x));
            yLabel.setText(String.format("Y: %.2f", y));
            directionLabel.setText(String.format("Направление: %.2f°", Math.toDegrees(direction)));
        });
    }

    @Override
    public void onTargetUpdated(int x, int y) {
        // Обновляем координаты цели
        SwingUtilities.invokeLater(() -> {
            targetXLabel.setText("Цель X: " + x);
            targetYLabel.setText("Цель Y: " + y);
        });
    }

    private void updateCoordinates() {
        // Полная синхронизация всех координат
        SwingUtilities.invokeLater(() -> {
            xLabel.setText(String.format("X: %.2f", model.getRobotPositionX()));
            yLabel.setText(String.format("Y: %.2f", model.getRobotPositionY()));
            directionLabel.setText(String.format("Направление: %.2f°", Math.toDegrees(model.getRobotDirection())));
            targetXLabel.setText("Цель X: " + model.getTargetPositionX());
            targetYLabel.setText("Цель Y: " + model.getTargetPositionY());
        });
    }
}