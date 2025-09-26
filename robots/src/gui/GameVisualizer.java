package gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class GameVisualizer extends JPanel implements Observer, RobotModel.RobotModelListener {
    private final RobotModel model;
    private volatile double currentRobotX;
    private volatile double currentRobotY;
    private volatile double currentRobotDirection;
    private volatile int currentTargetX;
    private volatile int currentTargetY;

    private final Timer timer;

    public GameVisualizer(RobotModel model) {
        this.model = model;
        this.model.addObserver(this);
        this.model.addListener(this);

        this.currentRobotX = model.getRobotPositionX();
        this.currentRobotY = model.getRobotPositionY();
        this.currentRobotDirection = model.getRobotDirection();
        this.currentTargetX = model.getTargetPositionX();
        this.currentTargetY = model.getTargetPositionY();

        this.timer = new Timer("events generator", true);


        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 50);


        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                model.updateModel();
            }
        }, 0, 10);


        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                model.setTargetPosition(e.getPoint().x, e.getPoint().y);
            }
        });

        setDoubleBuffered(true);
    }


    @Override
    public void update(Observable o, Object arg) {

    }


    @Override
    public void onRobotUpdated(double x, double y, double direction) {
        this.currentRobotX = x;
        this.currentRobotY = y;
        this.currentRobotDirection = direction;
    }

    @Override
    public void onTargetUpdated(int x, int y) {
        this.currentTargetX = x;
        this.currentTargetY = y;
        repaint();
    }

    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    private static int round(double value) {
        return (int)(value + 0.5);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        drawRobot(g2d, round(currentRobotX), round(currentRobotY), currentRobotDirection);
        drawTarget(g2d, currentTargetX, currentTargetY);
    }


    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        AffineTransform t = AffineTransform.getRotateInstance(direction, x, y);
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, x, y, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, x + 10, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x + 10, y, 5, 5);
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }


    public double getCurrentRobotX() { return currentRobotX; }
    public double getCurrentRobotY() { return currentRobotY; }
    public double getCurrentRobotDirection() { return currentRobotDirection; }
    public int getCurrentTargetX() { return currentTargetX; }
    public int getCurrentTargetY() { return currentTargetY; }
}