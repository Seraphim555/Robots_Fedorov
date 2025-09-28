package gui;

import java.util.Observable;
import java.util.concurrent.CopyOnWriteArrayList;

public class RobotModel extends Observable {
    private volatile double robotPositionX = 100;
    private volatile double robotPositionY = 100;
    private volatile double robotDirection = 0;

    private volatile int targetPositionX = 150;
    private volatile int targetPositionY = 100;

    private static final double MAX_VELOCITY = 0.1;
    private static final double MAX_ANGULAR_VELOCITY = 0.001;

    private final CopyOnWriteArrayList<RobotModelListener> listeners = new CopyOnWriteArrayList<>();

    public interface RobotModelListener {
        void onRobotUpdated(double x, double y, double direction);
        void onTargetUpdated(int x, int y);
    }

    public void addListener(RobotModelListener listener) {
        listeners.add(listener);
    }

    public void removeListener(RobotModelListener listener) {
        listeners.remove(listener);
    }

    private void notifyRobotUpdated() {
        for (RobotModelListener listener : listeners) {
            listener.onRobotUpdated(robotPositionX, robotPositionY, robotDirection);
        }
        setChanged();
        notifyObservers();
    }

    private void notifyTargetUpdated() {
        for (RobotModelListener listener : listeners) {
            listener.onTargetUpdated(targetPositionX, targetPositionY);
        }
        setChanged();
        notifyObservers();
    }

    public double getRobotPositionX() { return robotPositionX; }
    public double getRobotPositionY() { return robotPositionY; }
    public double getRobotDirection() { return robotDirection; }
    public int getTargetPositionX() { return targetPositionX; }
    public int getTargetPositionY() { return targetPositionY; }

    public void setTargetPosition(int x, int y) {
        this.targetPositionX = x;
        this.targetPositionY = y;
        notifyTargetUpdated();
    }

    public void setRobotPosition(double x, double y, double direction) {
        this.robotPositionX = x;
        this.robotPositionY = y;
        this.robotDirection = direction;
        notifyRobotUpdated();
    }

    public void updateModel() {
        double distance = distance(targetPositionX, targetPositionY, robotPositionX, robotPositionY);
        if (distance < 0.5) {
            return; // Цель достигнута
        }

        double velocity = MAX_VELOCITY;
        double angleToTarget = angleTo(robotPositionX, robotPositionY, targetPositionX, targetPositionY);

        // Вычисляем разницу углов с учетом круговой природы
        double angleDiff = angleToTarget - robotDirection;

        // Нормализуем разницу в диапазон [-PI, PI]
        angleDiff = normalizeAngle(angleDiff);

        // Пропорциональное управление: чем больше отклонение, тем быстрее поворот
        double angularVelocity = applyLimits(angleDiff * 2, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);

        // Если смотрим почти в противоположную сторону, двигаемся медленнее
        if (Math.abs(angleDiff) > Math.PI / 2) {
            velocity *= 0.3;
        }

        moveRobot(velocity, angularVelocity, 10);
    }

    private static double normalizeAngle(double angle) {
        while (angle > Math.PI) angle -= 2 * Math.PI;
        while (angle < -Math.PI) angle += 2 * Math.PI;
        return angle;
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);

        double newX = robotPositionX + velocity / angularVelocity *
                (Math.sin(robotDirection + angularVelocity * duration) - Math.sin(robotDirection));
        if (!Double.isFinite(newX)) {
            newX = robotPositionX + velocity * duration * Math.cos(robotDirection);
        }

        double newY = robotPositionY - velocity / angularVelocity *
                (Math.cos(robotDirection + angularVelocity * duration) - Math.cos(robotDirection));
        if (!Double.isFinite(newY)) {
            newY = robotPositionY + velocity * duration * Math.sin(robotDirection);
        }

        robotPositionX = newX;
        robotPositionY = newY;
        robotDirection = asNormalizedRadians(robotDirection + angularVelocity * duration);

        notifyRobotUpdated();
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    private static double applyLimits(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    private static double asNormalizedRadians(double angle) {
        while (angle < 0) angle += 2 * Math.PI;
        while (angle >= 2 * Math.PI) angle -= 2 * Math.PI;
        return angle;
    }
}